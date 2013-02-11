package com.kedzie.vbox.machine;

import java.io.IOException;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.INetworkAdapter;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.jaxb.CPUPropertyType;
import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.HWVirtExPropertyType;
import com.kedzie.vbox.api.jaxb.IMediumAttachment;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.api.jaxb.NetworkAttachmentType;
import com.kedzie.vbox.api.jaxb.StorageBus;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.PanelView;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.group.GroupInfoFragment.MachineInfo;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @apiviz.stereotype fragment
 */
public class InfoFragment extends SherlockFragment {

	class LoadInfoTask extends ActionBarTask<IMachine, MachineInfo> {

		public LoadInfoTask() { 
			super("LoadInfoTask", getSherlockActivity(), _machine.getAPI()); 
		}

		@Override 
		protected MachineInfo work(IMachine... m) throws Exception {
			//cache values
			MachineView.cacheProperties(m[0]);
			m[0].getMemorySize(); m[0].getCPUCount(); m[0].getVRAMSize(); 
			m[0].getAccelerate2DVideoEnabled(); m[0].getAccelerate3DEnabled(); 
			m[0].getDescription(); m[0].getGroups();
			m[0].getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING);
			m[0].getHWVirtExProperty(HWVirtExPropertyType.ENABLED);
			m[0].getCPUProperty(CPUPropertyType.PAE);
			//audio
			m[0].getAudioAdapter().getAudioController();
			m[0].getAudioAdapter().getAudioDriver();
			//boot order
			for(int i=1 ;i<=99; i++)
				if(m[0].getBootOrder(i).equals(DeviceType.NULL)) break;
			//storage controllers
			List<IStorageController> controllers = m[0].getStorageControllers();
			for(IStorageController controller : controllers) {
				controller.getBus();
				for(IMediumAttachment a : m[0].getMediumAttachmentsOfController(controller.getName())) {
					if(a.getMedium()!=null)
						a.getMedium().getName();
				}
			}
			//network adapters
			for(int i=0; i<4; i++) {
				INetworkAdapter adapter = m[0].getNetworkAdapter(i);
				adapter.getEnabled(); adapter.getAdapterType(); adapter.getAttachmentType(); 
				adapter.getBridgedInterface(); adapter.getHostOnlyInterface(); adapter.getGenericDriver(); adapter.getInternalNetwork();
			}
			//screenshots
			int size = getResources().getDimensionPixelSize(R.dimen.screenshot_size);
			MachineInfo info = new MachineInfo(m[0], null);
			if(m[0].getState().equals(MachineState.SAVED)) {
				info.screenshot =  _vmgr.readSavedScreenshot(m[0], 0);
				info.screenshot.scaleBitmap(size, size);
			} else if(m[0].getState().equals(MachineState.RUNNING)) {
				try { 
					info.screenshot = m[0].getAPI().takeScreenshot(m[0], size, size);
				} catch(IOException e) {
					Log.e(TAG, "Exception taking screenshot", e);
				}
			}
			return info;
		}

		@Override
		protected void onResult(MachineInfo result) {
		    _machine=result.machine;
		    _machineInfo = result;
		    populateViews(result);
		}
	}
	
	private IMachine _machine;
	private MachineInfo _machineInfo;
	private int _maxBootPosition;
	private View _view;
	private TextView _nameText;
	private TextView _descriptionText;
	private TextView _groupText;
	private TextView _osTypeText;
	private TextView _baseMemoryText;
	private TextView _processorsText;
	private TextView _bootOrderText;
	private TextView _accelerationText;
	private TextView _videoMemoryText;
	private TextView _accelerationVideoText;
	private TextView _rdpPortText;
	private TextView _storageText;
	private TextView _networkText;
	private TextView _audioDriver;
	private TextView _audioController;
	private PanelView _previewPanel;
	private ImageView _preview;
	private LocalBroadcastManager lbm;
	/** Event-handling local broadcasts */
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name())) {
				IMachine m = BundleBuilder.getProxy(intent.getExtras(), IMachine.BUNDLE, IMachine.class);
				new LoadInfoTask().execute(m);
			} 
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		_machine = BundleBuilder.getProxy(getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null) {
			_machine = BundleBuilder.getProxy(savedInstanceState, IMachine.BUNDLE, IMachine.class);
			_machineInfo = savedInstanceState.getParcelable("info");
			_maxBootPosition = savedInstanceState.getInt("maxBootPosition");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.machine_info, null);
		_nameText = (TextView)_view.findViewById(R.id.name);
		_descriptionText = (TextView)_view.findViewById(R.id.description);
		_groupText = (TextView)_view.findViewById(R.id.groups);
		_osTypeText = (TextView)_view.findViewById(R.id.ostype);
		_baseMemoryText = (TextView)_view.findViewById(R.id.baseMemory);
		_processorsText = (TextView)_view.findViewById(R.id.processors);
		_bootOrderText = (TextView)_view.findViewById(R.id.bootOrder);
		_accelerationText = (TextView)_view.findViewById(R.id.acceleration);
		_videoMemoryText = (TextView)_view.findViewById(R.id.videoMemory);
		_accelerationVideoText = (TextView)_view.findViewById(R.id.accelerationVideo);
		_storageText = (TextView)_view.findViewById(R.id.storage);
		_networkText = (TextView)_view.findViewById(R.id.network);
		_audioDriver = (TextView)_view.findViewById(R.id.audio_driver);
		_audioController = (TextView)_view.findViewById(R.id.audio_controlller);
		_rdpPortText = (TextView)_view.findViewById(R.id.rdpPort);
		_preview = (ImageView)_view.findViewById(R.id.preview);
		_previewPanel =(PanelView)_view.findViewById(R.id.previewPanel);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lbm = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
	}
	
	@Override
	public void onStart() {
		super.onStart();
		lbm.registerReceiver(_receiver, Utils.createIntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
		if(_machineInfo!=null) 
			populateViews(_machineInfo);
		else 
			new LoadInfoTask().execute(_machine);
	}

	@Override
	public void onStop() {
		lbm.unregisterReceiver(_receiver);
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
		outState.putParcelable("info", _machineInfo);
		outState.putInt("maxBootPosition", _maxBootPosition);
	}

	private void populateViews(MachineInfo info) {
	    IMachine m = info.machine;
		_nameText.setText( m.getName());
		_osTypeText.setText( m.getOSTypeId() );
		if(!Utils.isEmpty(m.getGroups())) 
		    _groupText.setText(m.getGroups().get(0));
	    else
	        _groupText.setText("None");
		_baseMemoryText.setText( m.getMemorySize()+"" );
		_processorsText.setText( m.getCPUCount()+"" );
		//boot order
		StringBuffer bootOrder = new StringBuffer();
		for(int i=1; i<=99; i++) {
			DeviceType b = m.getBootOrder(i);
			if(b.equals(DeviceType.NULL)) break;
			Utils.appendWithComma(bootOrder, b.toString());
		}
		_bootOrderText.setText( bootOrder.toString() );
		
		StringBuffer acceleration = new StringBuffer();
		if(m.getHWVirtExProperty(HWVirtExPropertyType.ENABLED))
			Utils.appendWithComma(acceleration, "VT-x/AMD-V");
		if(m.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING))
			Utils.appendWithComma(acceleration, "Nested Paging");
		if(m.getCPUProperty(CPUPropertyType.PAE))
			Utils.appendWithComma(acceleration,  "PAE/NX");
		_accelerationText.setText(acceleration.toString());
		//storage controllers
		StringBuffer storage = new StringBuffer();
		List<IStorageController> controllers = m.getStorageControllers();
		for(IStorageController controller : controllers) {
			storage.append("Controller: ").append(controller.getName());
			if(controller.getBus().equals(StorageBus.SATA)) {
				for(IMediumAttachment a : m.getMediumAttachmentsOfController(controller.getName()))
					storage.append(String.format("\nSATA Port %1$d\t\t%2$s", a.getPort(), a.getMedium()==null ? "" : a.getMedium().getName()));
			} else if (controller.getBus().equals(StorageBus.IDE)){
				for(IMediumAttachment a : m.getMediumAttachmentsOfController(controller.getName())) {
					storage.append(String.format("\nIDE %1$s %2$s\t\t%3$s", a.getPort()==0 ? "Primary" : "Secondary", a.getDevice()==0 ? "Master" : "Slave", a.getMedium()==null ? "" : a.getMedium().getName()));
				}
			} else {
				for(IMediumAttachment a : m.getMediumAttachmentsOfController(controller.getName()))
					storage.append(String.format("\nPort %1$d Device %2$d\t\t%3$s", a.getPort(), a.getDevice(), a.getMedium()==null ? "" : a.getMedium().getName()));
			}
		}
		_storageText.setText(storage.toString());
		//audio devices
		_audioController.setText(m.getAudioAdapter().getAudioController().toString());
		_audioDriver.setText(m.getAudioAdapter().getAudioDriver().toString());
		//network devices
		StringBuffer networkText = new StringBuffer();
		for(int i=0; i<4; i++) {
			INetworkAdapter adapter = m.getNetworkAdapter(i);
			if(!adapter.getEnabled())
				continue;
			if(i>0) 
				networkText.append("\n");
			networkText.append("Adapter ").append(i).append(": ").append(adapter.getAdapterType());
			if(adapter.getAttachmentType().equals(NetworkAttachmentType.BRIDGED))
				networkText.append("(Bridged Adapter, ").append(adapter.getBridgedInterface()).append(")");
			else if(adapter.getAttachmentType().equals(NetworkAttachmentType.BRIDGED))
				networkText.append("(Host-Only Adapter, ").append(adapter.getHostOnlyInterface()).append(")");
			if(adapter.getAttachmentType().equals(NetworkAttachmentType.GENERIC))
				networkText.append("(Generic-Driver Adapter, ").append(adapter.getGenericDriver()).append(")");
			if(adapter.getAttachmentType().equals(NetworkAttachmentType.INTERNAL))
				networkText.append("(Internal-Network Adapter, ").append(adapter.getInternalNetwork()).append(")");
		}
		_networkText.setText(networkText.toString());
		
		_videoMemoryText.setText( m.getVRAMSize()+" MB");
		_accelerationVideoText.setText( (m.getAccelerate2DVideoEnabled() ? "2D" : "") + " " +  (m.getAccelerate3DEnabled() ? "3D" : "") );
		_rdpPortText.setText( "NaN" );
		_descriptionText.setText( m.getDescription()+"" );
		
		if(info.screenshot!=null) {
			_preview.setImageBitmap(info.screenshot.getBitmap());
		    _preview.setAdjustViewBounds(true);
			_preview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			_previewPanel.expand();
		} else
			_previewPanel.collapse();
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    switch(item.getItemId()) {
	        case R.id.option_menu_refresh:
	            new LoadInfoTask().execute(_machine);
	            return true;
	    }
	    return false;
	}
}