package com.kedzie.vbox.task;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.app.VBoxProgressDialog;

/**
 * Created by kedzie on 3/1/14.
 */
public class TaskFragment extends SherlockFragment {
    private static final String TAG = "TaskFragment";

    private DialogTask task;

    public TaskFragment(DialogTask task) {
        this.task=task;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(task.getProgressDialog()==null) {
            task.setActivity((SherlockFragmentActivity) getActivity());
            Log.v(TAG, "Creating new progress dialog");
            task.onPreExecute();
        }
    }

    @Override
    public void onDetach() {
        task.setProgressDialog(null);
        super.onDetach();
    }
}
