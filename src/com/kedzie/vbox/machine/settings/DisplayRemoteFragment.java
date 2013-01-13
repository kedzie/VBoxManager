package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @apiviz.stereotype fragment
 */
public class DisplayRemoteFragment extends SherlockFragment {
	
	class LoadInfoTask extends ActionBarTask<IMachine, IHost> {
		public LoadInfoTask() { super("DisplayRemoteFragment", getSherlockActivity(), _machine.getVBoxAPI()); }
		@Override 
		protected IHost work(IMachine... m) throws Exception {
			IHost host = _vmgr.getVBox().getHost();
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
		if(savedInstanceState!=null) {
            _host = BundleBuilder.getProxy(savedInstanceState, "host", IHost.class);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		_view = inflater.inflate(R.layout.settings_display_video, null);
		_view = new FrameLayout(getActivity());
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
		BundleBuilder.putProxy(outState, "host", _host);
	}

	private void populateViews(IMachine m, IHost host) {
	}
}