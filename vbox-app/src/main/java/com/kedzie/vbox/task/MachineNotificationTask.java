package com.kedzie.vbox.task;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.IVirtualBoxErrorInfo;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;

import java.io.IOException;

/**
 * Created by mkedzierski on 1/16/16.
 */
public class MachineNotificationTask<Input, Output> extends MachineTask<Input, Output> {

    private int icon;

    public MachineNotificationTask(AppCompatActivity context, VBoxSvc vmgr, int msg, int icon, boolean indeterminate, IMachine m) {
        super(context, vmgr, msg, indeterminate, m);
        this.icon=icon;
    }

    public MachineNotificationTask(AppCompatActivity context, VBoxSvc vmgr, String msg, int icon, boolean indeterminate, IMachine m) {
        super(context, vmgr, msg, indeterminate, m);
        this.icon = icon;
    }

    @Override
    protected void handleProgress(IProgress p) throws IOException {
        Log.d(TAG, "Operation Completed. result code: " + p.getResultCode());
        Intent intent = new Intent(getContext(), ProgressService.class)
                .putExtra(IProgress.BUNDLE, p)
                .putExtra(ProgressService.INTENT_ICON, icon);
        getContext().startService(intent);
        return;
    }
}
