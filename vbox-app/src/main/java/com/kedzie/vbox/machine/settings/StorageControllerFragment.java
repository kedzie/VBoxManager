package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.jaxb.StorageControllerType;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.ActionBarTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class StorageControllerFragment extends Fragment {

    private class LoadInfoTask extends ActionBarTask<IStorageController, IStorageController> {
    	
        public LoadInfoTask() { 
        	super((AppCompatActivity)getActivity(), null);
        }
        
        @Override 
        protected IStorageController work(IStorageController...params) throws Exception {
        	params[0].getName();
        	params[0].getBus();
        	params[0].getUseHostIOCache();
            return params[0];
        }
        @Override
        protected void onSuccess(IStorageController result) {
        	_controller=result;
            populate();
        }
    }

    private IStorageController _controller;

	@BindView(R.id.controller_host_io_cache)
     CheckBox _hostIOCheckbox;
	@BindView(R.id.controller_name)
     TextView _nameText;
	@BindView(R.id.controller_type)
	 Spinner _typeSpinner;
	private StorageControllerType[] _types;
	private ArrayAdapter<StorageControllerType> _typeAdapter;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_controller = getArguments().getParcelable(IStorageController.BUNDLE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_storage_controller, null);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
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
