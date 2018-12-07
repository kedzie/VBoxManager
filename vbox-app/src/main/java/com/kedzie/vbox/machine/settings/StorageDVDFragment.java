package com.kedzie.vbox.machine.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.jaxb.IMediumAttachment;
import com.kedzie.vbox.api.jaxb.IMediumAttachment.Slot;
import com.kedzie.vbox.api.jaxb.StorageBus;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.DialogTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 
 * @apiviz.stereotype fragment
 */
public class StorageDVDFragment extends Fragment {

	/**
	 * Fetch DVD/Medium details
	 */
	class LoadInfoTask extends ActionBarTask<Void, Void> {

		public LoadInfoTask() { 
			super((AppCompatActivity)getActivity(), _machine.getAPI());
		}

		@Override 
		protected Void work(Void...params) throws Exception {
			if(_attachment.getMedium()!=null) 
				Utils.cacheProperties(_attachment.getMedium());
			
			_controller = _machine.getStorageControllerByName(_attachment.getController());
			_controller.getBus();
			_controller.getMaxPortCount();
			_controller.getMaxDevicesPerPortCount();
			_attachments = _machine.getMediumAttachmentsOfController(_controller.getName());
			return null;
		}
		
		@Override
		protected void onSuccess(Void result) {
			populate();
		}
	}

	/**
	 * Move the medium to a different slot within controller.
	 */
	class MoveTask extends ActionBarTask<Slot, Void> {

		public MoveTask() { 
			super((AppCompatActivity)getActivity(), _machine.getAPI());
		}

		@Override 
		protected Void work(Slot...params) throws Exception { 
			if(params[0].equals(_attachment.getSlot()))
				return null;
			_machine.detachDevice(_controller.getName(), _attachment.getPort(), _attachment.getDevice());
			if(_attachment.getMedium()!=null)
				_machine.attachDevice(_controller.getName(), params[0].port, params[0].device, _attachment.getType(), _attachment.getMedium());
			else
				_machine.attachDeviceWithoutMedium(_controller.getName(), params[0].port, params[0].device, _attachment.getType());
			return null;
		}
	}

	/**
	 * List mountable mediums
	 */
	class ListMediumsTask extends ActionBarTask<Void, List<IMedium>> {

		public ListMediumsTask() { 
			super((AppCompatActivity)getActivity(),_machine.getAPI());
		}

		@Override 
		protected List<IMedium> work(Void...params) throws Exception {
			List<IMedium> mediums = _vmgr.getVBox().getHost().getDVDDrives();
			mediums.addAll( _vmgr.getVBox().getDVDImages() );
			for(IMedium m : mediums) {
				m.getName(); m.getHostDrive();
			}
			return mediums;
		}

		@Override
		protected void onSuccess(final List<IMedium> result) {
			super.onSuccess(result);
			final CharSequence []items = new CharSequence[result.size()+1];
			for(int i=0; i<result.size(); i++) {
				IMedium m = result.get(i);
				items[i] = (m.getHostDrive() ? "Host Drive " : "") + m.getName();
			}
			items[items.length-1] = "No Disc"; 

			new AlertDialog.Builder(getContext())
			.setTitle("Select Disk")
			.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					CharSequence selected = items[item];
					new MountTask().execute(selected.equals("No Disc") ? null : result.get(item));
				}
			}).show();
		}
	}

	/**
	 * Mount different medium
	 */
	class MountTask extends DialogTask<IMedium, Void> {

		public MountTask() { 
			super((AppCompatActivity)getActivity(), _machine.getAPI(),R.string.progress_mounting_medium);
		}

		@Override 
		protected Void work(IMedium...params) throws Exception {
			if(_attachment.getMedium()!=null)
				_machine.unmountMedium(_controller.getName(), _attachment.getPort(), _attachment.getDevice(),  false);

			_attachment.setMedium(params[0]);

			if(params[0]!=null) {
				_machine.mountMedium(_controller.getName(), _attachment.getPort(), _attachment.getDevice(), params[0], false);
				params[0].getSize();
				params[0].getType();
				params[0].getLocation();
				params[0].getLogicalSize();
			}
			return null;
		}

		@Override
		protected void onSuccess(Void result) {
			super.onSuccess(result);
			populate();
		}
	}

	private IMachine _machine;
	private IMediumAttachment _attachment;
	private IStorageController _controller;
	private ArrayList<IMediumAttachment> _attachments;

	@BindView(R.id.storage_port)
	 Spinner _slotSpinner;
	private ArrayList<Slot> _slots;
	private ArrayAdapter<Slot> _slotAdapter;

	@BindView(R.id.storage_mount)
	 ImageButton _mountButton;
	@BindView(R.id.storage_type)
	 TextView _storageTypeText;
	@BindView(R.id.storage_size)
	 TextView _sizeText;
	@BindView(R.id.storage_location)
	 TextView _locationText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = getArguments().getParcelable(IMachine.BUNDLE);
		_attachment = getArguments().getParcelable(IMedium.BUNDLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_storage_details_dvd, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		_mountButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ListMediumsTask().execute();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		new LoadInfoTask().execute();
	}

	private void populate() {
		int devicesPerPort = _controller.getMaxDevicesPerPortCount();
		
		_slots = new ArrayList<Slot>(devicesPerPort*_controller.getMaxPortCount());
		for(int i=0; i<_controller.getMaxPortCount(); i++) {
			for(int j=0; j<devicesPerPort; j++) {
				Slot slot = new Slot(i, j);
				boolean isUsed=false;
				for(IMediumAttachment a : _attachments) {
					if(a.getSlot().equals(slot) && !Objects.equal(a.getMedium(),_attachment.getMedium())) {
						isUsed=true;
						break;
					}
				}
				if(!isUsed)
					_slots.add(slot);
				if(devicesPerPort==1)
					slot.name = new StringBuffer(_controller.getBus().toString()).append(" Port ").append(i).toString(); 
				else if (_controller.getBus().equals(StorageBus.IDE)) {
					slot.name = (i==0 ? "Primary " : "Secondary ") + (j==0 ? "MASTER" : "SLAVE");
				}
			}
		}
		_slotAdapter = new ArrayAdapter<Slot>(getActivity(), android.R.layout.simple_spinner_item, _slots);
		_slotSpinner.setAdapter(_slotAdapter);
		_slotSpinner.setSelection(_slots.indexOf(_attachment.getSlot()));
		
		_slotSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				new MoveTask().execute(_slotAdapter.getItem(position));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		if(_attachment.getMedium()!=null) {
			_storageTypeText.setText( _attachment.getMedium().getType().toString() );
			_sizeText.setText(_attachment.getMedium().getSize()/1024+" MB");
			_locationText.setText(_attachment.getMedium().getLocation());
		}
	}
}
