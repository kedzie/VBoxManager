package com.kedzie.vbox.metrics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricViewGL extends GLSurfaceView implements GLSurfaceView.Renderer, DataThread.Renderer {

	private FloatBuffer triangleVB;
	
	public MetricViewGL(Context context, int count, int period, int max, String []metrics, IPerformanceMetric pm) {
		super(context);
		setRenderer(this);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.5f, .5f, .5f, 1.f);
		
		float triangleCoords[] = {
				-0.5f, -0.25f, 0,
				0.5f, -0.25f, 0,
				0.0f,  0.559016994f, 0
		};
		ByteBuffer vbb = ByteBuffer.allocateDirect(triangleCoords.length * 4); 
		vbb.order(ByteOrder.nativeOrder());
		triangleVB = vbb.asFloatBuffer();  
		triangleVB.put(triangleCoords);    
		triangleVB.position(0);  
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0,0,width,height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glColor4f(0.63671875f, 0.76953125f, 0.22265625f, 0.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleVB);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
	}

	@Override
	public void addData(Map<String, Point2F> d) {
	}

	@Override
	public String[] getMetrics() {
		return null;
	}

	@Override
	public void setMetricPreferences(int period, int count) {
		// TODO Auto-generated method stub
		
	}
}
