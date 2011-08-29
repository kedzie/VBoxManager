package com.kedzie.vbox.machine;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.kedzie.vbox.BaseActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.task.BaseTask;

public class SnapshotActivity extends BaseActivity {

	private VBoxSvc _vmgr;
	private IMachine _machine;
	private ListView _listView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_list);
        _listView = (ListView)findViewById(R.id.list);
        _vmgr =getIntent().getParcelableExtra("vmgr");
		_machine = _vmgr.getProxy(IMachine.class, getIntent().getStringExtra("machine"));
		new LoadSnapshotsTask(this, _vmgr).execute(_machine);
    }
	
	class LoadSnapshotsTask extends BaseTask<IMachine, List<ISnapshot>>	{
		public LoadSnapshotsTask(Context ctx, VBoxSvc vmgr) { super( ctx, vmgr, "Loading Snapshots", true); 	}

		@Override
		protected List<ISnapshot> work(IMachine... params) throws Exception {
			List<ISnapshot> snapshots = new ArrayList<ISnapshot>();
			ISnapshot c = params[0].getCurrentSnapshot();
			while(c!=null) {
				snapshots.add(c);
				c = c.getParent();
			}
			return snapshots;
		}

		@Override
		protected void onPostExecute(List<ISnapshot> result)	{
			super.onPostExecute(result);
			if(result!=null)	_listView.setAdapter(new SnapshotAdapter(SnapshotActivity.this, result));
		}
	}
	
	class SnapshotAdapter extends ArrayAdapter<ISnapshot> {
		private final LayoutInflater _layoutInflater;
		
		public SnapshotAdapter(Context context, List<ISnapshot> snapshots) {
			super(context, 0, snapshots);
			_layoutInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) { 
				view = _layoutInflater.inflate(R.layout.machine_action_item, parent, false);
				((TextView)view.findViewById(R.id.action_item_text)).setText(getItem(position).getName());
				((ImageView)view.findViewById(R.id.action_item_icon)).setImageResource( R.drawable.ic_list_snapshot_c);
			}
			return view;
		}
	}
}
