package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISystemProperties;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.SliderView;
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class DisplayFragment extends SherlockFragment {

	class LoadInfoTask extends ActionBarTask<IMachine, ISystemProperties> {
		public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), _machine.getVBoxAPI()); }
		@Override 
		protected ISystemProperties work(IMachine... m) throws Exception {
			//cache values
			m[0].getVRAMSize(); 
			m[0].getAccelerate2DVideoEnabled();
			m[0].getAccelerate3DEnabled();
			m[0].getMonitorCount();
			ISystemProperties props = _vmgr.getVBox().getSystemProperties();
			props.getMaxGuestVRAM();
			props.getMinGuestVRAM();
			props.getMaxGuestMonitors();
			return props;
		}
		@Override
		protected void onResult(ISystemProperties result) {
		        _systemProperties = result;
				populateViews(_machine, _systemProperties);
		}
	}
	
	private IMachine _machine;
	private ISystemProperties _systemProperties;
	private View _view;
	private SliderView _videoMemoryBar;
	private SliderView _monitorBar;
	private CheckBox _acceleration3DBox;
	private CheckBox _acceleration2DBox;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null)
            _systemProperties = BundleBuilder.getProxy(savedInstanceState, "systemProperties", ISystemProperties.class);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_display, null);
		_videoMemoryBar = (SliderView)_view.findViewById(R.id.videoMemory);
		_monitorBar = (SliderView)_view.findViewById(R.id.numMonitors);
		_acceleration2DBox = (CheckBox)_view.findViewById(R.id.acceleration2D);
		_acceleration3DBox = (CheckBox)_view.findViewById(R.id.acceleration3D);
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
		BundleBuilder.putProxy(outState, "systemProperties", _systemProperties);
	}

	private void populateViews(IMachine m, ISystemProperties sp) {
		_videoMemoryBar.setMinValue(1);
		_videoMemoryBar.setMinValidValue(1);
	    _videoMemoryBar.setMaxValue(sp.getMaxGuestVRAM());
	    _videoMemoryBar.setMaxValidValue(sp.getMaxGuestVRAM());
	    _videoMemoryBar.setValue(m.getVRAMSize());
	    _videoMemoryBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValueChanged(final int newValue) {
				new Thread() {
                    @Override
                    public void run() {
                        _machine.setVRAMSize(newValue);
                    }
                }.start();
			}
		});
	    _monitorBar.setMinValue(1);
	    _monitorBar.setMinValidValue(1);
	    _monitorBar.setMaxValue(sp.getMaxGuestMonitors());
	    _monitorBar.setMaxValidValue(sp.getMaxGuestMonitors());
	    _monitorBar.setValue(m.getMonitorCount());
	    _monitorBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValueChanged(final int newValue) {
				new Thread() {
                    @Override
                    public void run() {
                        _machine.setMonitorCount(newValue);
                    }
                }.start();
			}
		});
	    _acceleration2DBox.setChecked(m.getAccelerate2DVideoEnabled());
	    _acceleration2DBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                new Thread() {
                    @Override
                    public void run() {
                        _machine.setAccelerate2DVideoEnabled(isChecked);
                    }
                }.start();
            }
        });
		_acceleration3DBox.setChecked(m.getAccelerate3DEnabled());
		_acceleration3DBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                new Thread() {
                    @Override
                    public void run() {
                        _machine.setAccelerate3DEnabled(isChecked);
                    }
                }.start();
            }
        });
	}
}