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

public class MetricViewGL extends BaseMetricView implements GLSurfaceView.Renderer {
	private static final float LINE_WIDTH = 6f;
	private static final String TAG = MetricViewGL.class.getSimpleName();
	
	private Map<String, FloatBuffer> buffers = new HashMap<String, FloatBuffer>();
	private Map<String, FloatBuffer> nBuffers = new HashMap<String, FloatBuffer>();
	
	public MetricViewGL(Context context, GLSurfaceView view, int max, String []metrics, IPerformanceMetric pm) {
		super(context, max, metrics, pm);
		view.setRenderer(this);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.5f, .5f, .5f, 1.f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
//		gl.glEnable(GL10.GL_LIGHTING);
//		gl.glEnable(GL10.GL_LIGHT0);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[] { 1f, 1f, -1f }, 0);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, new float[] { 1f, 1f, 1f,1f }, 0);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, new float[] { 1f, 1f, 1f,1f }, 0);
//		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, new float[] { 0f,0f, 0f,1f }, 0);
		
		for(String metric : _metrics) {
			ByteBuffer vbb = ByteBuffer.allocateDirect(_count*2*4); 
			vbb.order(ByteOrder.nativeOrder());
			buffers.put(metric, vbb.asFloatBuffer()); 
			ByteBuffer nbb = ByteBuffer.allocateDirect(_count*3*4); 
			nbb.order(ByteOrder.nativeOrder());
			nBuffers.put(metric, nbb.asFloatBuffer()); 
		}
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
		for(String metric : _metrics) {
			if(count>_count) {		//if count increased re-allocate buffers
				Log.i(TAG, "Reallocating buffers");
				ByteBuffer vbb = ByteBuffer.allocateDirect(count*2*4); 
				vbb.order(ByteOrder.nativeOrder());
				buffers.put(metric, vbb.asFloatBuffer()); 
				ByteBuffer nbb = ByteBuffer.allocateDirect(_count*3*4); 
				nbb.order(ByteOrder.nativeOrder());
				nBuffers.put(metric, nbb.asFloatBuffer()); 
			}
		}
		super.setMetricPreferences(period, count);
	}
	
	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		long timestamp= System.currentTimeMillis();
		for(String metric : _metrics) {
			FloatBuffer buf = buffers.get(metric); //vertex buffer
			FloatBuffer nBuf = nBuffers.get(metric); //normal buffer
			buf.position(0);
			for(Point2F p : data.get(metric)) {
				p.x=getXPixelFromTimestamp(p.timestamp, timestamp);
				buf.put(p.x); 
				buf.put(p.scaledY);
				Log.v(TAG, "Datapoint: " + p);
				nBuf.put(1f);
				nBuf.put(1f);
				nBuf.put(1f);
			}
			buf.position(0);
			nBuf.position(0);
		}
		gl.glClearColor(1,1,1,1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0f, 0f, -1f);
		
//		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, new float[] { 1f, 1f, 1f, 1f }, 0);
//		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, new float[] { 0f, 0f, 1f, 1f }, 0);
		
		gl.glLineWidth(LINE_WIDTH);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		for(String metric : _metrics) {
			int c = VBoxApplication.getColor(getContext(), metric.replace('/', '_'));
//			gl.glMaterialxv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, getColorv(metric), 0);
			gl.glColor4x(c&0x00ff0000, c&0x0000ff00, c&0x000000ff, c&0xff000000);
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffers.get(metric));
//			gl.glNormalPointer(GL10.GL_FLOAT, 3, nBuffers.get(metric));
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
		return new int[] { c&0x00ff0000, c&0x0000ff00, c&0x000000ff, c&0xff000000 };
	}
}
