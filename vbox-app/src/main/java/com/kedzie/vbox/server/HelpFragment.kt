package com.kedzie.vbox.server

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import kotlinx.android.synthetic.main.help.*

/**
 * Detailed help information for launching *vboxwebsrv*
 */
class HelpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main_text.text = Html.fromHtml(resources.getString(R.string.help_main))
        main_text.movementMethod = LinkMovementMethod.getInstance()

        ssl_text.text = Html.fromHtml(resources.getString(R.string.help_ssl))
        ssl_text.movementMethod = LinkMovementMethod.getInstance()
    }
}
