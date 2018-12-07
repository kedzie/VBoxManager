package com.kedzie.vbox.machine;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.kedzie.vbox.machine.settings.VMSettingsActivity;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;

import java.util.Map;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class ActionsFragment extends Fragment implements OnItemClickListener {
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
			super((AppCompatActivity)getActivity(), vmgr);
		}
		
		@Override 
		protected IMachine work(IMachine... m) throws Exception {
			Utils.cacheProperties(m[0]);
			m[0].getMemorySize();
			m[0].getSessionState();
			return m[0];
		}

		@Override
		protected void onSuccess(IMachine result) {
			_machine=result;
			_headerView.update(result);
            if(getActivity()!=null)
			    _listView.setAdapter(new MachineActionAdapter(VMAction.getVMActions(result.getState())));
		}
	}
	
	/**
	 * Handle SessionStateChanged event
	 */
	class HandleSessionChangedEvent extends ActionBarTask<Bundle, SessionState> {
		
		public HandleSessionChangedEvent(VBoxSvc vmgr) {  
			super((AppCompatActivity)getActivity(), vmgr);
		}

		@Override
		protected SessionState work(Bundle... params) throws Exception {
			ISessionStateChangedEvent event = (ISessionStateChangedEvent)BundleBuilder.getProxy(params[0], EventIntentService.BUNDLE_EVENT, IEvent.class);
			return event.getState();
		}

		@Override
		protected void onSuccess(SessionState result)	{
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
        setHasOptionsMenu(true);
        _vmgr = BundleBuilder.getVBoxSvc(getArguments());
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
			new LaunchVMProcessTask((AppCompatActivity)getActivity(), _vmgr).execute(_machine);
		else if(action.equals(VMAction.POWER_OFF))	
			new MachineTask<IMachine, Void>((AppCompatActivity)getActivity(), _vmgr, R.string.progress_powering_off, false, _machine) {
			  protected IProgress workWithProgress(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  return console.powerDown();
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.RESET))
			new MachineTask<IMachine, Void>((AppCompatActivity)getActivity(), _vmgr, R.string.progress_restarting, true, _machine) {
			  protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.reset(); 
				  return null;
			  }
			  }.execute(_machine);
		else if(action.equals(VMAction.PAUSE)) 	
			new MachineTask<IMachine, Void>((AppCompatActivity)getActivity(), _vmgr, R.string.progress_pausing, true, _machine) {
			  protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception {  
				  console.pause();	
				  return null;
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.RESUME)) 
			new MachineTask<IMachine, Void>((AppCompatActivity)getActivity(), _vmgr, R.string.progress_resuming, true, _machine) {
			  protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.resume();
				  return null;
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.POWER_BUTTON)) 	
			new MachineTask<IMachine, Void>( (AppCompatActivity)getActivity(), _vmgr, R.string.progress_acpi_down, true, _machine) {
			  protected Void work(IMachine m,  IConsole console,IMachine...i) throws Exception {	
				  console.powerButton(); 	
				  return null;
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.SAVE_STATE)) 	
			new MachineTask<IMachine, Void>( (AppCompatActivity)getActivity(), _vmgr, R.string.progress_saving_state, false, _machine) {
				protected IProgress workWithProgress(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					return console.saveState(); 
				}
			}.execute(_machine);
		else if(action.equals(VMAction.DISCARD_STATE)) 	
			new MachineTask<IMachine, Void>( (AppCompatActivity)getActivity(), _vmgr, R.string.progress_discarding_state, true, _machine) {
				protected Void work(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					console.discardSavedState(true); 
					return null;
				}
			}.execute(_machine);
		else if(action.equals(VMAction.TAKE_SNAPSHOT)) 	{
		    Utils.showDialog(((AppCompatActivity)getActivity()).getSupportFragmentManager(), "snapshotDialog",
                    TakeSnapshotFragment.getInstance(_vmgr, _machine, null) );
		} else if(action.equals(VMAction.VIEW_METRICS)) {
			startActivity(new Intent(getActivity(), MetricActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)_vmgr)
					.putExtra(MetricActivity.INTENT_TITLE, _machine.getName() + " Metrics")
                    .putExtra(MetricActivity.INTENT_ICON, getApp().getOSDrawable(_machine.getOSTypeId()))
					.putExtra(MetricActivity.INTENT_OBJECT, _machine.getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine.getMemorySize() )
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User",  "CPU/Load/Kernel"  } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" } ));
		} else if(action.equals(VMAction.TAKE_SCREENSHOT)) 	{
			new MachineTask<Void, byte []>((AppCompatActivity)getActivity(), _vmgr, R.string.progress_taking_snapshot, true, _machine) {
				protected byte[] work(IMachine m, IConsole console, Void...i) throws Exception { 	
					IDisplay display = console.getDisplay();
					Map<String, String> res = display.getScreenResolution(0);
					return display.takeScreenShotPNGToArray(0, Integer.valueOf(res.get("width")), Integer.valueOf(res.get("height")));
				}
				@Override
				protected void onSuccess(byte[] result) {
					super.onSuccess(result);
					Utils.showDialog(((AppCompatActivity)getActivity()).getSupportFragmentManager(), "screenshotDialog", ScreenshotDialogFragment.getInstance(result) );
				}
			}.execute();
		} else if(action.equals(VMAction.EDIT_SETTINGS)) {
			if(!_machine.getSessionState().equals(SessionState.UNLOCKED)) {
				new AlertDialog.Builder(getActivity())
						.setTitle("Cannot edit machine")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("Session state is " + _machine.getSessionState())
						.show();
			} else
				Utils.startActivity(getActivity(), new Intent(getActivity(), VMSettingsActivity.class).putExtras(getArguments()));

		}
	}
	
	public VBoxApplication getApp() { 
		return (VBoxApplication)getActivity().getApplication(); 
	}
}
