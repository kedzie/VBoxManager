package com.kedzie.vbox.machine

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.api.IMachine
import kotlinx.android.synthetic.main.machine_view.view.*

class MachineView(context: Context) : FrameLayout(context) {

    lateinit var machine: IMachine
        private set

    init {
        isFocusable = true
        LayoutInflater.from(context).inflate(R.layout.machine_view, this, true)
    }

    suspend fun update(m: IMachine) {
        machine = m
        this.contentDescription = "Virtual Machine: " + m.getName()
        machine_list_item_ostype.setImageResource(VBoxApplication.getOSDrawable(context, m.getOSTypeId()))
        machine_list_item_name.text = m.getName()
        machine_list_item_state.setImageResource(m.getStateNoCache().drawable())
        machine_list_item_state_text.text = m.getState().value()
        machine_list_item_snapshot.text = m.getCurrentSnapshotNoCache()?.let {
            StringBuffer("(").append(it.getName()).append(")").append(if (m.getCurrentStateModifiedNoCache()) "*" else "").toString()
        } ?: ""
    }
}
