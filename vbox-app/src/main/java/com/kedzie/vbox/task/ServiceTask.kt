package com.kedzie.vbox.task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.kedzie.vbox.api.IProgress
import com.kedzie.vbox.soap.VBoxSvc
import java.io.IOException

internal abstract class ServiceTask<Input, Output>(context: AppCompatActivity, vmgr: VBoxSvc, val icon: Int) : BaseTask<Input, Output>(context, vmgr) {

    @Throws(IOException::class)
    override fun handleProgress(p: IProgress?) {
        context.startService(Intent(context, ProgressService::class.java)
                .putExtra(IProgress.BUNDLE, p)
                .putExtra(ProgressService.INTENT_ICON, icon))
        return
    }
}