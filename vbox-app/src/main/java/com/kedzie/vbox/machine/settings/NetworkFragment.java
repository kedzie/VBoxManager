package com.kedzie.vbox.machine.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.INetworkAdapter;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.PagerTabHost;
import com.kedzie.vbox.task.ActionBarTask;

import java.util.ArrayList;

public class NetworkFragment extends Fragment {
    private PagerTabHost mTabHost;
    
    private IMachine _machine;
    private ArrayList<INetworkAdapter> _adapters;
    private int mSavedTab;

    public static class DummyFragment extends Fragment {
    	public DummyFragment() {
    		super();
    	}
    }
    
    /**
     * Load all the network adapters and create a tab for each one
     */
    class LoadDataTask extends ActionBarTask<IMachine, ArrayList<INetworkAdapter>> {
    	
    	public LoadDataTask() {
    		super((AppCompatActivity)getActivity(), _machine.getAPI());
    	}
    	
    	@Override
    	protected ArrayList<INetworkAdapter> work(IMachine... params) throws Exception {
    		int maxNumAdapters = 4;
    		ArrayList<INetworkAdapter> adapters = new ArrayList<INetworkAdapter>(maxNumAdapters);
    		for(int i=0; i<maxNumAdapters; i++) 
    			adapters.add(params[0].getNetworkAdapter(i));
    		Log.d(TAG, "Loaded " + adapters.size() + " network adapters");
    		return adapters;
    	}
    	
    	@Override
    	protected void onSuccess(ArrayList<INetworkAdapter> result) {
    		super.onSuccess(result);
    		_adapters = result;
    		populate();
    	}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putParcelableArrayList("adapters", _adapters);
        outState.putInt("tab", mTabHost.getCurrentTab());
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	_machine = (IMachine)getArguments().getParcelable(IMachine.BUNDLE);
    	if(savedInstanceState!=null) {
    		_adapters = savedInstanceState.getParcelableArrayList("adapters");
            mSavedTab = savedInstanceState.getInt("tab", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = new PagerTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(new FragmentElement("dummy", DummyFragment.class, new Bundle()));
        return mTabHost;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	if(_adapters==null)
    		new LoadDataTask().execute(_machine);
    	else
			populate();
    }
    
    void populate() {
    	mTabHost.removeAllTabs();
    	for(int i=0; i<_adapters.size(); i++)
    	    mTabHost.addTab(new FragmentElement("Adapter#"+(i+1), NetworkAdapterFragment.class, 
    	            new BundleBuilder().putParcelable(INetworkAdapter.BUNDLE, _adapters.get(i)).create()));
        mTabHost.setCurrentTab(mSavedTab);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}

