package com.kedzie.vbox.metrics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.kedzie.vbox.VBoxApplication;

public class MetricRenderer extends View {
    private static String TAG = "MetricRenderer";
	/** # of vertical grid lines */
	private static final int GRID_LINES_VERT = 10;
	/** # of horizontal grid lines */
	private static final int GRID_LINES_HORIZ = 10;
	/** Maximum Y Value */
	protected int _max;
	/** # of data points */
	protected int _count;
	/** Time interval between datapoints */
	protected int _period;
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
	private Paint textPaint = new Paint(), bgPaint = new Paint(), borderPaint = new Paint(), metricPaint = new Paint(), gridPaint = new Paint(), metricFill=new Paint(), _editTextPaint = new Paint();
	private Path path = new Path();
	private Path hGridPath = new Path();
	private Path vGridPath = new Path();
	private Bitmap _gridBitmap;
	
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
		
		_editTextPaint.setColor(0xFF000000);
		_editTextPaint.setAntiAlias(true);
        _editTextPaint.setTextSize(24.0f);
		
		gridPaint.setColor(gridColor);
		gridPaint.setAntiAlias(true);
		gridPaint.setStrokeWidth(1.5f);
		gridPaint.setStyle(Style.STROKE);
		gridPaint.setPathEffect(new DashPathEffect(new float[] { 5, 15, 4, 8 }, 0));
		
		metricPaint.setStrokeWidth(4.0f);
		metricPaint.setStrokeJoin(Join.MITER);
		metricPaint.setStrokeCap(Cap.ROUND);
		metricPaint.setAntiAlias(true);
		metricPaint.setStyle(Style.STROKE);
		
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
		vStep = (float)getHeight()/(float)_max;
		hStep = getWidth()/_count;
		Log.i(TAG, String.format("Set Metric Preferences period/count:  %1$d/%2$d\thStep/vStep: %3$d,%4$.2f",period, count, hStep, vStep ));
		if(getWidth()>0 && getHeight()>0) {
		    _gridBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		    Canvas gridCanvas = new Canvas(_gridBitmap);
		    drawGrid(gridCanvas);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(!isInEditMode() && _count>0 && _period>0) 
		    setMetricPrefs(_count, _period);
		bounds = new Rect();
	}
	
	public synchronized void setQuery(Map<String, MetricQuery> q) {
		_unit=q.get(_metrics[0]).unit;
		_data=q;
		postInvalidate();
	}
	
	private void drawGrid(Canvas canvas) {
	    canvas.drawRect(bounds, bgPaint);
        canvas.drawRect(bounds, borderPaint);
	    int hGridStep = _count/GRID_LINES_HORIZ*_period;
        int hPixelStep = hGridStep*hStep;
        int horiz = bounds.right;
        int seconds = 0;
        hGridPath.reset();
        for(int i=1; i<=GRID_LINES_HORIZ; i++) {
            horiz -= hPixelStep;
            seconds += hGridStep;
            hGridPath.moveTo(horiz, bounds.bottom);
            hGridPath.lineTo(horiz, bounds.top);
            canvas.drawText(seconds+" sec", horiz, bounds.bottom-20, textPaint);
        }
        canvas.drawPath(hGridPath, gridPaint);
        
        int yVal = 0;
        int vert = bounds.bottom;
        int vValStep = _max/GRID_LINES_VERT;
        int vPixelStep = (int)(vValStep*vStep);
        vGridPath.reset();
        for( int i=1; i<=GRID_LINES_VERT; i++) {
            yVal += vValStep;
            vert -= vPixelStep;
            vGridPath.moveTo(bounds.left, vert);
            vGridPath.lineTo(bounds.right, vert);
            canvas.drawText(yVal+_unit, bounds.left+10, vert+4, textPaint);
        }
        canvas.drawPath(vGridPath, gridPaint);
	}
	
	@Override
	protected synchronized  void onDraw(Canvas canvas) {
		if(bounds.width()==0)
			canvas.getClipBounds(bounds);
	    
	    if(isInEditMode()) { 
	        _editTextPaint.setTextSize(20f);
	        canvas.drawText("Edit Mode", 100, 100, _editTextPaint);
	        return;
	    }
		if(_gridBitmap==null)
		    canvas.drawBitmap(_gridBitmap, 0, 0, null);
		else
		    drawGrid(canvas);
		
		for(String metric : _metrics) {
			if(!_data.containsKey(metric)) continue;
			
			String colorName = metric.replace('/', '_').replace("Guest_","");
			metricPaint.setColor(VBoxApplication.getInstance().getColor(getContext(), colorName));
			metricFill.setColor(VBoxApplication.getInstance().getColor(getContext(), colorName+"_Fill"));
			
			int[] data = _data.get(metric).values;

			int x=bounds.right;
			path.reset();
			path.moveTo(x, bounds.bottom-(int)(data[data.length-1]*vStep));
			for(int i=data.length-2; i>=0; i--) {
			    x-=hStep;
                path.lineTo(x, bounds.bottom-(int)(data[i]*vStep));
			}
			canvas.drawPath(path, metricPaint);
			//close the path for fill
			path.lineTo(x, bounds.bottom);
			path.lineTo(bounds.right, bounds.bottom);
			path.close();
			canvas.drawPath(path, metricFill);
		}
	}
}
