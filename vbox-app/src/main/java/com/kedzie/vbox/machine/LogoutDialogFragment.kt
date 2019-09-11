package com.kedzie.vbox.machine

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.kedzie.vbox.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class LogoutDialogFragment : DialogFragment() {

    private val model: MachineListViewModel by sharedViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!, theme)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.ok) { _, _ ->
                    model.viewModelScope.launch {
                        model.vbox.value?.logoff()
                        model.vbox.value = null
                    }
                    findNavController().popBackStack()
                }
                .setNegativeButton(R.string.cancel) { _, _ -> findNavController().popBackStack() }
                .create()
    }
}
