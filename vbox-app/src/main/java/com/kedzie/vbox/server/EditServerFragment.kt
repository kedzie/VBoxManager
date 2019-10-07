package com.kedzie.vbox.server

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kedzie.vbox.R
import com.kedzie.vbox.app.Utils
import kotlinx.android.synthetic.main.server.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.regex.Pattern

class EditServerFragment : Fragment() {

    private val args: EditServerFragmentArgs by navArgs()

    private val model: EditServerViewModel by viewModel { parametersOf(args.server) }

    private val validIpAddressRegex = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")

    private val validHostnameRegex = Pattern.compile("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.server, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        server_name.setText(model.server.name)
        server_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                model.server.host = s.toString()
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        server_host.setText(model.server.host)
        server_host.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (validIpAddressRegex.matcher(s).matches() || validHostnameRegex.matcher(s).matches()) {
                    server_host_layout.isErrorEnabled = false
                } else {
                    server_host_layout.isErrorEnabled = true
                    server_host_layout.error = resources.getString(R.string.server_host_error)
                }
            }
        })
        server_ssl.isChecked = model.server.isSSL

        server_ssl.setOnCheckedChangeListener { _, isChecked ->
            model.server.isSSL = isChecked
        }

        server_port.setText(model.server.port.toString())
        server_port.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                model.server.port = s.toString().toInt()
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        server_username.setText(model.server.username)
        server_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                model.server.username = s.toString()
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        server_password.setText(model.server.password)
        server_username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                model.server.password = s.toString()
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.server_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.server_list_option_menu_save -> {
                if (!server_host_layout.isErrorEnabled) {
                    model.saveServer()
                    findNavController().navigate(EditServerFragmentDirections.saveServer())
                } else {
                    Utils.toastLong(activity, "Fix errors first")
                }
                return true
            }
            R.id.server_list_option_menu_delete -> {
                model.deleteServer()
                findNavController().navigate(EditServerFragmentDirections.saveServer())
                return true
            }
            else -> return false
        }

        return super.onOptionsItemSelected(item)
    }
}
