package com.kedzie.vbox.machine.group;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.Screenshot;
import com.kedzie.vbox.api.jaxb.CPUPropertyType;
import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.HWVirtExPropertyType;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.CollapsiblePanelView;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.MachineCallable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class GroupInfoFragment extends Fragment {
    private static final String TAG = "GroupInfoFragment";
    
    static final ClassLoader LOADER = GroupInfoFragment.class.getClassLoader();
    
    public static class MachineInfo implements Parcelable {

        public static final Parcelable.Creator<MachineInfo> CREATOR  = new Parcelable.Creator<MachineInfo>() {
        	
            @Override
            public MachineInfo createFromParcel(Parcel source) {
                return new MachineInfo((IMachine)source.readParcelable(LOADER),(Screenshot)source.readParcelable(LOADER));
            }
            @Override
            public MachineInfo[] newArray(int size) {
                return new MachineInfo[size];
            }
        };
        
        public IMachine machine;
        public Screenshot screenshot;
        
        public MachineInfo(IMachine m, Screenshot screenshot) {
            this.machine=m;
            this.screenshot=screenshot;
        }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(machine, flags);
            dest.writeParcelable(screenshot, flags);
        }
        
        @Override
        public int describeContents() {
            return 0;
        }
    }
    
	class LoadInfoTask extends ActionBarTask<VMGroup, ArrayList<MachineInfo>> {

		public LoadInfoTask() { 
			super((AppCompatActivity)getActivity(), null);
		}

		@Override 
		protected ArrayList<MachineInfo> work(VMGroup... g) throws Exception {
		    LinkedList<Future<MachineInfo>> futures = new LinkedList<Future<MachineInfo>>();
		    for(TreeNode child : g[0].getChildren()) {
		        if(child instanceof IMachine) {
		            futures.add(getExecutor().submit(new MachineCallable<MachineInfo>((IMachine)child) {
		                @Override
		                public MachineInfo call() throws Exception {
		                    Utils.cacheProperties(m);
		                    m.getGroups(); m.getMemorySize(); m.getCPUCount();
		                    m.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING);
		                    m.getHWVirtExProperty(HWVirtExPropertyType.ENABLED);
		                    m.getCPUProperty(CPUPropertyType.PAE);
		                    for(int i=1;i<=99; i++) {
		                        if(m.getBootOrder(i).equals(DeviceType.NULL)) break;
		                    }
		                    int size = getResources().getDimensionPixelSize(R.dimen.screenshot_size);
		                    MachineInfo mi = new MachineInfo(m, null);
		                    if(m.getState().equals(MachineState.SAVED)) {
		                        mi.screenshot = _vmgr.readSavedScreenshot(m, 0);
		                        mi.screenshot.scaleBitmap(size, size);
		                    }else if(m.getState().equals(MachineState.RUNNING)) {
		                        try {
		                            mi.screenshot = _vmgr.takeScreenshot(m, size, size);
		                        } catch(IOException e) {
		                            Log.e(TAG, "Exception taking screenshot", e);
		                        }
		                    }
		                    return mi;
		                }
		            }));
		        }
		    }
		    ArrayList<MachineInfo> info = new ArrayList<MachineInfo>(g[0].getChildren().size());
		    for(Future<MachineInfo> future : futures)
		        info.add(future.get());
		    return info;
		}

		@Override
		protected void onSuccess(ArrayList<MachineInfo> result) {
				_info = result;
				populateViews(result);
		}
	}
	
	private VMGroup _group;
	private int _maxBootPosition;
	private ArrayList<MachineInfo> _info;
	private LinearLayout _view;
	
	private LocalBroadcastManager lbm;
	/** Event-handling local broadcasts */
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Recieved Broadcast: " + intent.getAction());
			if(intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name())) {
				new LoadInfoTask().execute(_group);
			} 
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null) {
		    _group = savedInstanceState.getParcelable(VMGroup.BUNDLE);
		    _maxBootPosition = savedInstanceState.getInt("maxBootPosition");
		    _info = savedInstanceState.getParcelableArrayList("info");
		} else {
		    _group = getArguments().getParcelable(VMGroup.BUNDLE);
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(VMGroup.BUNDLE, _group);
        outState.putParcelableArrayList("info", _info);
        outState.putInt("maxBootPosition", _maxBootPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(Utils.getScreenSize(newConfig)==Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Log.i(TAG, "Handling orientation change");
            populateViews(_info);
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    _view = new LinearLayout(getActivity());
	    _view.setOrientation(LinearLayout.VERTICAL);
		ScrollView scrollView = new ScrollView(getActivity());
		scrollView.addView(_view);
	    return scrollView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lbm = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
		if(_info!=null)
			populateViews(_info);
		else
			new LoadInfoTask().execute(_group);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		lbm.registerReceiver(_receiver, Utils.createIntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
	}

	@Override
	public void onStop() {
		lbm.unregisterReceiver(_receiver);
		super.onStop();
	}

	private void populateViews(List<MachineInfo> m) {
        _view.removeAllViews();
	    LayoutInflater inflater = LayoutInflater.from(getActivity());
	    for(MachineInfo node : m) {
	        View view = inflater.inflate(R.layout.group_info, _view, false);
	        Utils.setTextView(view, R.id.name, node.machine.getName());
	        Utils.setTextView(view, R.id.ostype, node.machine.getOSTypeId());
	        if(!Utils.isEmpty(node.machine.getGroups()))
	            Utils.setTextView(view, R.id.groups, node.machine.getGroups().get(0));
	        Utils.setTextView(view, R.id.baseMemory, node.machine.getMemorySize()+"");
	        Utils.setTextView(view, R.id.processors, node.machine.getCPUCount()+"");
	        String acceleration = "";
			if(node.machine.getHWVirtExProperty(HWVirtExPropertyType.ENABLED))
				acceleration="VT-x/AMD-V";
			if(node.machine.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING))
				acceleration+=(acceleration.equals("") ? "" : ", ") + "Nested Paging";
			if(node.machine.getCPUProperty(CPUPropertyType.PAE))
				acceleration+=(acceleration.equals("") ? "" : ", ") + "PAE/NX";
			Utils.setTextView(view, R.id.acceleration, acceleration);
			StringBuffer bootOrder = new StringBuffer();
			for(int i=1; i<=99; i++) {
				DeviceType b = node.machine.getBootOrder(i);
				if(b.equals(DeviceType.NULL)) break;
				Utils.appendWithComma(bootOrder, b.toString());
			}
			Utils.setTextView(view, R.id.bootOrder, bootOrder.toString());
			CollapsiblePanelView previewPanel = (CollapsiblePanelView)view.findViewById(R.id.previewPanel);
	        if(node.screenshot!=null) {
	        	ImageView preview = (ImageView)view.findViewById(R.id.preview);
	        	preview.setAdjustViewBounds(true);
				preview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				preview.setImageBitmap(node.screenshot.getBitmap());
				previewPanel.expand(false);
	        } else {
	        	previewPanel.collapse(false);
	        }
	        _view.addView(view);
	    }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_refresh:
			new LoadInfoTask().execute(_group);
			return false;
		}
		return false;
	}
}
