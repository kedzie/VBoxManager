package com.kedzie.vbox.metrics;

import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.kedzie.vbox.api.IPerformanceMetric;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MetricViewGL extends GLSurfaceView implements GLSurfaceView.Renderer, DataThread.Renderer {

	public MetricViewGL(Context context, int count, int period, int max, String []metrics, IPerformanceMetric pm) {
		super(context);
		setRenderer(this);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.5f, .5f, .5f, 1.f);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0,0,width,height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void addData(Map<String, Point2F> d) {
	}

	@Override
	public String[] getMetrics() {
		return null;
	}
}
