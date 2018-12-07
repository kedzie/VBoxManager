package com.kedzie.vbox.task;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kedzie on 3/1/14.
 */
public class TaskFragment extends Fragment {

    private DialogTask task;

    public TaskFragment() {
    }

    public DialogTask getTask() {
        return task;
    }

    public TaskFragment setTask(DialogTask task) {
        this.task = task;
        return this;
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
            task.setActivity((AppCompatActivity) getActivity());
            task.onPreExecute();
        }
    }

    @Override
    public void onDetach() {
        task.setProgressDialog(null);
        super.onDetach();
    }
}
