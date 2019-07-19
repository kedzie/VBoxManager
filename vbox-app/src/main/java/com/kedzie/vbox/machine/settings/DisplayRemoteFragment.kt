package com.kedzie.vbox.machine.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IVRDEServer
import com.kedzie.vbox.api.jaxb.AuthType
import com.kedzie.vbox.app.Utils
import kotlinx.android.synthetic.main.settings_display_remote.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * Edit remote desktop server
 * @apiviz.stereotype fragment
 */
class DisplayRemoteFragment(arguments: Bundle) : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    init {
        this.arguments = arguments
    }

    private lateinit var machine: IMachine

    private fun loadInfo() {
        launch {
            val server = machine.getVRDEServer()
            Timber.i("VRDE Properties: " + Arrays.toString(server.getVRDEProperties()))
            Timber.i("TCP/Ports: " + server.getVRDEProperty(IVRDEServer.PROPERTY_PORT))

            enabled.isChecked = server.getEnabled()
            enabled.setOnCheckedChangeListener { buttonView, isChecked ->
                launch {
                    server.setEnabled(isChecked)
                }
            }
            server_port.setText(server.getVRDEProperty("TCP/Ports"))
            server_port.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    launch {
                        server.setVRDEProperty("TCP/Ports", s.toString())
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })

            val authMethodAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, AuthType.values())
            auth_method.adapter = authMethodAdapter
            auth_method.setSelection(Utils.indexOf(AuthType.values(), server.getAuthType()))
            auth_method.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    launch {
                        server.setAuthType(authMethodAdapter.getItem(position))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            auth_timeout.setText(server.getAuthTimeout().toString())
            auth_timeout.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    launch {
                        server.setAuthTimeout(auth_timeout.text.toString().toInt())
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
            multiple_connections.isChecked = server.getAllowMultiConnection()
            multiple_connections.setOnCheckedChangeListener { buttonView, isChecked ->
                launch { server.setAllowMultiConnection(isChecked) } }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_display_remote, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadInfo()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}