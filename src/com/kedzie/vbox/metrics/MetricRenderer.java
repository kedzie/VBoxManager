package com.kedzie.vbox.metrics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.Log;
import android.view.View;

import com.kedzie.vbox.VBoxApplication;

public class MetricRenderer extends View {
	/**  */
	private static final int GRID_LINES_VERT = 10;

	/**  */
	private static final int GRID_LINES_HORIZ = 10;

	private static String TAG = MetricRenderer.class.getSimpleName();
	
	/** Maximum Y Value */
	protected int _max;
	/** # of data points */
	protected int _count;
	/** Time interval between datapoints */
	protected int _period;
	/** view size (pixels) */
	protected int _width, _height;
	/** Metric names to render */
	protected String[] _metrics;
	/** pixels/period */
	protected int hStep;
	/** pixels/unit */
	protected double vStep;
	/** Metric data */
	protected Map<String, MetricQuery> _data = new HashMap<String, MetricQuery>();
	protected String _unit;
	
	private Rect bounds = new Rect();
	private Paint textPaint = new Paint(), bgPaint = new Paint(), borderPaint = new Paint(), metricPaint = new Paint(), gridPaint = new Paint(), metricFill=new Paint();
	private Path path = new Path();
	
	public MetricRenderer(Context context, int bgColor, int gridColor, int textColor, int borderColor) {
		super(context);
		bgPaint.setStyle(Style.FILL);
		bgPaint.setColor(bgColor);
		
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setColor(borderColor);
		borderPaint.setAntiAlias(true);
		borderPaint.setStrokeWidth(4.0f);
		
		textPaint.setColor(textColor);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(18.0f);
		
		gridPaint.setColor(gridColor);
		gridPaint.setAntiAlias(true);
		gridPaint.setStrokeWidth(1.5f);

		metricPaint.setStrokeWidth(2.0f);
		metricPaint.setStrokeJoin(Join.MITER);
		metricPaint.setStrokeCap(Cap.ROUND);
		metricPaint.setAntiAlias(true);
		metricPaint.setShadowLayer(4.0f, 2.0f, 2.0f, 0x96000000);
		
		metricFill.setStyle(Style.FILL);
	}

	public void init( int max, String []metrics) {
		Log.i(TAG, String.format("Metrics initialized: Max=%1$d  Metrics=%2$s", max, Arrays.asList(metrics).toString() ));
		_max=max;
		_metrics=metrics;
	}
	
	public void setMetricPrefs(int count, int period) {
		_count=count;
		_period=period;
		vStep = (float)_height/(float)_max;
		hStep = _width/_count;
		Log.i(TAG, String.format("Set Metric Preferences period/count:  %1$d/%2$d\thStep/vStep: %3$d,%4$.2f",period, count, hStep, vStep ));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i(TAG, "OnSizeChanged("+getWidth()+"," + getHeight() + ")");
		_width=getWidth();
		_height=getHeight();
		setMetricPrefs(_count, _period);
	}
	
	public synchronized void setQuery(Map<String, MetricQuery> q) {
		_unit=q.get(_metrics[0]).unit;
		_data=q;
		postInvalidate();
	}
	
	@Override
	protected synchronized  void onDraw(Canvas canvas) {
		canvas.getClipBounds(bounds);
		canvas.drawRect(bounds, bgPaint);
		canvas.drawRect(bounds, borderPaint);
		if(this.isInEditMode())
			return;
		
		int hGridStep = _count/GRID_LINES_HORIZ*_period;
		int hPixelStep = hGridStep*hStep;
		int horiz = bounds.right;
		int seconds = 0;
		for(int i=1; i<=GRID_LINES_HORIZ; i++) {	//horizontal grid
			horiz -= hPixelStep;
			seconds += hGridStep;
			canvas.drawLine(horiz, bounds.bottom, horiz, bounds.top, gridPaint);
			canvas.drawText(seconds+" sec", horiz, bounds.bottom-20, textPaint);
		}
		
		int yVal = 0;
		int vert = bounds.bottom;
		int vValStep = _max/GRID_LINES_VERT;
		int vPixelStep = (int)(vValStep*vStep);
		for( int i=1; i<=GRID_LINES_VERT; i++) {
			yVal += vValStep;
			vert -= vPixelStep;
			canvas.drawLine(bounds.left, vert, bounds.right, vert, gridPaint);
			canvas.drawText(yVal+_unit, bounds.left+10, vert+4, textPaint);
		}

		for(String metric : _metrics) {
			if(!_data.containsKey(metric)) continue;
			
			String colorName = metric.replace('/', '_').replace("Guest_","");
			metricPaint.setColor(VBoxApplication.getColor(getContext(), colorName));
			metricFill.setColor(VBoxApplication.getColor(getContext(), colorName+"_Fill"));
			
			int[] data = _data.get(metric).values;
			int prevX=bounds.right;
			int prevY=bounds.bottom-(int)(data[data.length-1]*vStep);
			for(int i=data.length-2; i>=0; i--) {
				int x=prevX-hStep;
				int y = bounds.bottom-(int)(data[i]*vStep);
				path.reset();
				path.moveTo(prevX, bounds.bottom);
				path.lineTo(prevX, prevY);
				path.lineTo(x, y);
				path.lineTo(x, bounds.bottom);
				path.close();
				canvas.drawPath(path, metricFill);
				canvas.drawLine(prevX, prevY, x, y, metricPaint);
				prevX=x;prevY=y;
			}
		}
	}
}
