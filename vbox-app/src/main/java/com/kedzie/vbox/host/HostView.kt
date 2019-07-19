package com.kedzie.vbox.host

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IHost
import kotlinx.android.synthetic.main.host_view.view.*

/**
 * Show VM information
 *
 * @apiviz.stereotype view
 */
class HostView(context: Context) : RelativeLayout(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.host_view, this, true)
        isFocusable = true
    }

    suspend fun update(h: IHost) {
        host_ip.text = "(${h.api.server.host})"
        host_version.text = h.api.vbox!!.getVersion()
    }
}
