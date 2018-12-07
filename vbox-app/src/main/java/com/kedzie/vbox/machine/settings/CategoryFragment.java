package com.kedzie.vbox.machine.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.FragmentElementListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Marek Kędzierski
 */
public class CategoryFragment extends Fragment {

    /**
     * Listener which handles selection events
     */
    public static interface OnSelectCategoryListener {

        /**
         * Handle category selection event
         * @param position      The position of the selected element
         * @param category		the selected {@link FragmentElement}
         */
        public void onSelectCategory(int position, FragmentElement category);
    }

    private ListView _listView;
    private FragmentElementListAdapter _adapter;
    private OnSelectCategoryListener _listener;
    private boolean _dualPane;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnSelectCategoryListener)
            _listener=(OnSelectCategoryListener)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<FragmentElement> categories = new ArrayList<FragmentElement>();
        categories.add(new FragmentElement(getResources().getString(R.string.settings_general), R.drawable.ic_settings_general, GeneralFragment.class, getArguments()));
        categories.add(new FragmentElement(getResources().getString(R.string.settings_system), R.drawable.ic_settings_system, SystemFragment.class, getArguments()));
        categories.add(new FragmentElement(getResources().getString(R.string.settings_display), R.drawable.ic_settings_display, DisplayFragment.class, getArguments()));
        categories.add(new FragmentElement(getResources().getString(R.string.settings_storage), R.drawable.ic_settings_storage, StorageFragment.class, getArguments()));
        categories.add(new FragmentElement(getResources().getString(R.string.settings_audio), R.drawable.ic_settings_audio, AudioFragment.class, getArguments()));
        categories.add(new FragmentElement(getResources().getString(R.string.settings_network), R.drawable.ic_settings_network, NetworkFragment.class, getArguments()));
        _adapter = new FragmentElementListAdapter(getActivity(), categories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _dualPane = getActivity().findViewById(R.id.details)!=null;
        _listView = new ListView(getActivity());
        _listView.setAdapter(_adapter);
        _listView.setChoiceMode(_dualPane ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
        _listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelection(position);
            }
        });
        return _listView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState!=null && _dualPane) {
            int position = savedInstanceState.getInt("checkedItem");
            if(position!=ListView.INVALID_POSITION)
                setSelection(position);
        }
    }

    public void setSelection(int position) {
        if(_dualPane)
            _listView.setItemChecked(position, true);
        if(_listener!=null)
            _listener.onSelectCategory(position, _adapter.getItem(position));
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("checkedItem", _listView.getCheckedItemPosition());
    }
}
