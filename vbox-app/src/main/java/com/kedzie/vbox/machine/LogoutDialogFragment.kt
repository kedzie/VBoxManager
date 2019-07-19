package com.kedzie.vbox.machine

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import com.kedzie.vbox.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class LogoutDialogFragment : DialogFragment() {

    private val model: MachineListViewModel by sharedViewModel { parametersOf(activity!!) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!, theme)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    model.viewModelScope.launch {
                        model.vmgr.value?.logoff()
                        model.vmgr.postValue(null)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
                .create()
    }
}
