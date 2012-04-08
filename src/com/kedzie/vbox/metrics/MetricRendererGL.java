package com.kedzie.vbox.metrics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricRendererGL extends MetricRenderer implements GLSurfaceView.Renderer {
	private static final float LINE_WIDTH = 6f;
	private static final String TAG = MetricRendererGL.class.getSimpleName();
	
	private Map<String, FloatBuffer> buffers = new HashMap<String, FloatBuffer>();
	private FloatBuffer timeGridBuffer;
	private int numTimeGridLines;
	
	public MetricRendererGL(Context context, GLSurfaceView view, int max, String []metrics, IPerformanceMetric pm) {
		super(context, max, metrics, pm);
		view.setRenderer(this);
	}
	
	/**
	 * Allocate a {@link FloatBuffer} of specified size
	 * @param size # of elements
	 * @return {@link FloatBuffer}
	 */
	private FloatBuffer allocateFloatBuffer(int size) {
		ByteBuffer bb = ByteBuffer.allocateDirect(size*4); 
		bb.order(ByteOrder.nativeOrder());
		return  bb.asFloatBuffer(); 
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(1f, 1f, 1f, 1.f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		for(String metric : _metrics)
			buffers.put(metric, allocateFloatBuffer((_count+MetricRenderer.EXTRA_POINTS)*2)); 
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.setSize(width, height);
		gl.glViewport(0,0,width,height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity(); 
		gl.glOrthof(0f, (float)_width, 0f, (float)height, 1f, 3f);
	}

	@Override
	public synchronized void setMetricPreferences(int period, int count) {
		if(count>_count) {		//if count increased re-allocate buffers
			Log.i(TAG, "Reallocating buffers");
			for(String metric : _metrics)
				buffers.put(metric, allocateFloatBuffer((_count+MetricRenderer.EXTRA_POINTS)*2)); 
		}
		super.setMetricPreferences(period, count);
		allocateGridBuffers();
	}
	
	private void allocateGridBuffers() {
		numTimeGridLines = _count*_period/5+1;
		timeGridBuffer = allocateFloatBuffer(numTimeGridLines*4); 
		timeGridBuffer.position(0);
		for(int i=0; i<=_count*_period; i+=5) {	
			int horiz = (int)(i*pixelsPerSecond);
			timeGridBuffer.put(horiz);
			timeGridBuffer.put(0f);
			timeGridBuffer.put(horiz);
			timeGridBuffer.put(_height);
		}
	}
	
	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		update();
		for(String metric : _metrics) {
			FloatBuffer buf = buffers.get(metric); //vertex buffer
			buf.position(0);
			for(Point2F p : data.get(metric)) {
				buf.put(p.x); 
				buf.put(p.scaledY);
			}
			buf.position(0);
		}
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0f, 0f, -1f);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		gl.glLineWidth(2f);
		gl.glColor4f(0, 0, 0, 1);
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0,timeGridBuffer);
		gl.glDrawArrays(GL10.GL_LINES, 0, numTimeGridLines*2);
		
		gl.glLineWidth(LINE_WIDTH);
		for(String metric : _metrics) {
			int c = VBoxApplication.getColor(getContext(), metric.replace('/', '_'));
			gl.glColor4f(((c>>4)&0x000000ff)/255f, ((c>>2)&0x000000ff)/255f, (c&0x000000ff)/255f, ((c>>6)&0x000000ff)/255f);
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffers.get(metric));
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, data.get(metric).size());
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
