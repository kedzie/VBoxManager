package com.kedzie.vbox.machine.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IAudioAdapter;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.INetworkAdapter;
import com.kedzie.vbox.api.jaxb.AudioControllerType;
import com.kedzie.vbox.api.jaxb.AudioDriverType;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.task.DialogTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Edit remote desktop server
 * @apiviz.stereotype fragment
 */
public class AudioFragment extends Fragment {
	
	class LoadInfoTask extends DialogTask<IMachine, Tuple<IAudioAdapter, AudioDriverType[]>> {
		
		public LoadInfoTask() { 
			super((AppCompatActivity)getActivity(), _machine.getAPI(), R.string.progress_loading_data_generic);
		}

		@Override 
		protected Tuple<IAudioAdapter, AudioDriverType[]> work(IMachine...params) throws Exception {
			IAudioAdapter adapter = params[0].getAudioAdapter();
			adapter.getEnabled();
			adapter.getAudioController();
			adapter.getAudioDriver();
			AudioDriverType[] types = AudioDriverType.getAudioDrivers(_vmgr.getVBox().getHost().getOperatingSystem());
			return new Tuple<IAudioAdapter, AudioDriverType[]>(adapter, types);
		}
		@Override
		protected void onSuccess(Tuple<IAudioAdapter, AudioDriverType[]> result) {
			super.onSuccess(result);
			_adapter = result.first;
		    _types = result.second;
		    _audioDriverAdapter = new ArrayAdapter<AudioDriverType>(getActivity(), android.R.layout.simple_spinner_item, _types);
			_audioDriverSpinner.setAdapter(_audioDriverAdapter);
		    populate();
		}
	}
	
	private IMachine _machine;
	private IAudioAdapter _adapter;
	private AudioDriverType[] _types;
	
    @BindView(R.id.enabled)
	 CheckBox _enabledCheckBox;
    @BindView(R.id.audio_driver)
	 Spinner _audioDriverSpinner;
	private ArrayAdapter<AudioDriverType> _audioDriverAdapter;
    @BindView(R.id.audio_controller)
	 Spinner _audioControllerSpinner;
	private ArrayAdapter<AudioControllerType> _audioControllerAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_machine = getArguments().<IMachine>getParcelable(IMachine.BUNDLE);
		if(savedInstanceState!=null) {
		    _adapter = savedInstanceState.getParcelable(IAudioAdapter.BUNDLE);
		    _types = (AudioDriverType[])savedInstanceState.getSerializable("types");
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putSerializable("types", _types);
        outState.putParcelable(INetworkAdapter.BUNDLE, _adapter);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_audio_adapter, null);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        _audioControllerAdapter = new ArrayAdapter<AudioControllerType>(getActivity(), android.R.layout.simple_spinner_item, AudioControllerType.values());
        _audioControllerSpinner.setAdapter(_audioControllerAdapter);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(_adapter!=null) 
			populate();
		else 
			new LoadInfoTask().execute(_machine);
	}

	private void populate() {
		_enabledCheckBox.setChecked(_adapter.getEnabled());
		_enabledCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_adapter.setEnabled(isChecked);
			}
		});
		_audioControllerSpinner.setSelection(Utils.indexOf(AudioControllerType.values(), _adapter.getAudioController()));
		_audioControllerSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_adapter.setAudioController(_audioControllerAdapter.getItem(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});
		_audioDriverSpinner.setSelection(Utils.indexOf(_types, _adapter.getAudioDriver()));
		_audioDriverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				_adapter.setAudioDriver(_audioDriverAdapter.getItem(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
}