package com.kedzie.vbox.machine.group;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuInflater;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IDisplay;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.MachineTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class GroupInfoFragment extends SherlockFragment {

	class LoadInfoTask extends ActionBarTask<VMGroup, VMGroup> {

		public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), null); }

		@Override 
		protected VMGroup work(VMGroup... m) throws Exception {
		    VMGroup group = m[0];
		    
		    return group;
		}

		@Override
		protected void onResult(VMGroup result) {
				_group = result;
				populateViews(result);
				for(TreeNode node : result.getChildren()) {
				    if(!(node instanceof IMachine)) continue;
				    IMachine machine = (IMachine)node;
				    new MachineTask<Void, byte []>("TakeScreenshotTask", getActivity(), machine.getVBoxAPI(), "Taking Screenshot", true, machine) { 
		                protected byte[] work(IMachine m, IConsole console, Void...i) throws Exception {    
		                    IDisplay display = console.getDisplay();
		                    Map<String, String> res = display.getScreenResolution(0);
		                    return display.takeScreenShotPNGToArray(0, new Integer(res.get("width")), new Integer(res.get("height")));
		                }
		                @Override
		                protected void onResult(byte[] result) {
		                    super.onResult(result);
		                    _preview.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(result)));
		                }
		            }.execute();
				}
		}
	}
	
	private VMGroup _group;
	private LinearLayout _view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if(savedInstanceState!=null) 
		    _group = savedInstanceState.getParcelable(VMGroup.BUNDLE);
		else
		    _group = getArguments().getParcelable(VMGroup.BUNDLE);
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(VMGroup.BUNDLE, _group);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    _view = new LinearLayout(getActivity());
	    _view.setOrientation(LinearLayout.VERTICAL);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState!=null) 
			populateViews(_group);
		else 
			new LoadInfoTask().execute(_group);
	}

	private void populateViews(VMGroup m) {
	    LayoutInflater inflater = LayoutInflater.from(getActivity());
	    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        for(TreeNode node : _group.getChildren()) {
            if(node instanceof IMachine) {
                IMachine machine = (IMachine)node;
                View view = inflater.inflate(R.layout.machine_info_short, null);
                ((TextView)view.findViewById(R.id.name)).setText(machine.getName());
                _view.addView(view, lp);
            }
        }
	}
	
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.info_actions, menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()) {
		//TODO lock action
		case R.id.option_menu_refresh:
			new LoadInfoTask().execute(_group);
			return true;
		}
		return false;
	}
}
