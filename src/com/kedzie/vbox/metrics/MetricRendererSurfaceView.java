package com.kedzie.vbox.metrics;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.task.LoopingThread;

public class MetricRendererSurfaceView  extends MetricRenderer implements SurfaceHolder.Callback {
	private RenderThread _thread;
	
	public MetricRendererSurfaceView(Context ctx, SurfaceView view, int max, String []metrics) {
		super(ctx, max, metrics);
		view.getHolder().addCallback(this);
		_thread = new RenderThread(view.getHolder());
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		super.setSize(width,height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		_thread.quit();
        _thread=null;
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
		
		public RenderThread(SurfaceHolder holder) {
			super("Metric Render");
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

		protected void onDraw(Canvas canvas) {
			if(bounds==null) 
				bounds = canvas.getClipBounds();
			canvas.drawRect(bounds, bgPaint);
			canvas.drawRect(bounds, borderPaint);
			
			for(int i=0; i<=_count; i+=5) {	//horizontal grid
				int horiz = bounds.right-i*hStep;
				canvas.drawLine(horiz, bounds.bottom, horiz, bounds.top, gridPaint);
				canvas.drawText(i*_period+"sec", horiz, bounds.bottom-20, textPaint);
			}

			for(String metric : _metrics) {
				if(!data.containsKey(metric) || data.get(metric).isEmpty()) continue;
				metricPaint.setColor(VBoxApplication.getColor(getContext(), metric.replace('/', '_')));
				Iterator<Point2F> it=data.get(metric).iterator();
				Point2F p = it.next();
				while(it.hasNext() ) {
					Point2F nP = it.next();
					canvas.drawLine(bounds.left+(int)p.x, bounds.bottom-(int)p.scaledY, bounds.left+(int)nP.x, bounds.bottom-(int)nP.scaledY, metricPaint);
					p = nP;
				}
			}
		}
		
		@Override
		public void loop() {
			Canvas c = null;
            try {
                c = _surfaceHolder.lockCanvas(null);
                synchronized (MetricRendererSurfaceView.this){
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
