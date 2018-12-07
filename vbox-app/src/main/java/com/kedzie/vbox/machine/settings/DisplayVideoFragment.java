package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISystemProperties;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.SliderView;
import com.kedzie.vbox.app.SliderView.OnSliderViewChangeListener;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.task.DialogTask;

/**
 * @apiviz.stereotype fragment
 */
public class DisplayVideoFragment extends Fragment {

	class LoadInfoTask extends DialogTask<IMachine, Tuple<ISystemProperties, IHost>> {
		
		public LoadInfoTask() {
			super((AppCompatActivity)getActivity(), _machine.getAPI(), R.string.progress_loading_data_generic);
		}
		
		@Override 
		protected Tuple<ISystemProperties, IHost> work(IMachine... m) throws Exception {
			m[0].getVRAMSize(); 
			m[0].getAccelerate2DVideoEnabled();
			m[0].getAccelerate3DEnabled();
			m[0].getMonitorCount();
			ISystemProperties props = _vmgr.getVBox().getSystemProperties();
			props.getMaxGuestVRAM();
			props.getMinGuestVRAM();
			props.getMaxGuestMonitors();
			IHost host = _vmgr.getVBox().getHost();
			host.getAcceleration3DAvailable();
			return new Tuple<ISystemProperties, IHost>(props, host);
		}
		
		@Override
		protected void onSuccess(Tuple<ISystemProperties, IHost> result) {
		        _systemProperties = result.first;
		        _host = result.second;
				populateViews(_machine, _systemProperties, _host);
		}
	}
	
	private IMachine _machine;
	private ISystemProperties _systemProperties;
	private IHost _host;
	private View _view;
	private SliderView _videoMemoryBar;
	private SliderView _monitorBar;
	private CheckBox _acceleration3DBox;
	private CheckBox _acceleration2DBox;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null) {
            _systemProperties = BundleBuilder.getProxy(savedInstanceState, "systemProperties", ISystemProperties.class);
            _host = BundleBuilder.getProxy(savedInstanceState, "host", IHost.class);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_display_video, null);
		_videoMemoryBar = (SliderView)_view.findViewById(R.id.videoMemory);
		_monitorBar = (SliderView)_view.findViewById(R.id.numMonitors);
		_acceleration2DBox = (CheckBox)_view.findViewById(R.id.acceleration2D);
		_acceleration3DBox = (CheckBox)_view.findViewById(R.id.acceleration3D);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_host!=null && _systemProperties!=null) 
			populateViews(_machine, _systemProperties, _host);
		else 
			new LoadInfoTask().execute(_machine);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
		BundleBuilder.putProxy(outState, "systemProperties", _systemProperties);
		BundleBuilder.putProxy(outState, "host", _host);
	}

	private void populateViews(IMachine m, ISystemProperties sp, IHost host) {
		_videoMemoryBar.setMinValue(1);
		_videoMemoryBar.setMinValidValue(1);
	    _videoMemoryBar.setMaxValue(sp.getMaxGuestVRAM());
	    _videoMemoryBar.setMaxValidValue(sp.getMaxGuestVRAM());
	    _videoMemoryBar.setValue(m.getVRAMSize());
	    _videoMemoryBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValidValueChanged(int newValue) {
				_machine.setVRAMSize(newValue);
			}
			@Override
			public void onSliderInvalidValueChanged(int newValue) {
			}
		});
	    _monitorBar.setMinValue(1);
	    _monitorBar.setMinValidValue(1);
	    _monitorBar.setMaxValue(sp.getMaxGuestMonitors());
	    _monitorBar.setMaxValidValue(sp.getMaxGuestMonitors());
	    _monitorBar.setValue(m.getMonitorCount());
	    _monitorBar.setOnSliderViewChangeListener(new OnSliderViewChangeListener() {
			@Override
			public void onSliderValidValueChanged(int newValue) {
				_machine.setMonitorCount(newValue);
			}
			@Override
			public void onSliderInvalidValueChanged(int newValue) {
			}
		});
	    _acceleration2DBox.setChecked(m.getAccelerate2DVideoEnabled());
	    _acceleration2DBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        _machine.setAccelerate2DVideoEnabled(isChecked);
            }
        });
	    _acceleration3DBox.setEnabled(host.getAcceleration3DAvailable());
		_acceleration3DBox.setChecked(m.getAccelerate3DEnabled());
		_acceleration3DBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        _machine.setAccelerate3DEnabled(isChecked);
            }
        });
	}
}