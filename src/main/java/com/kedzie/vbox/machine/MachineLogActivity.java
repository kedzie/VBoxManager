package com.kedzie.vbox.machine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.task.BaseTask;

public class MachineLogActivity extends Activity {
	private static final int MAX_LOG_SIZE=1024;
	private static final String TAG = "MachineLogActivity";
	
	private IMachine _machine;
	private String log;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machine_log);
        _machine = BundleBuilder.getProxy(getIntent(), "machine", IMachine.class);
        if((log=(String)getLastNonConfigurationInstance())!=null)
        	((TextView)findViewById(R.id.logText)).setText(log);
        else
        	new LoadLogTask(this).execute(_machine);
    }

	@Override
	public Object onRetainNonConfigurationInstance() {
		return log;
	}
	
class LoadLogTask extends BaseTask<IMachine, String> {
		
		public LoadLogTask(Context activity) {
			super(LoadLogTask.class.getSimpleName(), activity, null, "Reading Log");
		}
		
		@Override 
		protected String work(IMachine... m) throws Exception {
			return new String(m[0].readLog(0, 0, MAX_LOG_SIZE));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			((TextView)findViewById(R.id.logText)).setText(result);
			Log.i(TAG,"Log size: " + result.length());
		}
	}
}
