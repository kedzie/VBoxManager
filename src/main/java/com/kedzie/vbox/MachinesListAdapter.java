/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kedzie.vbox;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kedzie.vbox.api.IMachine;

public class MachinesListAdapter extends BaseAdapter {
	private List<IMachine> _machines;
	private final LayoutInflater _layoutInflater;

	public MachinesListAdapter(Context context, List<IMachine> machines) {
		_machines = machines;
		_layoutInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return _machines.size();
	}

	public IMachine getItem(int position) {
		return _machines.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = _layoutInflater.inflate(R.layout.machine_list_item, parent, false);
		TextView t = (TextView) view.findViewById(R.id.machine_name); 
		t.setText(getItem(position).getName() + " - " +  getItem(position).getState());
		return view;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return super.getDropDownView(position, convertView, parent);
	}
}
