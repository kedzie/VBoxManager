package com.kedzie.vbox.app;

import java.util.List;

import com.kedzie.vbox.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * {@link ListAdapter} for {@link FragmentElement}s
 */
public class FragmentElementListAdapter extends ArrayAdapter<FragmentElement> {

	private LayoutInflater inflater;
    
    public FragmentElementListAdapter(Context context, List<FragmentElement> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FragmentElement info = getItem(position);
        if(convertView==null) {
        	convertView = inflater.inflate(R.layout.simple_selectable_list_item, parent, false);
        	convertView.setTag((TextView)convertView.findViewById(android.R.id.text1));
        }
        TextView text1 = (TextView)convertView.getTag();
        text1.setText(info.name);
        text1.setCompoundDrawablesWithIntrinsicBounds(info.icon, 0, 0, 0);
        return convertView;
    }
}
