package com.kedzie.vbox.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.task.DialogTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Dialog with two separate progress bars for primary/secondary operations.  Also an optional cancel button.
 */
public class VBoxProgressDialog extends DialogFragment {
	
	/** Ongoing operation */
	private IProgress _progress;
    private DialogTask _task;
	private boolean _cancelable;

    @BindView(R.id.primary_progress)
	 ProgressBar _primaryProgress;
    @BindView(R.id.secondary_progress)
	 ProgressBar _secondaryProgress;
    @BindView(R.id.message)
	 TextView _primaryText;
    @BindView(R.id.operation_description)
	 TextView _operationText;
    @BindView(R.id.operation_number)
	 TextView _operationCountText;
    @BindView(R.id.time_remaining)
	 TextView _timeRemainingText;
    @BindView(R.id.cancel_button)
	 Button _cancelButton;

    public void showAllowingStateLoss(FragmentTransaction transaction, String tag) {
//            mDismissed = false;
//            mShownByMe = true;
            transaction.add(this, tag).commitAllowingStateLoss();
//            mViewDestroyed = false;
//            mBackStackId = transaction;
//            return mBackStackId;
    }

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
		return inflater.inflate(R.layout.progress_dialog, container, false);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
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
