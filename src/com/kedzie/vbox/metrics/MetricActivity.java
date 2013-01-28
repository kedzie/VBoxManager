package com.kedzie.vbox.metrics;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Activity to view metric graphs for Virtual Machine or Host
 * @author Marek Kedzierski
 * @apiviz.stereotype activity
 */
public class MetricActivity extends BaseActivity  {
	private static final int REQUEST_CODE_PREFS = 1;
	static final String ACTION_METRIC_QUERY = "com.kedzie.vbox.METRIC_QUERY";
	public static final String INTENT_TITLE="t",INTENT_OBJECT = "o",
			INTENT_RAM_AVAILABLE = "ra", INTENT_RAM_METRICS="rm",
			INTENT_CPU_METRICS="cm";

	private ViewPager _flipper;
	private MetricView cpuV, ramV;
	private DataThread _thread;
	private VBoxSvc _vmgr;
	private String _object;
	private int _ramAvailable;
	private int _count;
	private int _period;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setTitle(getIntent().getStringExtra(INTENT_TITLE));

		_vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		_object = getIntent().getStringExtra(INTENT_OBJECT);
		_ramAvailable = getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0);
		_count = Utils.getIntPreference(this, MetricPreferencesActivity.COUNT);
        _period = Utils.getIntPreference(this, MetricPreferencesActivity.PERIOD);
        
        View content = LayoutInflater.from(this).inflate(R.layout.metrics, null);
        cpuV = (MetricView) content.findViewById(R.id.cpu_metrics);
        cpuV.init(100, getIntent().getStringArrayExtra(INTENT_CPU_METRICS));
        cpuV.setMetricPrefs(_count, _period);
        ramV = (MetricView) content.findViewById(R.id.ram_metrics);
        ramV.init( _ramAvailable*1000, getIntent().getStringArrayExtra(INTENT_RAM_METRICS));
        ramV.setMetricPrefs(_count, _period);
        
        //for large devices show both metric graphs on same page
        if( (getResources().getConfiguration().screenLayout&Configuration.SCREENLAYOUT_SIZE_MASK) >=Configuration.SCREENLAYOUT_SIZE_LARGE) {
            setContentView(content);
        } else { //for smaller devices show a single metric graph on the page
            MetricViewPagerAdapter adapter = new MetricViewPagerAdapter();
            adapter.addView(cpuV);
            adapter.addView(ramV);
            _flipper = new ViewPager(this);
            _flipper.setId(99);
            _flipper.setAdapter(adapter);
            PagerTitleStrip tabStrip = new PagerTitleStrip(this);
            ViewPager.LayoutParams params = new ViewPager.LayoutParams();
            params.width=LayoutParams.MATCH_PARENT;
            params.height=LayoutParams.WRAP_CONTENT;
            params.gravity=Gravity.TOP;
            _flipper.addView(tabStrip, params);
            setContentView(_flipper);
        }
	}
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_PREFS) {
			_count = Utils.getIntPreference(this, MetricPreferencesActivity.COUNT);
			_period = Utils.getIntPreference(this, MetricPreferencesActivity.PERIOD);
			cpuV.setMetricPrefs(_count, _period);
			ramV.setMetricPrefs(_count, _period);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.metric_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.metrics_option_menu_preferences:
			startActivityForResult(new Intent(this, MetricPreferencesActivity.class), REQUEST_CODE_PREFS);
			return true;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		_thread = new DataThread(this, _vmgr, _object, Utils.getIntPreference(this, MetricPreferencesActivity.PERIOD), cpuV, ramV);
		_thread.start();
	}	
	
	@Override 
	protected void onStop() {
		if(_thread!=null)
			_thread.quit();
		super.onStop();
	}
	
    class MetricViewPagerAdapter extends PagerAdapter {
        private List<MetricView> _views = new ArrayList<MetricView>();
        
        public void addView(MetricView view) {
            _views.add(view);
        }
        
        @Override
        public int getCount() {
            return _views.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _views.get(position).getHeader();
        }
        
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MetricView view = _views.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
}
