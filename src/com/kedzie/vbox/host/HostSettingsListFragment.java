package com.kedzie.vbox.host;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.FragmentElementListAdapter;

/**
 * 
 */
public class HostSettingsListFragment extends SherlockFragment {
    
    public static interface OnSelectHostCategoryListener {
        public void onSelectCategory(FragmentElement category);
    }
    
    private ListView _listView;
    private FragmentElementListAdapter _adapter;
    private OnSelectHostCategoryListener _listener;
    private boolean _dualPane;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnSelectHostCategoryListener)
            _listener=(OnSelectHostCategoryListener)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<FragmentElement> categories = new ArrayList<FragmentElement>();
        categories.add(new FragmentElement("Host Network Interfaces", R.drawable.ic_settings_network, HostNetworkListFragment.class, getArguments()));
        _adapter = new FragmentElementListAdapter(getActivity(), categories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _listView = new ListView(getActivity());
        _listView.setAdapter(_adapter);
        _dualPane = getActivity().findViewById(R.id.details)!=null;
        _listView.setChoiceMode(_dualPane ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
        _listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	_listView.setItemChecked(position, true);
                if(_listener!=null)
                    _listener.onSelectCategory(_adapter.getItem(position));
            }
        });
        return _listView;
    }
}
