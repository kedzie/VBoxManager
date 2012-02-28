package com.kedzie.vbox.metrics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricViewSimple extends View implements DataThread.Renderer {
	protected String[] _metrics;
	protected static final int VERT_GRID_INTERVAL=500000;
	protected int _max;
	protected int _count;
	protected int _period;
	protected IPerformanceMetric _baseMetric;
	protected int hStep;
	protected double vStep;
	protected Rect bounds;
	protected Paint textPaint = new Paint(), bgPaint = new Paint(), borderPaint = new Paint(), metricPaint = new Paint(), gridPaint = new Paint();
	protected Map<String, Integer> metricColor = new HashMap<String, Integer>();
	protected Map<String, LinkedList<Point2F>> data;
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			invalidate();
		}
	};

	public MetricViewSimple(Context context, int count, int period, int max, String []metrics, IPerformanceMetric pm) {
		super(context);
		_metrics=metrics;
		_max=max;
		_count=count;
		_metrics=metrics;
		_period=period;
		_baseMetric=pm;
		bgPaint.setARGB(255, 255, 255, 255);
		bgPaint.setStyle(Style.FILL);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setARGB(255, 0, 0, 255);
		borderPaint.setAntiAlias(true);
		borderPaint.setStrokeWidth(2.0f);
		textPaint.setARGB(255, 0, 0, 0);
		textPaint.setAntiAlias(true);
		textPaint.setDither(true);
		textPaint.setTextSize(18.0f);
		gridPaint.setAntiAlias(true);
		gridPaint.setStrokeWidth(1.5f);
		gridPaint.setARGB(100, 0,0,0);
		metricPaint.setARGB(255, 0, 0, 255);
		metricPaint.setStrokeWidth(2.0f);
		metricPaint.setStrokeJoin(Join.BEVEL);
		metricPaint.setStrokeCap(Cap.SQUARE);
		metricPaint.setAntiAlias(true);
		metricPaint.setShadowLayer(4.0f, 2.0f, 2.0f, 0xdd000000);
		data = new HashMap<String, LinkedList<Point2F>>();
		for(String metric : _metrics)
			data.put(metric, new LinkedList<Point2F>());
	}

	@Override
	public String[] getMetrics() {
		return _metrics;
	}

	@Override
	public void addData(Map<String, Point2F> d) {
		synchronized (this) {
			for(String metric : _metrics){
				data.get(metric).addLast( d.get(metric) );
				if(data.get(metric).size()>_count) 
					data.get(metric).removeFirst();
			}
			handler.obtainMessage().sendToTarget();
		}
	}

	int getColor(String name) {
		if(!metricColor.containsKey(name)) 	{
			Resources res = getContext().getResources();
			metricColor.put(name, res.getColor(res.getIdentifier(name.replace("/", "_"), "color", getContext().getPackageName())) );
		}
		return metricColor.get(name);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(bounds==null) {
			bounds = canvas.getClipBounds();
//			hStep = bounds.width()/_count;
//			vStep = bounds.height()/(double)_max;
		}
		canvas.drawRect(bounds, bgPaint);
		canvas.drawRect(bounds, borderPaint);
//		for(int i=0; i<=_count; i-=5) {	//horizontal grid
//			int horiz = bounds.left+i*hStep;
//			canvas.drawLine(horiz, bounds.bottom, horiz, bounds.top, gridPaint);
//			canvas.drawText(i*_period+"sec", horiz, bounds.bottom-20, textPaint);
//		}
//		canvas.drawText(_max+""+_baseMetric.getUnit(), bounds.left+20, bounds.bottom-(float)(_max*vStep), textPaint);
//
//		for(String metric : _metrics) {
//			if(!data.containsKey(metric) || data.get(metric).isEmpty()) continue;
//			metricPaint.setColor(getColor(metric));
//			Iterator<Point2F> it=data.get(metric).iterator();
//			Point2F p = it.next();
//			while(it.hasNext() ) {
//				Point2F nP = it.next();
//				canvas.drawLine(bounds.left+(int)p.x, bounds.bottom-(int)(p.y*vStep), bounds.left+(int)nP.x, bounds.bottom-(int)(nP.y*vStep), metricPaint);
//				p = nP;
//			}
//		}
	}
}
