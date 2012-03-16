package com.kedzie.vbox.metrics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.task.LoopingThread;

public class MetricViewSurfaceView  extends SurfaceView implements SurfaceHolder.Callback, DataThread.Renderer {
	private static final String TAG = MetricViewSurfaceView.class.getSimpleName();
	
	private RenderThread _thread;
	
	public MetricViewSurfaceView(Context ctx, int max, String []metrics, IPerformanceMetric pm) {
		super(ctx);
		getHolder().addCallback(this);
		_thread = new RenderThread(getHolder(), max, metrics, pm);
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
		_thread.quit();
        _thread=null;
	}
	
	public String[] getMetrics() {
		return _thread._metrics;
	}
	
	@Override
	public void addData(Map<String, Point2F> d) {
		_thread.addData(d);
	}
	
	@Override
	public void setMetricPreferences(int period, int count) {
		_thread.setMetricPreferences(period,count);
	}
	
	@Override
	public void pause() {
		_thread._on=false;
	}
	
	@Override
	public void resume() {
		_thread._on=true;
	}
	
	class RenderThread extends LoopingThread {
		boolean _on=true;
		private SurfaceHolder _surfaceHolder;
		private Rect bounds;
		private Paint textPaint = new Paint(), bgPaint = new Paint(), borderPaint = new Paint(), metricPaint = new Paint(), gridPaint = new Paint();
		private Map<String, LinkedList<Point2F>> data= new HashMap<String, LinkedList<Point2F>>();
		/** Maximum Y Value */
		private int _max;
		/** # of data points */
		private int _count;
		/** Time interval between datapoints */
		private int _period;
		/** width in pixels */
		private int _width;
		/** Metric names to render */
		private String[] _metrics;
		private IPerformanceMetric _baseMetric;
		/** pixels/period */
		private int hStep;
		/** pixels/unit */
		private double vStep;
		/** timestamp of last rendering */
		private double pixelsPerSecond; 
		
		public RenderThread(SurfaceHolder holder, int max, String []metrics, IPerformanceMetric pm) {
			super("Metric Render");
			_max=max;
			_count=VBoxApplication.getCountPreference(getContext());
			_metrics=metrics;
			_period=VBoxApplication.getPeriodPreference(getContext());;
			_baseMetric=pm;
			for(String metric : _metrics)
				data.put(metric, new LinkedList<Point2F>());
			_surfaceHolder = holder;
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
		}
		
		public void setSize(int w, int h) {
			Log.i(TAG, "OnSizeChanged("+w+"," + h + ")");
			bounds=null;
			_width=w;
			vStep = (float)h/(float)_max;
			setMetricPreferences(_period, _count);
			for(String metric : _metrics) {  //REscale the scaled data set
				for(Point2F p : data.get(metric))
					p.scaledY = (float)(p.y*vStep);
			}
		}

		public void setMetricPreferences(int period, int count) {
			synchronized (_surfaceHolder) {
				Log.i(TAG, "Metric Preferences Changed ("+period+"," + count + ")");
				_period = period;
				_count = count;
				hStep = _width/_count;
				pixelsPerSecond =hStep/_period;
				for(String metric : _metrics) {
					while(data.get(metric).size()>_count)  //if count is lowered, dump unecessary data points
						data.get(metric).removeFirst();
				}
			}
		}

		public void addData(Map<String, Point2F> d) {
			synchronized (_surfaceHolder) {
				for(String metric : _metrics){
					d.get(metric).scaledY = (float)(d.get(metric).y*vStep);
					data.get(metric).addLast( d.get(metric) );
					if(data.get(metric).size()>_count)
						data.get(metric).removeFirst();
				}
			}
		}

		protected void update() {
			long timestamp= System.currentTimeMillis();
			for(LinkedList<Point2F> dataPoints : data.values()) {
				for(Point2F p : dataPoints) {
					p.x=MetricView.getXPixelFromTimestamp(_width, pixelsPerSecond, p.timestamp, timestamp);
				}
			}
		}
		
		protected void onDraw(Canvas canvas) {
			if(bounds==null) 
				bounds = canvas.getClipBounds();
			canvas.drawRect(bounds, bgPaint);
			canvas.drawRect(bounds, borderPaint);
			
			for(int i=0; i<=_count; i+=5) {	//horizontal grid
				int horiz = bounds.left+i*hStep;
				canvas.drawLine(horiz, bounds.bottom, horiz, bounds.top, gridPaint);
				canvas.drawText((_count-i)*_period+"sec", horiz, bounds.bottom-20, textPaint);
			}
			canvas.drawText(_max+""+_baseMetric.getUnit(), bounds.left+20, bounds.bottom-(float)(_max*vStep), textPaint);

			for(String metric : _metrics) {
				if(!data.containsKey(metric) || data.get(metric).isEmpty()) continue;
				metricPaint.setColor(VBoxApplication.getColor(getContext(), metric.replace("/", "_")));
				Iterator<Point2F> it=data.get(metric).iterator();
				Point2F p = it.next();
				while(it.hasNext() ) {
					Point2F nP = it.next();
					Log.v(TAG, "Datapoint: " + nP);
					canvas.drawLine(bounds.left+(int)p.x, bounds.bottom-(int)(p.scaledY), bounds.left+(int)nP.x, bounds.bottom-(int)(nP.y*vStep), metricPaint);
					p = nP;
				}
			}
		}
		
		@Override
		public void loop() {
			Canvas c = null;
            try {
                c = _surfaceHolder.lockCanvas(null);
                synchronized (_surfaceHolder) {
                    if (_on) {
                    	update();
                    	onDraw(c);
                    }
                }
            } finally {
                if (c != null) {
                    _surfaceHolder.unlockCanvasAndPost(c);
                }
            }
		}
	}
}
