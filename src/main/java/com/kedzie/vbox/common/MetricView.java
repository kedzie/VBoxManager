package com.kedzie.vbox.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricView  implements SurfaceHolder.Callback {
	private static final String TAG = MetricView.class.getSimpleName();
	
	private RenderThread _thread;
	private Context context;
	private String[] _metrics;
	
	public MetricView(Context ctx, SurfaceHolder holder, int count, int period, int max, String []metrics, IPerformanceMetric pm) {
		this.context=ctx;
		holder.addCallback(this);
		this._metrics=metrics;
		_thread = new RenderThread(holder, count, period, max, metrics, pm);
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
		return _metrics;
	}
	
	public void addData(Map<String, Map<String, Object>> data) {
		_thread.addData(data);
	}
	
	public void pause() {
		_thread._on=false;
	}
	
	public void resume() {
		_thread._on=true;
	}
	
	class RenderThread extends Thread {
		private static final int VERT_GRID_INTERVAL=50000;
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
		private Map<String, LinkedList<Point2D>> data;
		/** timestamp of last rendering */
		private double pixelsPerSecond; 
		
		public RenderThread(SurfaceHolder holder, int count, int period, int max, String []metrics, IPerformanceMetric pm) {
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
			data = new HashMap<String, LinkedList<Point2D>>();
			for(String metric : _metrics)
					data.put(metric, new LinkedList<Point2D>());
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
				Resources res = context.getResources();
				metricColor.put(name, res.getColor(res.getIdentifier(name.replace("/", "_"), "color", context.getPackageName())) );
			}
			return metricColor.get(name);
		}
		
		protected void update() {
			long timestamp= System.currentTimeMillis();
			for(LinkedList<Point2D> dataPoints : data.values()) {
				for(Point2D p : dataPoints)
					p.x=getXPixelFromTimestamp(p.timestamp, timestamp);
			}
		}
		
		protected void onDraw(Canvas canvas) {
			if(bounds==null) 
				bounds = canvas.getClipBounds();
			canvas.drawRect(bounds, bgPaint);
			canvas.drawRect(bounds, borderPaint);
			
			for(int i=_count; i>=0; i-=5) {	//horizontal grid
				int horiz = bounds.left+i*hStep;
				canvas.drawLine(horiz, bounds.bottom, horiz, bounds.top, gridPaint);
				canvas.drawText(i*_period+"sec", horiz, bounds.bottom-20, textPaint);
			}
			for(int i=0; i<_max; i+=VERT_GRID_INTERVAL) {	//vertical grid
				int vert = bounds.bottom-(int)(i*vStep);
				canvas.drawLine(bounds.left, vert, bounds.right, vert, gridPaint);
				canvas.drawText(i+""+_baseMetric.getUnit(), bounds.left+20, vert, textPaint);
			}
			canvas.drawText(_max+""+_baseMetric.getUnit(), bounds.left+20, bounds.bottom-(float)(_max*vStep), textPaint);

			for(String metric : _metrics) {
				if(!data.containsKey(metric) || data.get(metric).isEmpty()) continue;
				metricPaint.setColor(getColor(metric));
				Iterator<Point2D> it=data.get(metric).iterator();
				Point2D p = it.next();
				while(it.hasNext() ) {
					Point2D nP = it.next();
					canvas.drawLine(bounds.left+(int)p.x, bounds.bottom-(int)p.y, bounds.left+(int)nP.x, bounds.bottom-(int)nP.y, metricPaint);
					p = nP;
				}
			}
		}
		
		public void addData(Map<String, Map<String, Object>> d) {
			synchronized (_surfaceHolder) {
				for(String metric : _metrics){
					@SuppressWarnings("unchecked")
					int newValue  = ((List<Integer>)d.get(metric).get("val")).get(0);
					Log.i(TAG, "Added data point: " + newValue);
					data.get(metric).addLast(new Point2D( 0, newValue*vStep, System.currentTimeMillis()+1000 ));
					if(data.get(metric).size()>_count) 
						data.get(metric).removeFirst();
				}
			}
		}
		
		private int getXPixelFromTimestamp(long stamp, long current) {
			return _width-(int)(((current-stamp)/1000.d)*pixelsPerSecond);
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
