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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.kedzie.vbox.VBoxApplication;

public class MetricRenderer extends View {
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
	
	private Rect bounds = new Rect();
	private Paint textPaint = new Paint(), bgPaint = new Paint(), borderPaint = new Paint(), metricPaint = new Paint(), gridPaint = new Paint(), metricFill=new Paint();
	private Path path = new Path();
	
	private Handler _handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "Invalidate");
			MetricRenderer.this.invalidate();
		}
	};
	
	public MetricRenderer(Context context, int bgColor, int gridColor, int textColor, int borderColor) {
		super(context);
		bgPaint.setStyle(Style.FILL);
		bgPaint.setColor(bgColor);
		
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setColor(borderColor);
		borderPaint.setAntiAlias(true);
		borderPaint.setStrokeWidth(2.0f);
		
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
		metricPaint.setShadowLayer(4.0f, 2.0f, 2.0f, 0xdd000000);
		
		metricFill.setStyle(Style.FILL);
	}

	public void init( int max, String []metrics) {
		Log.i(TAG, String.format("Metrics initialized: Max=%1$d  Metrics=%s", max, Arrays.asList(metrics).toString() ));
		_max=max;
		_metrics=metrics;
	}
	
	public void setMetricPrefs(int count, int period) {
		_count=count;
		_period=period;
		vStep = (float)_height/(float)_max;
		hStep = _width/_count;
		Log.i(TAG, String.format("Set Metric Preferences period/count:  %1$d/%1$d\thStep/vStep: %3$d,%4$d",period, count, hStep, vStep ));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i(TAG, "OnSizeChanged("+getWidth()+"," + getHeight() + ")");
		_width=getWidth();
		_height=getHeight();
	}
	
	public synchronized void setQuery(Map<String, MetricQuery> q) {
		Log.i(TAG, "Received Metric data");
		_data=q;
		_handler.obtainMessage().sendToTarget();
	}
	
	@Override
	protected synchronized  void onDraw(Canvas canvas) {
		canvas.getClipBounds(bounds);
		canvas.drawRect(bounds, bgPaint);
		canvas.drawRect(bounds, borderPaint);
		
		if(this.isInEditMode())
			return;
		
		int horiz = bounds.right;
		for(int i=0; i<=_count; i+=5) {	//horizontal grid
			canvas.drawLine(horiz, bounds.bottom, horiz, bounds.top, gridPaint);
			canvas.drawText(i*_period+"sec", horiz, bounds.bottom-20, textPaint);
			horiz-=i*hStep;
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
