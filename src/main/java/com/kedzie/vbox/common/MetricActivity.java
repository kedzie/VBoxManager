package com.kedzie.vbox.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VBoxSvc;

public class MetricActivity extends Activity  implements OnGestureListener {
	private static final String TAG = MetricActivity.class.getSimpleName();
	public static final String INTENT_OBJECT = "object",  INTENT_RAM_AVAILABLE = "ram_available", INTENT_RAM_METRICS="ram_metrics", INTENT_CPU_METRICS="cpu_metrics";
	
	private MetricView cpuView, ramView;
	private MetricThread _thread;
	private GestureDetector detector;
	private ViewFlipper vf;
	private Animation _anim_LeftIn;
	private Animation _anim_RightIn;
	private Animation _anim_LeftOut;
	private Animation _anim_RightOut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_anim_LeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
		_anim_RightIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		_anim_LeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
		_anim_RightOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
		setContentView(R.layout.flipper);
		detector = new GestureDetector(this, this);
		vf = (ViewFlipper)findViewById(R.id.viewflipper);
	    vf.setAnimateFirstView(true);
		((ImageButton)findViewById(R.id.left_button)).setOnClickListener(new OnClickListener() { public void onClick(View v) { flingLeft(); } });
		((ImageButton)findViewById(R.id.right_button)).setOnClickListener(new OnClickListener() { public void onClick(View v) { flingRight(); } });
		
		VBoxSvc vmgr = getIntent().getParcelableExtra("vmgr");
		setTitle(getIntent().getStringExtra("title"));
		String []cpuMetrics = getIntent().getStringArrayExtra(INTENT_CPU_METRICS);
		String [] ramMetrics = getIntent().getStringArrayExtra(INTENT_RAM_METRICS);
		String object = getIntent().getStringExtra(INTENT_OBJECT);
		int ramAvailable = getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0);
		try {
			SurfaceView sv1 = (SurfaceView)findViewById(R.id.cpu_metrics);
			SurfaceView sv2 = (SurfaceView)findViewById(R.id.ram_metrics);
			cpuView = new MetricView(this, sv1.getHolder(), getApp().getCount(), getApp().getPeriod(), 100000, cpuMetrics, vmgr.getVBox().getPerformanceCollector().getMetrics(cpuMetrics, object).get(0));
			ramView = new MetricView(this, sv2.getHolder(), getApp().getCount(), getApp().getPeriod(), ramAvailable*1000, ramMetrics, vmgr.getVBox().getPerformanceCollector().getMetrics(ramMetrics, object).get(0));
			_thread = new MetricThread(vmgr, object, getApp().getCount(), getApp().getPeriod(), cpuView, ramView);
			_thread.start();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	protected void onDestroy() {
		boolean done = false;
        _thread._running= false;
        while (!done) {
            try {
                _thread.join();
                done = true;
            } catch (InterruptedException e) { }
        }
		super.onDestroy();
	}
	
	protected void flingLeft() {
		vf.setInAnimation(_anim_RightIn);
		vf.setOutAnimation(_anim_LeftOut);
		vf.showPrevious();
	}
	
	protected void flingRight() {
		vf.setInAnimation(_anim_LeftIn);
		vf.setOutAnimation(_anim_RightOut);
		vf.showNext();
	}
	
	@Override 
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if( e1.getX() > e2.getX() ) //left fling
			flingLeft();
		else
			flingRight();
		return true;
	}
	
	@Override public boolean onTouchEvent(MotionEvent event) { return detector.onTouchEvent(event); }
	@Override public boolean onDown(MotionEvent e) { return false; }
	@Override public void onShowPress(MotionEvent e) {}
	@Override public boolean onSingleTapUp(MotionEvent e) { return false; }
	@Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
	@Override public void onLongPress(MotionEvent e) {}
	
	public VBoxApplication getApp() {  
		return (VBoxApplication)getApplication();  
	}
	
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
			Toast.makeText(this, "Portrait", Toast.LENGTH_SHORT).show();
		} else if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE) {
			Toast.makeText(this, "Landscape", Toast.LENGTH_SHORT).show();
		}
	}



	class MetricThread extends Thread {
		boolean _running=true;
		private VBoxSvc _vmgr;
		private MetricView []_views;
		private String _object;
		private int _period;
		
		public MetricThread(VBoxSvc vmgr, String object, int count, int period, MetricView...views){
			super("Metric Data");
			_vmgr=vmgr;
			_object=object;
			_period=period;
			_views=views;
		}
		
		@Override
		public void run() {
			while(_running) {
				try {
					Map<String, Point2D> newData = new HashMap<String, Point2D>();
					Map<String, Map<String, Object>> data = _vmgr.queryMetricsData(_object, 1, _period, "*:");
					for(String metric : data.keySet()){
						@SuppressWarnings("unchecked")
						int newValue  = ((List<Integer>)data.get(metric).get("val")).get(0);
						newData.put(metric, new Point2D( 0, newValue, System.currentTimeMillis()+1000 ));
					}
					for(MetricView v : _views)
						v.addData(newData);
					Thread.sleep(_period*1000);
				} catch (Exception e) {
					Log.e(TAG, "", e);
				} 
			}
		}
	}
}
