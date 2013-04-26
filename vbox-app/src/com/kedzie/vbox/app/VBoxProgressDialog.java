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

/**
 * Dialog with two separate progress bars for primary/secondary operations.  Also an optional cancel button.
 */
public class VBoxProgressDialog extends SherlockDialogFragment {
	
	/** Ongoing operation */
	private IProgress _progress;
	
	private OnCancelListener _cancelListener;
	private boolean _cancelable;
	
	private ProgressBar _primaryProgress;
	private ProgressBar _secondaryProgress;
	private TextView _primaryText;
	private TextView _operationText;
	private TextView _operationCountText;
	private TextView _timeRemainingText;
	private Button _cancelButton;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = getLayoutInflater(savedInstanceState).inflate(R.layout.progress_dialog, container, false);
		_primaryText = (TextView)view.findViewById(R.id.message);
		_primaryProgress = (ProgressBar)view.findViewById(R.id.primary_progress);
		_operationText = (TextView)view.findViewById(R.id.operation_description);
		_operationCountText = (TextView)view.findViewById(R.id.operation_number);
		_secondaryProgress = (ProgressBar)view.findViewById(R.id.secondary_progress);
		_timeRemainingText = (TextView)view.findViewById(R.id.time_remaining);
		_cancelButton = (Button)view.findViewById(R.id.cancel_button);
		_cancelButton.setEnabled(_cancelable);
		_cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
				if(_cancelable && _progress!=null)
					_progress.cancel();
				if(_cancelListener!=null)
					_cancelListener.onCancel(getDialog());
			}
		});
		return view;
	}

	public void update(IProgress progress) {
		_progress=progress;
		if(_primaryProgress==null) return;
		_primaryProgress.setProgress(progress.getPercent());
		_primaryText.setText(progress.getDescription());
		_secondaryProgress.setProgress(progress.getOperationPercent());
		_operationText.setText(progress.getOperationDescription());
		_operationCountText.setText(progress.getOperation()+"/"+progress.getOperationCount());
		_timeRemainingText.setText(progress.getTimeRemaining()+" seconds");
		_cancelable = progress.getCancelable();
	}
	
	public void setOnCancelListener(OnCancelListener listener) {
		_cancelListener = listener;
	}
	
	public void setCancelable(boolean cancelable) {
		_cancelable=cancelable;
	}
}
