package com.kedzie.vbox.machine

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.api.IMachineEntity
import kotlinx.android.synthetic.main.machine_view.view.*

class MachineView(context: Context) : FrameLayout(context) {

    init {
        isFocusable = true
        LayoutInflater.from(context).inflate(R.layout.machine_view, this, true)
    }

    suspend fun update(m: IMachineEntity) {
        this.contentDescription = "Virtual Machine: " + m.name
        machine_list_item_ostype.setImageResource(VBoxApplication.getOSDrawable(context, m.osTypeId))
        machine_list_item_name.text = m.name
        m.state?.let {
            machine_list_item_state.setImageResource(it.drawable())
            machine_list_item_state_text.text = it.value()
        }
        machine_list_item_snapshot.text = m.currentSnapshot?.let {
            "($it) ${if (m.currentStateModified == true) "*" else ""}"
        } ?: ""
    }
}
