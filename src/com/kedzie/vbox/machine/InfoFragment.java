package com.kedzie.vbox.machine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.task.BaseTask;

public class InfoFragment extends SherlockFragment {

	private IMachine _machine;
	private View _view;
	private TextView _nameText;
	private TextView _descriptionText;
	private TextView _osTypeText;
	private TextView _baseMemoryText;
	private TextView _processorsText;
	private TextView _bootOrderText;
	private TextView _accelerationText;
	private TextView _videoMemoryText;
	private TextView _accelerationVideoText;
	private TextView _rdpPortText;
	
	public static InfoFragment getInstance(Bundle args) {
		InfoFragment f = new InfoFragment();
		f.setArguments(args);
		return f;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.machine_info, null);
		_nameText = (TextView)_view.findViewById(R.id.name);
		_descriptionText = (TextView)_view.findViewById(R.id.description);
		_osTypeText = (TextView)_view.findViewById(R.id.ostype);
		_baseMemoryText = (TextView)_view.findViewById(R.id.baseMemory);
		_processorsText = (TextView)_view.findViewById(R.id.processors);
		_bootOrderText = (TextView)_view.findViewById(R.id.bootOrder);
		_accelerationText = (TextView)_view.findViewById(R.id.acceleration);
		_videoMemoryText = (TextView)_view.findViewById(R.id.videoMemory);
		_accelerationVideoText = (TextView)_view.findViewById(R.id.accelerationVideo);
		_rdpPortText = (TextView)_view.findViewById(R.id.rdpPort);
		return _view;
	}
	
	private void populateViews(IMachine m) {
		_nameText.setText( m.getName()+"" );
		_osTypeText.setText( m.getOSTypeId()+"" );
		_baseMemoryText.setText( m.getMemorySize()+"" );
		_processorsText.setText( m.getCPUCount()+"" );
		_bootOrderText.setText( "" );
		_accelerationText.setText("" );
		_videoMemoryText.setText( m.getVRAMSize()+"" );
		_accelerationVideoText.setText( (m.getAccelerate2DVideoEnabled() ? "2D" : "") + " " +  (m.getAccelerate3DEnabled() ? "3D" : "") );
		_rdpPortText.setText( "NaN" );
		_descriptionText.setText( m.getDescription()+"" );
	}

	class LoadInfoTask extends BaseTask<IMachine, IMachine> {

		public LoadInfoTask() { super(LoadInfoTask.class.getSimpleName(), getSherlockActivity(), null); }

		@Override 
		protected IMachine work(IMachine... m) throws Exception {
			//cache values
			m[0].getName(); m[0].getOSTypeId(); m[0].getMemorySize();
			m[0].getCPUCount(); m[0].getVRAMSize(); m[0].getAccelerate2DVideoEnabled();
			m[0].getAccelerate3DEnabled(); m[0].getDescription();
			return m[0];
		}

		@Override
		protected void onPostExecute(IMachine result) {
			super.onPostExecute(result);
			if(result!=null) {
				_machine = result;
				populateViews(result);
			}
		}
	}
}
