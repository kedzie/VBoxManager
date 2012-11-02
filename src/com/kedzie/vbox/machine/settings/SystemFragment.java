package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISystemProperties;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class SystemFragment extends SherlockFragment {

	class LoadInfoTask extends ActionBarTask<IMachine, Tuple<ISystemProperties, IHost>> {
		public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), _machine.getVBoxAPI()); }

		@Override 
		protected Tuple<ISystemProperties, IHost> work(IMachine... m) throws Exception {
			//cache values
			m[0].getMemorySize();
			m[0].getCPUCount(); 
			ISystemProperties props = _vmgr.getVBox().getSystemProperties();
			props.getMaxGuestCPUCount();
			props.getMinGuestCPUCount();
			props.getMaxGuestRAM();
			IHost host = _vmgr.getVBox().getHost();
			host.getMemoryAvailable();
			host.getMemorySize();
			return new Tuple<ISystemProperties, IHost>(props, host);
		}
		@Override
		protected void onResult(Tuple<ISystemProperties, IHost> result) {
		    _systemProperties=result.first;
		    _host = result.second;
		    populateViews(_machine, _systemProperties, _host);
		}
	}
	
	private IMachine _machine;
	private ISystemProperties _systemProperties;
	private IHost _host;
	private View _view;
	private SeekBar _baseMemoryBar;
	private SeekBar _processorsBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null) {
		    _systemProperties = savedInstanceState.getParcelable("systemProperties");
		    _host = savedInstanceState.getParcelable("host");
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IMachine.BUNDLE, _machine);
        outState.putParcelable("systemProperties", _systemProperties);
        outState.putParcelable("host", _host);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_system, null);
		_baseMemoryBar = (SeekBar)_view.findViewById(R.id.baseMemory);
		_processorsBar = (SeekBar)_view.findViewById(R.id.processors);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState!=null) 
			populateViews(_machine, _systemProperties, _host);
		else 
			new LoadInfoTask().execute(_machine);
	}

	private void populateViews(IMachine m, ISystemProperties sp, IHost h) {
	    _baseMemoryBar.setMax(h.getMemoryAvailable());
		_baseMemoryBar.setProgress(m.getMemorySize());
		_processorsBar.setMax(sp.getMaxGuestCPUCount());
		_processorsBar.setProgress(m.getCPUCount());
	}
}
