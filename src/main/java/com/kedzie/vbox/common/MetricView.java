package com.kedzie.vbox.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricView extends View {
	private static final String TAG = "vbox."+MetricView.class.getSimpleName();
	
	private long max;
	private int count;
	private int period;
	private String[] metrics;
	private IPerformanceMetric baseMetric;
	private Map<String, Map<String, Object>> data;
	private int hStep;
	private double vStep;
	private double vStepGrid;
	private Rect bounds;
	private Paint textPaint = new Paint(), bgPaint = new Paint(), borderPaint = new Paint(), metricPaint = new Paint(), gridPaint = new Paint();
	private Map<String, Integer> metricColor = new HashMap<String, Integer>();
	
	public MetricView(Context context, AttributeSet as) {
		super(context, as);
		bgPaint.setARGB(255, 255, 255, 255);
		bgPaint.setStyle(Style.FILL);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setARGB(1, 0, 0, 255);
		borderPaint.setAntiAlias(true);
		borderPaint.setStrokeWidth(2.0f);
		textPaint.setARGB(255, 0, 0, 0);
		textPaint.setAntiAlias(true);
		textPaint.setDither(true);
		textPaint.setTextSize(16.0f);
		gridPaint.setAntiAlias(true);
		gridPaint.setStrokeWidth(1.5f);
		gridPaint.setARGB(100, 0,0,0);
		metricPaint.setARGB(255, 0, 0, 255);
		metricPaint.setStrokeWidth(2.0f);
		metricPaint.setStrokeJoin(Join.BEVEL);
		metricPaint.setStrokeCap(Cap.SQUARE);
		metricPaint.setAntiAlias(true);
		metricPaint.setShadowLayer(4.0f, 2.0f, 2.0f, 0xdd000000);
	}
	
	/* @see android.view.View#onSizeChanged(int, int, int, int) */
	@Override 
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.i(TAG, "OnSizeChanged("+w+"," + h + ", " + oldw + ", " + oldh + ")");
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public int getColor(String name) {
		if(!metricColor.containsKey(name)) 	metricColor.put(name, getResources().getColor(getResources().getIdentifier(name.replace("/", "_"), "color", getContext().getPackageName())) );
		return metricColor.get(name);
	}
	
	public void init( int count, int period, long max, String []metrics, IPerformanceMetric pm) {
		this.max=max;
		this.count=count;
		this.metrics=metrics;
		this.period=period;
		this.baseMetric=pm;
	}

	public void setData(Map<String, Map<String, Object>> data) {
		this.data = data;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onDraw(Canvas canvas) {
		bounds = canvas.getClipBounds();
		canvas.drawRect(bounds, bgPaint);
		canvas.drawRect(bounds, borderPaint);
		hStep = bounds.width()/count;
		vStep = bounds.height()/(double)max;
		vStepGrid = bounds.height()/(double)count;
		for(int i=1; i<count; i+=4) {
			int horiz = bounds.left+i*hStep, vert = bounds.bottom-(int)(i*vStepGrid);
			canvas.drawLine(horiz, bounds.bottom, horiz, bounds.top, gridPaint);
			canvas.drawText(""+(count-i)*period, horiz, bounds.bottom-20, textPaint);
			canvas.drawLine(bounds.left, vert, bounds.right, vert, gridPaint);
			canvas.drawText(max*(double)i/(double)count+""+baseMetric.getUnit(), bounds.left+20, vert, textPaint);
		}
		if(data==null) return;
		for(String mName : metrics) {
			metricPaint.setColor(getColor(mName));
			int x = bounds.left, y = bounds.bottom;
			for(Integer val : ((List<Integer>)data.get(mName).get("val"))) {
				int nY = bounds.bottom-(int)(val*vStep);
				canvas.drawLine(x, y, x+hStep, nY, metricPaint);
				x+=hStep; y = nY;
			}
		}
	}
}
