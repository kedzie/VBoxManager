package com.kedzie.vbox.machine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import kotlinx.android.synthetic.main.group_info.*
import kotlinx.android.synthetic.main.machine_info_column_1.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class InfoFragment(arguments: Bundle) : Fragment() {

    init {
        this.arguments = arguments
    }

    private val args by navArgs<InfoFragmentArgs>()

    private val model by viewModel<InfoViewModel> { parametersOf(args.server, args.machineId) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = SwipeRefreshLayout(activity!!)
        view.setOnRefreshListener {
            //TODO refresh
        }
        inflater.inflate(R.layout.machine_info, view, true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.name.observe(viewLifecycleOwner, Observer { name.text = it })
        model.description.observe(viewLifecycleOwner, Observer { description.text = it })
        model.osTypeId.observe(viewLifecycleOwner, Observer { ostype.text = it })
        model.groups.observe(viewLifecycleOwner, Observer {
            groups.text =  if (!it.isNullOrEmpty())
                it[0]
            else
                "None"
        })

        model.memorySize.observe(viewLifecycleOwner, Observer { baseMemory.text = "$it" })
        model.cpuCount.observe(viewLifecycleOwner, Observer { processors.text = "$it" })
        model.bootOrder.observe(viewLifecycleOwner, Observer { bootOrder.text = it })
        model.acceleration.observe(viewLifecycleOwner, Observer { acceleration.text = it })
        model.storageControllers.observe(viewLifecycleOwner, Observer { storage.text = it })
        model.networkAdapters.observe(viewLifecycleOwner, Observer { network.text = it })
        model.audioController.observe(viewLifecycleOwner, Observer { audio_controller.text = it })
        model.audioDriver.observe(viewLifecycleOwner, Observer { audio_driver.text = it })
        model.vramSize.observe(viewLifecycleOwner, Observer { videoMemory.text = "$it MB" })
        model.accelerationVideo.observe(viewLifecycleOwner, Observer { accelerationVideo.text = it })
        model.rdpPort.observe(viewLifecycleOwner, Observer { rdpPort.text = it })
        model.screenshot.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                preview.setImageBitmap(it.bitmap)
                preview.adjustViewBounds = true
                preview.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                previewPanel.expand(false)
            } else
                previewPanel.collapse(false)
        })
    }
}