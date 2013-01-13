package com.kedzie.vbox.machine.group;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class GroupInfoFragment extends SherlockFragment {
    private static final String TAG = "GroupInfoFragment";
    
    public static class MachineInfo implements Parcelable {

        public static final Parcelable.Creator<MachineInfo> CREATOR  = new Parcelable.Creator<MachineInfo>() {
            @Override
            public MachineInfo createFromParcel(Parcel source) {
                return new MachineInfo((IMachine)source.readParcelable(GroupInfoFragment.class.getClassLoader()), source.createByteArray());
            }
            @Override
            public MachineInfo[] newArray(int size) {
                return new MachineInfo[size];
            }
        };
        
        public IMachine machine;
        public byte[] screenshot;
        
        public MachineInfo(IMachine m, byte[] bytes) {
            this.machine=m;
            this.screenshot=bytes;
        }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(machine, flags);
            dest.writeByteArray(screenshot);
        }
        
        @Override
        public int describeContents() {
            return 0;
        }
    }
    
	class LoadInfoTask extends ActionBarTask<VMGroup, ArrayList<MachineInfo>> {

		public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), null); }

		@Override 
		protected ArrayList<MachineInfo> work(VMGroup... g) throws Exception {
		    Log.i(TAG, "Loading Group Info");
		    ArrayList<MachineInfo> info = new ArrayList<MachineInfo>();
		    for(TreeNode child : g[0].getChildren()) {
		        if(child instanceof IMachine) {
		            IMachine m = (IMachine)child;
		            m.getName();  m.getGroups(); m.getOSTypeId(); m.getMemorySize(); m.getCPUCount();
		            int size = getResources().getDimensionPixelSize(R.dimen.screenshot_size);
                    info.add(new MachineInfo(m, m.getVBoxAPI().takeScreenshot(m, size, size)));
		        }
		    }
		    return info;
		}

		@Override
		protected void onResult(ArrayList<MachineInfo> result) {
				_info = result;
				populateViews(result);
		}
	}
	
	private VMGroup _group;
	private ArrayList<MachineInfo> _info;
	private LinearLayout _view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null) {
		    _group = savedInstanceState.getParcelable(VMGroup.BUNDLE);
		    _info = savedInstanceState.getParcelableArrayList("info");
		} else {
		    _group = getArguments().getParcelable(VMGroup.BUNDLE);
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(VMGroup.BUNDLE, _group);
        outState.putParcelableArrayList("info", _info);
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
		if(savedInstanceState!=null) 
			populateViews(_info);
		else 
			new LoadInfoTask().execute(_group);
	}

	private void populateViews(List<MachineInfo> m) {
	    LayoutInflater inflater = LayoutInflater.from(getActivity());
	    for(MachineInfo node : m) {
	        Log.i(TAG, "Populating view: " + node.machine);
	        View view = inflater.inflate(R.layout.group_info, _view, true);
	        Utils.setTextView(view, R.id.name, node.machine.getName());
	        Utils.setTextView(view, R.id.ostype, node.machine.getOSTypeId());
	        if(!Utils.isEmpty(node.machine.getGroups()))
	            Utils.setTextView(view, R.id.groups, node.machine.getGroups().get(0));
	        Utils.setTextView(view, R.id.baseMemory, node.machine.getMemorySize()+"");
	        Utils.setTextView(view, R.id.processors, node.machine.getCPUCount()+"");
	        if(node.screenshot!=null)
	            Utils.setImageView(view, R.id.preview, BitmapFactory.decodeStream(new ByteArrayInputStream(node.screenshot)));
	    }
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_refresh:
			new LoadInfoTask().execute(_group);
			return true;
		}
		return false;
	}
}
