package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
			props.getMaxGuestVRam();
			props.getMinGuestVRam();
			props.getMaxGuestMonitors();
			props.getMinGuestMonitors();
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
	private SeekBar _videoMemoryBar;
	private SeekBar _monitorBar;
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
		_videoMemoryBar = (SeekBar)_view.findViewById(R.id.videoMemory);
		_monitorBar = (SeekBar)_view.findViewById(R.id.numMonitors);
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
	    _videoMemoryBar.setMax(sp.getMaxGuestVRam());
	    _videoMemoryBar.setProgress(m.getVRAMSize());
	    _monitorBar.setMax(sp.getMaxGuestMonitors());
	    _monitorBar.setProgress(m.getMonitorCount());
	    _acceleration2DBox.setChecked(m.getAccelerate2DVideoEnabled());
		_acceleration3DBox.setChecked(m.getAccelerate3DEnabled());
	}
}
