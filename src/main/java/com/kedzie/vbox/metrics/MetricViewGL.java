package com.kedzie.vbox.metrics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricViewGL extends BaseMetricView implements GLSurfaceView.Renderer {
	private static final float LINE_WIDTH = 8f;

	private static final String TAG = MetricViewGL.class.getSimpleName();
	
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
	private Map<String, LinkedList<Point2F>> data= new HashMap<String, LinkedList<Point2F>>();
	private Map<String, FloatBuffer> buffers = new HashMap<String, FloatBuffer>();
	
	public MetricViewGL(Context context, GLSurfaceView view, int max, String []metrics, IPerformanceMetric pm) {
		super(context);
		_max=max;
		_count=VBoxApplication.getCountPreference(context);
		_metrics=metrics;
		_period=VBoxApplication.getPeriodPreference(context);
		_baseMetric=pm;
		view.setRenderer(this);
		for(String metric : _metrics)
			data.put(metric, new LinkedList<Point2F>());
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.5f, .5f, .5f, 1.f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		for(String metric : _metrics) {
			ByteBuffer vbb = ByteBuffer.allocateDirect(_count*2*4); 
			vbb.order(ByteOrder.nativeOrder());
			buffers.put(metric, vbb.asFloatBuffer()); 
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		_width=width;
		hStep = width/_count;
		vStep = (float)height/(float)_max;
		Log.i(TAG, "OnSizeChanged("+width+"," + height + ") vStep: " + vStep + " hStep="+hStep);
		setMetricPreferences(_period,_count);
		for(String metric : _metrics) {  //REscale the scaled data set
			for(Point2F p : data.get(metric))
				p.scaledY = (float)(p.y*vStep);
		}
		
		gl.glViewport(0,0,width,height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity(); 
		gl.glOrthof(0f, (float)_width, 0f, (float)height, 1f, 3f);
	}

	@Override
	public synchronized void setMetricPreferences(int period, int count) {
			Log.i(TAG, "Metric Preferences Changed ("+period+"," + count + ")");
			for(String metric : _metrics) {
				while(data.get(metric).size()>count) //if count is lowered, dump unecessary data points
					data.get(metric).removeFirst();
				if(count>_count) {		//if count increased re-allocate buffers
					Log.i(TAG, "Reallocating buffers");
					ByteBuffer vbb = ByteBuffer.allocateDirect(count*2*4); 
					vbb.order(ByteOrder.nativeOrder());
					buffers.put(metric, vbb.asFloatBuffer()); 
				}
			}
			_period = period;
			_count = count;
			hStep = _width/_count;
			pixelsPerSecond =((float)hStep/(float)_period);
	}
	
	@Override
	public synchronized void addData(Map<String, Point2F> d) {
		for(String metric : _metrics){
			d.get(metric).scaledY = (float)(d.get(metric).y*vStep);
			data.get(metric).addLast( d.get(metric) );
			if(data.get(metric).size()>_count)
				data.get(metric).removeFirst();
		}
	}
	
	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		long timestamp= System.currentTimeMillis();
		for(String metric : _metrics) {
			FloatBuffer buf = buffers.get(metric);
			buf.position(0);
			for(Point2F p : data.get(metric)) {
				p.x=MetricView.getXPixelFromTimestamp(_width, pixelsPerSecond, p.timestamp, timestamp);
				buf.put(p.x); 
				buf.put(p.scaledY);
				Log.v(TAG, "Datapoint: " + p);
			}
			buf.position(0);
		}
		gl.glColor4x(1,1,1,1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0f, 0f, -1f);
		
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, new float[] { 1f, 1f, 1f, 1f }, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, new float[] { 0f, 1f, 0f, 1f }, 0);
		
		gl.glLineWidth(LINE_WIDTH);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		for(String metric : _metrics) {
			int c = VBoxApplication.getColor(getContext(), metric);
			gl.glMaterialxv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, getColorv(metric),0);
			gl.glColor4x(c&0x00ff0000, c&0x0000ff00, c&0x0000ff, c&0xff000000);
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffers.get(metric));
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, data.get(metric).size());//TODO change count
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	/**
	 * @param name name of color resource
	 * @return 4byte color - 0xAARRGGBB
	 */
	private int[] getColorv(String name) {
		int c = VBoxApplication.getColor(getContext(), name.replace("/", "_"));
		return new int[] { c&0x00ff0000, c&0x0000ff00, c&0x0000ff, c&0xff000000 };
	}
	
	@Override
	public String[] getMetrics() { return _metrics; }

	@Override
	public void pause() {  }

	@Override
	public void resume() {  }
}
