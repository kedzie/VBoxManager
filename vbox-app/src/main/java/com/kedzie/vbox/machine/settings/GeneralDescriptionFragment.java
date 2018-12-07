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
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @apiviz.stereotype fragment
 */
public class GeneralDescriptionFragment extends Fragment {

    class LoadInfoTask extends ActionBarTask<IMachine, IMachine> {
    	
        public LoadInfoTask() { 
        	super((AppCompatActivity)getActivity(), null);
        }
        
        @Override 
        protected IMachine work(IMachine... m) throws Exception {
            m[0].getDescription();
            return m[0];
        }
        
        @Override
        protected void onSuccess(IMachine result) {
            _machine = result;
            populate();
        }
    }

    private IMachine _machine;
    private View _view;
    private EditText _descriptionText;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.settings_general_description, null);
		_descriptionText = (EditText)_view.findViewById(R.id.description);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_machine.getCache().containsKey("getDescription")) 
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
		_descriptionText.setText( _machine.getDescription()+"" );
		_descriptionText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				_machine.setDescription(_descriptionText.getText().toString());
			}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
		});
	}
}
