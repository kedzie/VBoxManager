package com.kedzie.vbox.machine;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VMAction;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IDisplay;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.event.EventIntentService;
import com.kedzie.vbox.machine.settings.CategoryListFragmentActivity;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class ActionsFragment extends SherlockFragment implements OnItemClickListener {
	protected static final String TAG = ActionsFragment.class.getSimpleName();
	
	private MachineView _headerView;
	private ListView _listView;
	
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	/** The Virtual Machine */
	private IMachine _machine;
	private LocalBroadcastManager lbm;
	
	/** Event-handling local broadcasts */
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Recieved Broadcast: " + intent.getAction());
			if(intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name())) {
				IMachine m = BundleBuilder.getProxy(intent.getExtras(), IMachine.BUNDLE, IMachine.class);
				new UpdateMachineViewTask(_vmgr).execute(m);
			} else if (intent.getAction().equals(VBoxEventType.ON_SESSION_STATE_CHANGED.name())) {
				new HandleSessionChangedEvent(_vmgr).execute(intent.getExtras());
			}
		}
	};
	
	/**
	 * Load Machine properties from web server
	 */
	class UpdateMachineViewTask extends ActionBarTask<IMachine, IMachine> {
		
		public UpdateMachineViewTask(VBoxSvc vmgr) {
			super(UpdateMachineViewTask.class.getSimpleName(), getSherlockActivity(), vmgr);
		}
		
		@Override 
		protected IMachine work(IMachine... m) throws Exception {
			MachineView.cacheProperties(m[0]);
			m[0].getMemorySize();
			return m[0];
		}

		@Override
		protected void onResult(IMachine result) {
			_machine=result;
			_headerView.update(result);
			_listView.setAdapter(new MachineActionAdapter(VMAction.getVMActions(result.getState())));
		}
	}
	
	/**
	 * Handle SessionStateChanged event
	 */
	class HandleSessionChangedEvent extends ActionBarTask<Bundle, SessionState> {
		
		public HandleSessionChangedEvent(VBoxSvc vmgr) {  
			super( "HandleEventTask", getSherlockActivity(), vmgr);
		}

		@Override
		protected SessionState work(Bundle... params) throws Exception {
			ISessionStateChangedEvent event = (ISessionStateChangedEvent)BundleBuilder.getProxy(params[0], EventIntentService.BUNDLE_EVENT, IEvent.class);
			return event.getState();
		}

		@Override
		protected void onResult(SessionState result)	{
		}
	}
	
	/**
	 * List Adapter for Virtual Machine Actions 
	 */
	class MachineActionAdapter extends ArrayAdapter<VMAction> {
		private final LayoutInflater _layoutInflater;
		
		public MachineActionAdapter(VMAction []actions) {
			super(getActivity(), 0, actions);
			_layoutInflater = LayoutInflater.from(getActivity());
		}

		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) 
				view = _layoutInflater.inflate(R.layout.machine_action_item, parent, false);
			((TextView)view.findViewById(R.id.action_item_text)).setText(getItem(position).toString());
			((ImageView)view.findViewById(R.id.action_item_icon)).setImageResource( getApp().getDrawable(getItem(position)));
			return view;                                                                                                                                                                                                                                                          
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _vmgr = getArguments().getParcelable(VBoxSvc.BUNDLE);
        _machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_headerView = new MachineView(getActivity());
		_listView = new ListView(getActivity());
		_listView.setClipChildren(false);
		_listView.addHeaderView(_headerView);
		_listView.setOnItemClickListener(this);
		return _listView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lbm = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
		new UpdateMachineViewTask(_vmgr).execute(_machine);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		lbm.registerReceiver(_receiver, Utils.createIntentFilter(
		        VBoxEventType.ON_MACHINE_STATE_CHANGED.name(),
		        VBoxEventType.ON_SESSION_STATE_CHANGED.name()));
	}

	@Override
	public void onStop() {
		lbm.unregisterReceiver(_receiver);
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_refresh:
			new UpdateMachineViewTask(_vmgr).execute(_machine);
			return true;
		}
		return false;
	}
	
	@Override 
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		VMAction action = (VMAction)_listView.getAdapter().getItem(position);
		if(action==null) return;
		if(action.equals(VMAction.START))	
			new LaunchVMProcessTask(getActivity(), _vmgr).execute(_machine);
		else if(action.equals(VMAction.POWER_OFF))	
			new MachineTask<IMachine, Void>("PoweroffTask", getActivity(), _vmgr, "Powering Off", false, _machine) {	
			  protected IProgress workWithProgress(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  return console.powerDown();
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.RESET))
			new MachineTask<IMachine, Void>("ResetTask", getActivity(), _vmgr, "Resetting", true, _machine) {	
			  protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.reset(); 
				  return null;
			  }
			  }.execute(_machine);
		else if(action.equals(VMAction.PAUSE)) 	
			new MachineTask<IMachine, Void>("PauseTask", getActivity(), _vmgr, "Pausing", true, _machine) {	
			  protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception {  
				  console.pause();	
				  return null;
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.RESUME)) 
			new MachineTask<IMachine, Void>("ResumeTask", getActivity(), _vmgr, "Resuming", true, _machine) {	
			  protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.resume();
				  return null;
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.POWER_BUTTON)) 	
			new MachineTask<IMachine, Void>("ACPITask", getActivity(), _vmgr, "ACPI Power Down", true, _machine) {
			  protected Void work(IMachine m,  IConsole console,IMachine...i) throws Exception {	
				  console.powerButton(); 	
				  return null;
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.SAVE_STATE)) 	
			new MachineTask<IMachine, Void>("SaveStateTask", getActivity(), _vmgr, "Saving State", false, _machine) { 
				protected IProgress workWithProgress(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					return console.saveState(); 
				}
			}.execute(_machine);
		else if(action.equals(VMAction.DISCARD_STATE)) 	
			new MachineTask<IMachine, Void>("DiscardStateTask", getActivity(), _vmgr, "Discarding State", true, _machine) { 
				protected Void work(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					console.discardSavedState(true); 
					return null;
				}
			}.execute(_machine);
		else if(action.equals(VMAction.TAKE_SNAPSHOT)) 	{
		    Utils.showDialog(getSherlockActivity().getSupportFragmentManager(), 
                    "snapshotDialog", 
                    TakeSnapshotFragment.getInstance(new BundleBuilder()
										.putParcelable(VBoxSvc.BUNDLE, _vmgr)
										.putProxy(IMachine.BUNDLE, _machine)
										.create()) );
		} else if(action.equals(VMAction.VIEW_METRICS)) {
			startActivity(new Intent(getActivity(), MetricActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)_vmgr)
					.putExtra(MetricActivity.INTENT_TITLE, _machine.getName() + " Metrics")
					.putExtra(MetricActivity.INTENT_OBJECT, _machine.getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine.getMemorySize() )
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User",  "CPU/Load/Kernel"  } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" } ));
		} else if(action.equals(VMAction.TAKE_SCREENSHOT)) 	{
			new MachineTask<Void, byte []>("TakeScreenshotTask", getActivity(), _vmgr, "Taking Screenshot", true, _machine) { 
				protected byte[] work(IMachine m, IConsole console, Void...i) throws Exception { 	
					IDisplay display = console.getDisplay();
					Map<String, String> res = display.getScreenResolution(0);
					return display.takeScreenShotPNGToArray(0, Integer.valueOf(res.get("width")), Integer.valueOf(res.get("height")));
				}
				@Override
				protected void onResult(byte[] result) {
					super.onResult(result);
					Utils.showDialog(getSherlockActivity().getSupportFragmentManager(), 
		                    "screenshotDialog", 
					        ScreenshotDialogFragment.getInstance(
					            new BundleBuilder().putByteArray(ScreenshotDialogFragment.BUNDLE_BYTES, result).create()) );
				}
			}.execute();
		} else if(action.equals(VMAction.EDIT_SETTINGS)) {
		    startActivity(new Intent(getActivity(), CategoryListFragmentActivity.class).putExtras(getArguments()));
		}
	}
	
	public VBoxApplication getApp() { 
		return (VBoxApplication)getActivity().getApplication(); 
	}
}
