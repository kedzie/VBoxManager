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

import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricView extends View {
	private static final String TAG = "vbox."+MetricView.class.getSimpleName();
	
	private long max;
	private String[] metrics;
	private int count;
	private int period;
    IPerformanceMetric baseMetric;
	private Map<String, Map<String, Object>> data;
	
	private Paint textPaint = new Paint();
	private Paint bgPaint = new Paint();
	private Paint borderPaint = new Paint();
	private Paint metricPaint = new Paint();
	private Map<String,Integer> metricColor = new HashMap<String, Integer>();
	private Paint gridPaint = new Paint();
	
	public MetricView(Context context, AttributeSet as) {
		super(context, as);
		bgPaint.setARGB(255, 255, 255, 255);
		bgPaint.setStyle(Style.FILL);
		
		borderPaint = new Paint();
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
		metricColor.put("CPU/Load/Kernel", 0xff0000ff);
		metricColor.put("Guest/CPU/Load/Kernel", 0xff0000ff);
		metricColor.put("CPU/Load/User", 0xff00ff00);
		metricColor.put("Guest/CPU/Load/User", 0xff00ff00);
		metricColor.put("RAM/Usage/Used", 0xffff0000);
		metricColor.put("Guest/RAM/Usage/Used", 0xffff0000);
	}
	
	public void init( int count, int period, long max, String []metrics, IPerformanceMetric baseMetric) {
		this.max=max;
		this.count=count;
		this.metrics=metrics;
		this.period=period;
		this.baseMetric = baseMetric;
	}

	public void setData(Map<String, Map<String, Object>> data) {
		this.data = data;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onDraw(Canvas canvas) {
		Rect rect = canvas.getClipBounds();
		canvas.drawRect(rect, bgPaint);
		canvas.drawRect(rect, borderPaint);
	
		int hStep = rect.width()/count;
		double vStep = rect.height()/(double)max;

		textPaint.setTextSize(16.0f);
		double vStepGrid = rect.height()/(double)count;
		for(int i=1; i<count; i++) {
			canvas.drawLine(i*hStep, rect.bottom, i*hStep, rect.top, gridPaint);
			canvas.drawLine(rect.left, (int)(i*vStepGrid), rect.right, (int)(i*vStepGrid), gridPaint);
			canvas.drawText(max*(double)i/(double)count+"", 20, (int)(i*vStepGrid), textPaint);
		}
		
		if(data==null) return;
		for(String mName : metrics) {
			Paint p = metricPaint.get(mName);
			Map<String, Object> metric = data.get(mName);
			int x = rect.left, y = rect.bottom;
			List<Integer> vals = (List<Integer>)metric.get("val");
			for(Integer val : vals) {
				int nX = x+hStep;
				int nY = rect.bottom-(int)(val*vStep);
				canvas.drawLine(x, y, nX, nY, p);
				x = nX;
				y = nY;
			}
		}
	}
}
