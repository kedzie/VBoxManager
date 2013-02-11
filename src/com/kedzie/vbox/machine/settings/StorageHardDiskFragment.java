package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.jaxb.IMediumAttachment;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class StorageHardDiskFragment extends SherlockFragment {

    class LoadInfoTask extends ActionBarTask<Void, IStorageController> {
    	
        public LoadInfoTask() { 
        	super("LoadInfoTask", getSherlockActivity(), _attachment.getMedium().getAPI()); 
        }
        
        @Override 
        protected IStorageController work(Void...params) throws Exception {
        	_attachment.getMedium().getSize();
        	_attachment.getMedium().getType();
        	_attachment.getMedium().getLocation();
        	_attachment.getMedium().getLogicalSize();
        	IStorageController controller = _machine.getStorageControllerByName(_attachment.getController());
        	controller.getBus();
        	controller.getMaxPortCount();
            return controller;
        }
        @Override
        protected void onResult(IStorageController result) {
        	_controller=result;
            populate();
        }
    }

    private IMachine _machine;
    private IMediumAttachment _attachment;
    private IStorageController _controller;
    
    private View _view;
    private Spinner _portSpinner;
    private ImageButton _mountButton;
    private CheckBox _solidStateCheckbox;
    private TextView _storageTypeText;
    private TextView _virtualSizeText;
    private TextView _actualSizeText;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = getArguments().getParcelable(IMachine.BUNDLE);
		_attachment = getArguments().getParcelable(IMedium.BUNDLE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_storage_details_harddisk, null);
		_portSpinner = (Spinner)_view.findViewById(R.id.storage_port);
		_mountButton = (ImageButton)_view.findViewById(R.id.storage_mount);
		_solidStateCheckbox = (CheckBox)_view.findViewById(R.id.storage_solid_state);
		_storageTypeText = (TextView)_view.findViewById(R.id.storage_type);
		_virtualSizeText = (TextView)_view.findViewById(R.id.storage_virtual_size);
		_actualSizeText = (TextView)_view.findViewById(R.id.storage_actual_size);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new LoadInfoTask().execute();
	}

	private void populate() {
		_storageTypeText.setText( _attachment.getMedium().getType().toString() );
		_virtualSizeText.setText(_attachment.getMedium().getLogicalSize()+" MB");
		_actualSizeText.setText(_attachment.getMedium().getSize()+" MB");
	}
}
