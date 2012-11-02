package com.kedzie.vbox.machine.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.kedzie.vbox.R;
import com.kedzie.vbox.app.TabFragmentInfo;

/**
 * 
 * @author Marek Kędzierski
 */
public class CategoryFragment extends SherlockFragment {
    
    public static interface OnSelectCategoryListener {
        public void onSelectCategory(TabFragmentInfo category);
    }
    
    class CategoryAdapter extends ArrayAdapter<TabFragmentInfo> {
        private LayoutInflater inflater;
        
        public CategoryAdapter(Context context, List<TabFragmentInfo> objects) {
            super(context, 0, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TabFragmentInfo info = getItem(position);
            View view = inflater.inflate(R.layout.simple_list_item, null);
            ((ImageView)view.findViewById(R.id.list_item_icon)).setImageResource(info.icon);
            ((TextView)view.findViewById(R.id.list_item_text)).setText(info.name);
            return view;
        }
    }
    
    private ListView _listView;
    private CategoryAdapter _adapter;
    private OnSelectCategoryListener _listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnSelectCategoryListener)
            _listener=(OnSelectCategoryListener)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<TabFragmentInfo> categories = new ArrayList<TabFragmentInfo>();
        categories.add(new TabFragmentInfo("General", R.drawable.ic_settings_general, GeneralFragment.class, getArguments()));
        categories.add(new TabFragmentInfo("System", R.drawable.ic_settings_general, SystemFragment.class, getArguments()));
        categories.add(new TabFragmentInfo("Display", R.drawable.ic_settings_display, DisplayFragment.class, getArguments()));
        _adapter = new CategoryAdapter(getActivity(), categories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _listView = new ListView(getActivity());
        _listView.setAdapter(_adapter);
        _listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(_listener!=null)
                    _listener.onSelectCategory(_adapter.getItem(position));
            }
        });
        return _listView;
    }
}
