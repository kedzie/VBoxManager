package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISystemProperties;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.SliderView;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener;
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
	private SliderView _baseMemoryBar;
	private SliderView _processorsBar;
	
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
		_baseMemoryBar = (SliderView)_view.findViewById(R.id.baseMemory);
		_processorsBar = (SliderView)_view.findViewById(R.id.processors);
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
		_baseMemoryBar.setMinValue(1);
		_baseMemoryBar.setMinValidValue(1);
		_baseMemoryBar.setMaxValue(h.getMemoryAvailable());
		_baseMemoryBar.setMaxValidValue(h.getMemoryAvailable());
		_baseMemoryBar.setValue(m.getMemorySize());
	    _baseMemoryBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValueChanged(final int newValue) {
				new Thread() {
                    @Override
                    public void run() {
                        _machine.setMemorySize(newValue);
                    }
                }.start();
			}
		});
	    _processorsBar.setMinValue(1);
	    _processorsBar.setMinValidValue(1);
	    _processorsBar.setMaxValue(sp.getMaxGuestCPUCount());
		_processorsBar.setMaxValidValue(sp.getMaxGuestCPUCount());
		_processorsBar.setValue(m.getCPUCount());
		_processorsBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValueChanged(final int newValue) {
				new Thread() {
                    @Override
                    public void run() {
                        _machine.setCPUCount(newValue);
                    }
                }.start();
			}
		});
	}
}
