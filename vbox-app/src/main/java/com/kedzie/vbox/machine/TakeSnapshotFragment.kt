package com.kedzie.vbox.machine

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.kedzie.vbox.R
import com.kedzie.vbox.VMAction
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IProgress
import com.kedzie.vbox.api.jaxb.LockType
import com.kedzie.vbox.api.jaxb.SessionState
import com.kedzie.vbox.soap.VBoxSvc
import com.kedzie.vbox.task.ProgressService
import kotlinx.android.synthetic.main.snapshot_dialog.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

/**
 * Create a new snapshot
 *
 * @apiviz.stereotype fragment
 */
class TakeSnapshotFragment : DialogFragment() {

    private val model: MachineListViewModel by sharedViewModel { activity!!.intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    private val args: TakeSnapshotFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle(if (args.snapshot == null) resources.getString(R.string.new_snapshot_dialog_title) else resources.getString(R.string.edit_snapshot_dialog_title))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.snapshot_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snapshot_description.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                takeSnapshot()
            }
            false
        }
        args.snapshot?.let {
            model.viewModelScope.launch {
                snapshot_name.setText(it.getName())
                snapshot_description.setText(it.getDescription())
            }
        }

        button_save.setOnClickListener {
            dismiss()
            if (args.snapshot != null) {
                model.viewModelScope.launch {
                    args.snapshot?.setName(snapshot_name.text.toString())
                    args.snapshot?.setDescription(snapshot_description.text.toString())
                }
            } else {
                takeSnapshot()
            }
        }
        button_cancel.setOnClickListener { dismiss() }
    }

    private fun handleProgress(p: IProgress, action: VMAction) =
            activity?.startService(Intent(activity, ProgressService::class.java)
                    .putExtra(IProgress.BUNDLE, p)
                    .putExtra(ProgressService.INTENT_ICON, action.drawable()))

    private fun takeSnapshot() {
        model.vmgr.value?.vbox?.let { vbox ->
            model.viewModelScope.launch {
                val session = vbox.getSessionObject()
                if (session.getState() == SessionState.UNLOCKED)
                    model.machine.value!!.lockMachine(session, LockType.SHARED)
                try {
                    handleProgress(session.getConsole().takeSnapshot(snapshot_name.text.toString(), snapshot_description.text.toString()), VMAction.TAKE_SNAPSHOT)
                } finally {
                    if (session.getState() == SessionState.LOCKED)
                        session.unlockMachine()
                }
            }
        }
    }
}