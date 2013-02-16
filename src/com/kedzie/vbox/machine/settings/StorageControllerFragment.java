package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.jaxb.StorageControllerType;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class StorageControllerFragment extends SherlockFragment {

    class LoadInfoTask extends ActionBarTask<IStorageController, IStorageController> {
    	
        public LoadInfoTask() { 
        	super("LoadInfoTask", getSherlockActivity(), null); 
        }
        
        @Override 
        protected IStorageController work(IStorageController...params) throws Exception {
        	params[0].getName();
        	params[0].getBus();
        	params[0].getUseHostIOCache();
            return params[0];
        }
        @Override
        protected void onResult(IStorageController result) {
        	_controller=result;
            populate();
        }
    }

    private IStorageController _controller;
    
    private View _view;
    private Spinner _typeSpinner;
    private StorageControllerType[] _types;
    private ArrayAdapter<StorageControllerType> _typeAdapter;
    private CheckBox _hostIOCheckbox;
    private TextView _nameText;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_controller = getArguments().getParcelable(IStorageController.BUNDLE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_storage_controller, null);
		_typeSpinner = (Spinner)_view.findViewById(R.id.controller_type);
		_hostIOCheckbox = (CheckBox)_view.findViewById(R.id.controller_host_io_cache);
		_nameText = (TextView)_view.findViewById(R.id.controller_name);
		return _view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
			new LoadInfoTask().execute(_controller);
	}

	private void populate() {
		_nameText.setText( _controller.getName() );
		_hostIOCheckbox.setChecked(_controller.getUseHostIOCache());
		_types = StorageControllerType.getValidTypes(_controller.getBus());
		_typeAdapter = new ArrayAdapter<StorageControllerType>(getActivity(), android.R.layout.simple_spinner_item, _types);
		_typeSpinner.setAdapter(_typeAdapter);
		_typeSpinner.setSelection(Utils.indexOf(_types, _controller.getControllerType()));
	}
}
