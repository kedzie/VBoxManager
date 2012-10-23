package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISystemProperties;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class SystemFragment extends SherlockFragment {

	class LoadInfoTask extends ActionBarTask<IMachine, ISystemProperties> {
		public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), _machine.getVBoxAPI()); }

		@Override 
		protected ISystemProperties work(IMachine... m) throws Exception {
			//cache values
			m[0].getMemorySize();
			m[0].getCPUCount(); 
			ISystemProperties props = _vmgr.getVBox().getSystemProperties();
			props.getMaxGuestCPUCount();
			props.getMinGuestCPUCount();
			props.getMaxGuestRam();
			return props;
		}
		@Override
		protected void onResult(ISystemProperties result) {
		    _systemProperties=result;
		    populateViews(_machine, _systemProperties);
		}
	}
	
	private IMachine _machine;
	private ISystemProperties _systemProperties;
	private View _view;
	private SeekBar _baseMemoryBar;
	private SeekBar _processorsBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null)
		    _systemProperties = BundleBuilder.getProxy(savedInstanceState, "systemProperties", ISystemProperties.class);
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
        BundleBuilder.putProxy(outState, "systemProperties", _systemProperties);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.machine_info, null);
		_baseMemoryBar = (SeekBar)_view.findViewById(R.id.baseMemory);
		_processorsBar = (SeekBar)_view.findViewById(R.id.processors);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState!=null) 
			populateViews(_machine, _systemProperties);
		else 
			new LoadInfoTask().execute(_machine);
	}

	private void populateViews(IMachine m, ISystemProperties sp) {
	    _baseMemoryBar.setMax(sp.getMaxGuestRam());
		_baseMemoryBar.setProgress(m.getMemorySize());
		_processorsBar.setMax(sp.getMaxGuestCPUCount());
		_processorsBar.setProgress(m.getCPUCount());
	}
}
