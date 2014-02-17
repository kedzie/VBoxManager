package com.kedzie.vbox.app;

import android.app.Dialog;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.task.DialogTask;
import roboguice.fragment.RoboSherlockDialogFragment;
import roboguice.inject.InjectView;

/**
 * Dialog with two separate progress bars for primary/secondary operations.  Also an optional cancel button.
 */
public class VBoxProgressDialog extends RoboSherlockDialogFragment {
	
	/** Ongoing operation */
	private IProgress _progress;
    private DialogTask _task;
	private boolean _cancelable;

    @InjectView(R.id.primary_progress)
	private ProgressBar _primaryProgress;
    @InjectView(R.id.secondary_progress)
	private ProgressBar _secondaryProgress;
    @InjectView(R.id.message)
	private TextView _primaryText;
    @InjectView(R.id.operation_description)
	private TextView _operationText;
    @InjectView(R.id.operation_number)
	private TextView _operationCountText;
    @InjectView(R.id.time_remaining)
	private TextView _timeRemainingText;
    @InjectView(R.id.cancel_button)
	private Button _cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _cancelable = getArguments().getBoolean("cancelable");
    }

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return getLayoutInflater(savedInstanceState).inflate(R.layout.progress_dialog, container, false);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _primaryText.setText(getArguments().getString("msg"));
        _cancelButton.setEnabled(_cancelable);
        _cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                if(_task!=null)
                    _task.cancel(true);
                if(_cancelable && _progress!=null)
                    _progress.cancel();
            }
        });
    }

    public void update(IProgress progress) {
		_progress=progress;
		if(_primaryProgress==null) return;
        setIndeterminate(false);
		_primaryProgress.setProgress(progress.getPercent());
		_primaryText.setText(progress.getDescription());
		_secondaryProgress.setProgress(progress.getOperationPercent());
		_operationText.setText(progress.getOperationDescription());
		_operationCountText.setText(progress.getOperation()+"/"+progress.getOperationCount());
		_timeRemainingText.setText(progress.getTimeRemaining()+" seconds");
        setCancelable(progress.getCancelable());
	}
	
	public void setCancelable(boolean cancelable) {
		_cancelable=cancelable;
        if(_cancelButton!=null)
            _cancelButton.setEnabled(_cancelable);
	}

    public void setIndeterminate(boolean indeterminate) {
        _primaryProgress.setIndeterminate(indeterminate);
        _secondaryProgress.setIndeterminate(indeterminate);
    }

    public void setTask(DialogTask<?, ?> task) {
        _task=task;
    }
}
