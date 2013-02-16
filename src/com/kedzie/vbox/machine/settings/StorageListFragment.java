package com.kedzie.vbox.machine.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.ISystemProperties;
import com.kedzie.vbox.api.jaxb.ChipsetType;
import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.IMediumAttachment;
import com.kedzie.vbox.api.jaxb.StorageBus;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.DialogTask;

public class StorageListFragment extends SherlockFragment {

	public static interface OnStorageControllerClickedListener {
		public void onStorageControllerClicked(IStorageController element);
	}

	public static interface OnMediumAttachmentClickedListener {
		public void onMediumAttachmentClicked(IMediumAttachment element);
	}

	private ExpandableListView _listView;
	private ItemAdapter _listAdapter;

	private OnStorageControllerClickedListener _controllerListener;
	private OnMediumAttachmentClickedListener _mediumListener;

	private IMachine _machine;
	private ISystemProperties _systemProperties;
	private Map<IStorageController, List<IMediumAttachment>> _controllers; 

	/**
	 * Load Data
	 */
	private class LoadDataTask extends DialogTask<IMachine, Map<IStorageController, List<IMediumAttachment>>> {

		public LoadDataTask() {
			super("LoadStorageControllersTask", getSherlockActivity(), _machine.getAPI(), "Loading Storage Controllers");
		}

		@Override
		protected Map<IStorageController, List<IMediumAttachment>> work(IMachine... params) throws Exception {
			_systemProperties = _vmgr.getVBox().getSystemProperties();
			ChipsetType chipset = _machine.getChipsetType();
			for(StorageBus bus : StorageBus.values()) {
				_systemProperties.getMaxInstancesOfStorageBus(chipset, bus);
				_systemProperties.getDeviceTypesForStorageBus(bus);
			}
			Map<IStorageController, List<IMediumAttachment>> controllers = new HashMap<IStorageController, List<IMediumAttachment>>();
			for(IStorageController c : params[0].getStorageControllers()) {
				ArrayList<IMediumAttachment> attachments = params[0].getMediumAttachmentsOfController(c.getName());
				c.getBus(); c.getControllerType();
				for(IMediumAttachment a : attachments) {
					if(a.getMedium()!=null)
						a.getMedium().getName();
				}
				controllers.put(c, attachments);
			}
			return controllers;
		}

		@Override
		protected void onResult(Map<IStorageController, List<IMediumAttachment>> result) {
			super.onResult(result);
			_controllers = result;
			_listAdapter = new ItemAdapter(getSherlockActivity(), result);
			_listView.setAdapter(_listAdapter);
			for(int i=0; i<result.keySet().size(); i++)
				_listView.expandGroup(i, true);
		}
	}

	/**
	 * Add Controller
	 */
	private class AddControllerTask extends ActionBarTask<StorageBus, IStorageController> {

		public AddControllerTask() {
			super("AddControllerTask", getSherlockActivity(), _machine.getAPI());
		}

		@Override
		protected IStorageController work(StorageBus... params) throws Exception {
			IStorageController controller = _machine.addStorageController(params[0].toString(), params[0]);
			controller.getName(); controller.getBus(); controller.getControllerType();
			return controller;
		}

		@Override
		protected void onResult(IStorageController result) {
			super.onResult(result);
			_controllers.put(result, new ArrayList<IMediumAttachment>());
			_listAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Delete Controller
	 */
	private class DeleteControllerTask extends ActionBarTask<IStorageController, IStorageController> {

		public DeleteControllerTask() {
			super("DeleteControllerTask", getSherlockActivity(), _machine.getAPI());
		}

		@Override
		protected IStorageController work(IStorageController... params) throws Exception {
			_machine.removeStorageController(params[0].getName());
			return params[0];
		}

		@Override
		protected void onResult(IStorageController result) {
			super.onResult(result);
			_controllers.remove(result);
			_listAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Detach Medium
	 */
	private class DetachMediumTask extends ActionBarTask<IMediumAttachment, IMediumAttachment> {

		public DetachMediumTask() {
			super("DetachMediumTask", getSherlockActivity(), _machine.getAPI());
		}

		@Override
		protected IMediumAttachment work(IMediumAttachment... params) throws Exception {
			_machine.detachDevice(params[0].getController(), params[0].getPort(), params[0].getDevice());
			return params[0];
		}

		@Override
		protected void onResult(IMediumAttachment result) {
			super.onResult(result);
			_controllers.get(result.getController()).remove(result);
			_listAdapter.notifyDataSetChanged();
		}
	}

	private class ItemAdapter extends BaseExpandableListAdapter {

		private final LayoutInflater _inflater;
		private List<IStorageController> controllers;
		private Map<IStorageController, List<IMediumAttachment>> data;

		public ItemAdapter(Context context, Map<IStorageController, List<IMediumAttachment>> data) {
			_inflater = LayoutInflater.from(context);
			controllers = new ArrayList<IStorageController>(data.keySet().size());
			for(IStorageController controller : data.keySet())
				controllers.add(controller);
			this.data = data;
		}

		@Override
		public int getGroupCount() {
			return controllers.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return controllers.get(groupPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return data.get(controllers.get(groupPosition)).size();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return data.get(controllers.get(groupPosition)).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			final IStorageController controller = (IStorageController)getGroup(groupPosition);
			if(convertView==null) {
				convertView = _inflater.inflate(R.layout.settings_storage_controller_list_item, parent, false);
				convertView.setTag((TextView)convertView.findViewById(android.R.id.text1));
			}
			TextView text1 = (TextView)convertView.getTag();
			text1.setText(controller.getName());
			//	        text1.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			final IMediumAttachment attachment = (IMediumAttachment)getChild(groupPosition, childPosition);
			if(convertView==null) {
				convertView = _inflater.inflate(R.layout.simple_selectable_list_item, parent, false);
				convertView.setTag((TextView)convertView.findViewById(android.R.id.text1));
			}
			TextView text1 = (TextView)convertView.getTag();
			text1.setText(attachment.getMedium()!=null ? attachment.getMedium().getName() : "Empty");
			if(attachment.getType().equals(DeviceType.HARD_DISK))
				text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_hdd_c, 0, 0, 0);
			if(attachment.getType().equals(DeviceType.DVD))
				text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_dvd_c, 0, 0, 0);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		_machine = (IMachine)getArguments().getParcelable(IMachine.BUNDLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_storage_tree, container, false);
		_listView = (ExpandableListView)view.findViewById(R.id.storage_tree);
		_listView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				parent.expandGroup(groupPosition,true);
				parent.setSelectedGroup(groupPosition);
				if(_controllerListener!=null)
					_controllerListener.onStorageControllerClicked((IStorageController)_listAdapter.getGroup(groupPosition));
				return true;
			}
		});
		_listView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				parent.setSelectedChild(groupPosition, childPosition, true);
				if(_mediumListener!=null)
					_mediumListener.onMediumAttachmentClicked((IMediumAttachment)_listAdapter.getChild(groupPosition, childPosition));
				return true;
			}
		});
		registerForContextMenu(_listView);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(getParentFragment() instanceof OnStorageControllerClickedListener)
			_controllerListener = (OnStorageControllerClickedListener)getParentFragment();
		if(getParentFragment() instanceof OnMediumAttachmentClickedListener)
			_mediumListener = (OnMediumAttachmentClickedListener)getParentFragment();
	}

	@Override
	public void onStart() {
		super.onStart();
		if(_controllers==null)
			new LoadDataTask().execute(_machine);
		else {
			_listAdapter = new ItemAdapter(getSherlockActivity(), _controllers);
			_listView.setAdapter(_listAdapter);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.storage_actions, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_add_controller:
				List<CharSequence> itemList = new ArrayList<CharSequence>(5);
				ChipsetType chipset = _machine.getChipsetType();
				for(StorageBus bus : Utils.removeNull(StorageBus.values())) {
					if(getNumStorageControllersOfType(bus)<_systemProperties.getMaxInstancesOfStorageBus(chipset, bus))
						itemList.add(bus.toString());
				}
				final CharSequence[] items = itemList.toArray(new CharSequence[itemList.size()]);
				new AlertDialog.Builder(getActivity())
					.setTitle("Controller Type")
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							final StorageBus bus = StorageBus.fromValue(items[item].toString());
							new AddControllerTask().execute(bus);
							Utils.toastLong(getActivity(), bus.toString());
						}
					}).show();
				return true;
		}
		return false;
	}
	
	private int getNumStorageControllersOfType(StorageBus bus) {
		int numControllers = 0;
		for(IStorageController controller : _controllers.keySet()) {
			if(controller.getBus().equals(bus))
				numControllers++;
		}
		return numControllers;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(Menu.NONE, R.id.menu_delete, Menu.NONE, R.string.delete);
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		int groupNum = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        switch(ExpandableListView.getPackedPositionType(info.packedPosition)) {
        	case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
        		IStorageController controller = (IStorageController)_listAdapter.getGroup(groupNum);
        		new DeleteControllerTask().execute(controller);
        		break;
        	case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
        		int childNum = ExpandableListView.getPackedPositionChild(info.packedPosition);
        		IMediumAttachment attachment = (IMediumAttachment)_listAdapter.getChild(groupNum, childNum);
        		new DetachMediumTask().execute(attachment);
        		break;
        }
		return false;
	}
}
