package com.kedzie.vbox;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ServerListAdapter extends BaseAdapter {
	private List<Server> _servers;
	private final LayoutInflater _layoutInflater;

	public ServerListAdapter(Context context, List<Server> s) {
		_servers = s;
		_layoutInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return _servers.size();
	}

	public Server getItem(int position) {
		return _servers.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		Server s = getItem(position);
		if (view == null) {
			view = _layoutInflater.inflate(R.layout.server_list_item, parent, false);
		}
		return view;
	}
	
}
