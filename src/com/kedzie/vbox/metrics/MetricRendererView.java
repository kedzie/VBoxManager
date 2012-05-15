package com.kedzie.vbox.metrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class MetricRendererView extends View {

	class Renderer extends MetricRenderer {

		public Renderer(Context context, int max, String[] metrics) {
			super(context, max, metrics);
		}
		
	}
	
	private Renderer _renderer;
	
	public MetricRendererView(Context context, int max, String []metrics) {
		super(context);
		_renderer = new Renderer(context, max, metrics);
	}

	public MetricRenderer getRenderer() {
		return _renderer;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint bg = new Paint();
		bg.setARGB(255, 255, 255, 255);
		bg.setStyle(Style.FILL);
		canvas.drawRect(canvas.getClipBounds(), bg);
	}

}
