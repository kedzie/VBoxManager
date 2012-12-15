package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.SliderView;
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class SystemFragment extends SherlockFragment {

	class LoadInfoTask extends ActionBarTask<IMachine, IHost> {
		public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), _machine.getVBoxAPI()); }

		@Override 
		protected IHost work(IMachine... m) throws Exception {
			//cache values
			m[0].getMemorySize();
			m[0].getCPUCount(); 
			IHost host = _vmgr.getVBox().getHost();
			host.getProcessorCount();
			host.getProcessorOnlineCount();
			host.getMemoryAvailable();
			host.getMemorySize();
			return host;
		}
		@Override
		protected void onResult(IHost result) {
		    _host = result;
		    populateViews(_machine, _host);
		}
	}
	
	private IMachine _machine;
	private IHost _host;
	private View _view;
	private SliderView _baseMemoryBar;
	private SliderView _processorsBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null) {
		    _host = savedInstanceState.getParcelable("host");
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IMachine.BUNDLE, _machine);
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
			populateViews(_machine, _host);
		else 
			new LoadInfoTask().execute(_machine);
	}

	private void populateViews(IMachine m, IHost h) {
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
	    _processorsBar.setMaxValue(h.getProcessorCount());
		_processorsBar.setMaxValidValue(h.getProcessorOnlineCount());
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
