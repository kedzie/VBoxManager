package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.IGuestOSType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.task.ActionBarTask;

import java.util.ArrayList;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class GeneralBasicFragment extends Fragment {

    class LoadInfoTask extends ActionBarTask<IMachine, Tuple<IMachine, IGuestOSType>> {
    	
        public LoadInfoTask() { 
        	super((AppCompatActivity)getActivity(), _machine.getAPI());
        }
        
        @Override 
        protected Tuple<IMachine, IGuestOSType> work(IMachine... m) throws Exception {
            m[0].getName();
            _guestOSTypes = _vmgr.getVBox().getGuestOSTypes();
            return new Tuple<IMachine, IGuestOSType>(m[0],  _vmgr.getVBox().getGuestOSType(m[0].getOSTypeId()));
        }
        @Override
        protected void onSuccess(Tuple<IMachine, IGuestOSType> result) {
            _machine = result.first;
            _guestOSType = result.second;
            populate();
        }
    }

    private IMachine _machine;
    private IGuestOSType _guestOSType;
    ArrayList<IGuestOSType> _guestOSTypes;
    
    private View _view;
    private EditText _nameText;
    private EditText _osTypeText;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_general, null);
		_nameText = (EditText)_view.findViewById(R.id.name);
		_osTypeText = (EditText)_view.findViewById(R.id.ostype);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_machine.getCache().containsKey("getName") && _guestOSType!=null) 
			populate();
		else 
			new LoadInfoTask().execute(_machine);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
	}

	private void populate() {
		_nameText.setText( _machine.getName()+"" );
		_nameText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				_machine.setName(_nameText.getText().toString());
			}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
		_osTypeText.setText( _machine.getOSTypeId()+"" );
	}
}
