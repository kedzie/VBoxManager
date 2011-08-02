package com.kedzie.vbox.machine;

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

public class MetricView extends View {
	private static final String TAG = "vbox."+MetricView.class.getSimpleName();
	
	private int max;
	private String[] metrics;
	private int count;
	private int period;
	private Map<String, Map<String, Object>> data;
	
	private Paint textPaint = new Paint();
	private Paint bgPaint = new Paint();
	private Map<String,Paint> metricPaint = new HashMap<String, Paint>();
	private Paint gridPaint = new Paint();
	
	public MetricView(Context context, AttributeSet as) {
		super(context, as);
	}
	
	public void init( int count, int period, int max, String []metrics) {
		this.max=max;
		this.count=count;
		this.metrics=metrics;
		this.period=period;
		bgPaint.setARGB(255, 25, 25, 25);
		textPaint.setARGB(255,255,255,255);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(16.0f);
		gridPaint.setAntiAlias(true);
		gridPaint.setStrokeWidth(1.5f);
		gridPaint.setStyle(Style.STROKE);
		gridPaint.setARGB(100, 255,255,255);
		Paint p = new Paint();
		p.setARGB(255, 0, 0, 255);
		p.setStrokeWidth(2.0f);
		p.setStrokeJoin(Join.BEVEL);
		p.setStrokeCap(Cap.SQUARE);
		p.setAntiAlias(true);
		metricPaint.put("CPU/Load/Kernel", p);
		metricPaint.put("Guest/CPU/Load/Kernel", p);
		p = new Paint();
		p.setStrokeWidth(2.0f);
		p.setStrokeJoin(Join.BEVEL);
		p.setStrokeCap(Cap.SQUARE);
		p.setAntiAlias(true);
		p.setARGB(255, 0, 255, 0);
		metricPaint.put("CPU/Load/User", p);
		metricPaint.put("Guest/CPU/Load/User", p);
		p = new Paint();
		p.setStrokeWidth(2.0f);
		p.setStrokeJoin(Join.BEVEL);
		p.setStrokeCap(Cap.SQUARE);
		p.setAntiAlias(true);
		p.setARGB(255, 255, 0, 0);
		metricPaint.put("RAM/Usage/Used", p);
		metricPaint.put("Guest/RAM/Usage/Used", p);
	}

	public void setData(Map<String, Map<String, Object>> data) {
		for(String k: data.keySet())
			Log.i(TAG, "Got Data: " + k);
		this.data = data;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Rect rect = new Rect(0,0, canvas.getWidth(), canvas.getHeight());
		canvas.drawRect(rect, bgPaint);
		canvas.drawText(max+"", 20, 20, textPaint);
		int hStep = canvas.getWidth()/count;
		double vStep = canvas.getHeight()/(double)max;

		double vStepGrid = canvas.getHeight()/(double)count;
		for(int i=1; i<count; i++) {
			canvas.drawLine(i*hStep, 0, i*hStep, canvas.getHeight(), gridPaint);
			canvas.drawLine(0, (int)(i*vStepGrid), canvas.getWidth(), (int)(i*vStepGrid), gridPaint);
			canvas.drawText(max*i/count+"", 20, (int)(i*vStepGrid), textPaint);
		}
		
		if(data==null) return;
		for(String mName : metrics) {
			Map<String, Object> metric = data.get(mName);
			int x = 0, y = canvas.getHeight();
			List<Integer> vals = (List<Integer>)metric.get("val");
			for(Integer val : vals) {
				int nX = x+hStep;
				int nY = canvas.getHeight()-(int)(val*vStep);
				canvas.drawLine(x, y, nX, nY, metricPaint.get(mName));
				x = nX;
				y = nY;
			}
		}
	}
}
