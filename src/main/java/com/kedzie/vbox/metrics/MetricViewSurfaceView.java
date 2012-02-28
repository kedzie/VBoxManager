package com.kedzie.vbox.metrics;

import java.util.HashMap;
import java.util.Iterator;
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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricViewSurfaceView  extends SurfaceView implements SurfaceHolder.Callback, DataThread.Renderer {
	private static final String TAG = MetricViewSurfaceView.class.getSimpleName();
	
	private RenderThread _thread;
	
	public MetricViewSurfaceView(Context ctx, int count, int period, int max, String []metrics, IPerformanceMetric pm) {
		super(ctx);
		getHolder().addCallback(this);
		_thread = new RenderThread(getHolder(), count, period, max, metrics, pm);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		_thread.setSize(width,height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean done = false;
        _thread._running= false;
        while (!done) {
            try {
                _thread.join();
                done = true;
            } catch (InterruptedException e) { }
        }
	}
	
	public String[] getMetrics() {
		return _thread._metrics;
	}
	
	@Override
	public void addData(Map<String, Point2F> d) {
		_thread.addData(d);
	}
	
	public void pause() {
		_thread._on=false;
	}
	
	public void resume() {
		_thread._on=true;
	}
	
	class RenderThread extends Thread {
		boolean _running=true;
		boolean _on=true;
		private SurfaceHolder _surfaceHolder;
		private int _max;
		private int _count;
		private int _period;
		private int _width;
		private String[] _metrics;
		private IPerformanceMetric _baseMetric;
		private int hStep;
		private double vStep;
		private Rect bounds;
		private Paint textPaint = new Paint(), bgPaint = new Paint(), borderPaint = new Paint(), metricPaint = new Paint(), gridPaint = new Paint();
		private Map<String, Integer> metricColor = new HashMap<String, Integer>();
		private Map<String, LinkedList<Point2F>> data;
		/** timestamp of last rendering */
		private double pixelsPerSecond; 
		
		public RenderThread(SurfaceHolder holder, int count, int period, int max, String []metrics, IPerformanceMetric pm) {
			super("Metric Render");
			_surfaceHolder = holder;
			_max=max;
			_count=count;
			_metrics=metrics;
			_period=period;
			_baseMetric=pm;
			bgPaint.setARGB(255, 255, 255, 255);
			bgPaint.setStyle(Style.FILL);
			borderPaint.setStyle(Style.STROKE);
			borderPaint.setARGB(1, 0, 0, 255);
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
		
		public void setSize(int w, int h) {
			synchronized (_surfaceHolder) {
				_width=w;
				Log.i(TAG, "OnSizeChanged("+w+"," + h + ")");
				hStep = w/_count;
				vStep = h/(double)_max;
				bounds=null;
				pixelsPerSecond =hStep/_period;
			}
		}
		
		private int getColor(String name) {
			if(!metricColor.containsKey(name)) 	{
				Resources res = getContext().getResources();
				metricColor.put(name, res.getColor(res.getIdentifier(name.replace("/", "_"), "color", getContext().getPackageName())) );
			}
			return metricColor.get(name);
		}
		
		protected void update() {
			long timestamp= System.currentTimeMillis();
			for(LinkedList<Point2F> dataPoints : data.values()) {
				for(Point2F p : dataPoints)
					p.x=getXPixelFromTimestamp(p.timestamp, timestamp);
			}
		}
		
		private int getXPixelFromTimestamp(long stamp, long current) {
			return _width-(int)(((current-stamp)/1000.d)*pixelsPerSecond);
		}
		
		protected void onDraw(Canvas canvas) {
			if(bounds==null) 
				bounds = canvas.getClipBounds();
			canvas.drawRect(bounds, bgPaint);
			canvas.drawRect(bounds, borderPaint);
			
			for(int i=0; i>=_count; i+=2) {	//horizontal grid
				int horiz = bounds.left+i*hStep;
				canvas.drawLine(horiz, bounds.bottom, horiz, bounds.top, gridPaint);
				canvas.drawText(i*_period+"sec", horiz, bounds.bottom-20, textPaint);
			}
			canvas.drawText(_max+""+_baseMetric.getUnit(), bounds.left+20, bounds.bottom-(float)(_max*vStep), textPaint);

			for(String metric : _metrics) {
				if(!data.containsKey(metric) || data.get(metric).isEmpty()) continue;
				metricPaint.setColor(getColor(metric));
				Iterator<Point2F> it=data.get(metric).iterator();
				Point2F p = it.next();
				while(it.hasNext() ) {
					Point2F nP = it.next();
					canvas.drawLine(bounds.left+(int)p.x, bounds.bottom-(int)(p.y*vStep), bounds.left+(int)nP.x, bounds.bottom-(int)(nP.y*vStep), metricPaint);
					p = nP;
				}
			}
		}
		
		public void addData(Map<String, Point2F> d) {
			synchronized (_surfaceHolder) {
				for(String metric : _metrics){
					data.get(metric).addLast( d.get(metric) );
					if(data.get(metric).size()>_count) 
						data.get(metric).removeFirst();
				}
			}
		}
		
		@Override
		public void run() {
			while(_running) {
				Canvas c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        if (_on) update();
                        onDraw(c);
                    }
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
			}
		}
	}
}
