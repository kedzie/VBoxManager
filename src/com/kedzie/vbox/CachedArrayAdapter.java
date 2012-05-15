/**
 * 
 */
package com.kedzie.vbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * ArrayAdapter which stores a Map of id's to views inside each element view's tag.  
 * Saves performance on findViewById calls.
 * @author kedzie
 */
public class CachedArrayAdapter<T> extends ArrayAdapter<T> {
	protected final LayoutInflater _layoutInflater;

	public CachedArrayAdapter(Context context, List<T> objects) {
		super(context, 0, objects);
		_layoutInflater = LayoutInflater.from(context);
	}
	
	public CachedArrayAdapter(Context context, T []objects) {
		super(context, 0, objects);
		_layoutInflater = LayoutInflater.from(context);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer,View> getViewMap(View v) {
		if(v.getTag()==null)
			v.setTag(new HashMap<Integer, View>());
		return (Map<Integer,View>)v.getTag();
	}
	
	public View findViewById(View v, int id) {
		if(!getViewMap(v).containsKey(id))
			getViewMap(v).put(id, v.findViewById(id));
		return getViewMap(v).get(id);
	}
}
