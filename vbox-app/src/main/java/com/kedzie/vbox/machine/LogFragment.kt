package com.kedzie.vbox.machine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.app.BundleBuilder
import kotlinx.android.synthetic.main.machine_log.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class LogFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private lateinit var _machine: IMachine
    private var _log: String? = null

    private fun loadLog() {
        launch {
            var text = ""
            try {
                text = String(_machine.readLog(0, 0, MAX_LOG_SIZE.toLong()))
            } catch (e: Exception) {
                Timber.w(e, "Error reading log")
            }

            withContext(Dispatchers.Main) {
                if (text.length == MAX_LOG_SIZE)
                    Timber.w( "Didn't get entire log file.  Log size: %d", text.length)
                _log = text
                logText!!.text = _log
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _machine = BundleBuilder.getProxy(arguments, IMachine.BUNDLE, IMachine::class.java)
        if (savedInstanceState != null)
            _log = savedInstanceState.getString("log")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val swipeLayout = SwipeRefreshLayout(activity!!)
        swipeLayout.setOnRefreshListener { loadLog() }
        inflater.inflate(R.layout.machine_log, swipeLayout)
        return swipeLayout
    }

    override fun onStart() {
        super.onStart()
        job = Job()
        if (_log != null)
            logText!!.text = _log
        else
            loadLog()
    }

    override fun onStop() {
        job.cancel()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("log", _log)
    }

    companion object {
        private const val MAX_LOG_SIZE = 409600 //400 Kbps
    }
}
