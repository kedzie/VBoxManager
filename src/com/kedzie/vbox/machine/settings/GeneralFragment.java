package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class GeneralFragment extends SherlockFragment {

    class LoadInfoTask extends ActionBarTask<IMachine, IMachine> {
        public LoadInfoTask() { super("LoadInfoTask", getSherlockActivity(), null); }
        @Override 
        protected IMachine work(IMachine... m) throws Exception {
            //cache values
            m[0].getName(); m[0].getOSTypeId(); m[0].getDescription();
            return m[0];
        }
        @Override
        protected void onResult(IMachine result) {
            _machine = result;
            populateViews(result);
        }
    }

    private IMachine _machine;
    private View _view;
    private EditText _nameText;
    private EditText _descriptionText;
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
		_descriptionText = (EditText)_view.findViewById(R.id.description);
		_osTypeText = (EditText)_view.findViewById(R.id.ostype);
		return _view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState!=null) 
			populateViews(_machine);
		else 
			new LoadInfoTask().execute(_machine);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
	}

	private void populateViews(IMachine m) {
		_nameText.setText( m.getName()+"" );
		_osTypeText.setText( m.getOSTypeId()+"" );
		_descriptionText.setText( m.getDescription()+"" );
	}
}
