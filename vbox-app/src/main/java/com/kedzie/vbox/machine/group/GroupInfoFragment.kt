package com.kedzie.vbox.machine.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.api.*
import com.kedzie.vbox.api.jaxb.CPUPropertyType
import com.kedzie.vbox.api.jaxb.DeviceType
import com.kedzie.vbox.api.jaxb.HWVirtExPropertyType
import com.kedzie.vbox.api.jaxb.MachineState
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.machine.MachineListViewModel
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.android.synthetic.main.group_info.view.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.io.IOException

class GroupInfoFragment : Fragment()  {

    private val model: MachineListViewModel by sharedViewModel { activity!!.intent.let {
        parametersOf(it.getParcelableExtra(VBoxSvc.BUNDLE), it.getParcelableExtra(IMachine.BUNDLE)) } }

    private lateinit var group: VMGroup
    private lateinit var info: ArrayList<MachineInfo>

    private lateinit var view: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments!!.getParcelable(VMGroup.BUNDLE)!!
        info = arrayListOf<MachineInfo>().apply {
            addAll(group.children.mapNotNull { if (it is IMachine) MachineInfo(it, null) else null })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = LinearLayout(activity)
        view.orientation = LinearLayout.VERTICAL
        val swipeLayout = SwipeRefreshLayout(activity!!)
        swipeLayout.setOnRefreshListener { loadInfo(true) }
        val scrollView = ScrollView(activity)
        scrollView.addView(view)
        swipeLayout.addView(scrollView)
        return swipeLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.vmgr.observe(this, Observer {
            it?.let { vmgr ->
                loadInfo(true)
            }
        })

        model.events.observe(this, Observer {
            when(it) {
                is IMachineStateChangedEvent -> {
                    Timber.d("got event loading data %s", it)
                    loadInfo(true)
                }
            }
        })
    }

    private fun loadInfo(refresh: Boolean) {
        model.viewModelScope.launch {
            view.removeAllViews()
            val inflater = LayoutInflater.from(activity)
            for (node in info) {
                val view = inflater.inflate(R.layout.group_info, view, true)

                view.name.text = if(refresh) node.machine.getNameNoCache() else node.machine.getName()
                view.ostype.text = if(refresh) node.machine.getOSTypeIdNoCache() else node.machine.getOSTypeId()

                view.groups.text = node.machine.getGroups()?.get(0)

                view.baseMemory.text = "${node.machine.getMemorySize()}"
                view.processors.text = "${node.machine.getCPUCount()}"

                var accelerationBuf = StringBuffer()
                if (node.machine.getHWVirtExProperty(HWVirtExPropertyType.ENABLED))
                    accelerationBuf.append("VT-x/AMD-V")
                if (node.machine.getHWVirtExProperty(HWVirtExPropertyType.NESTED_PAGING))
                    Utils.appendWithComma(accelerationBuf, "Nested Paging")
                if (node.machine.getCPUProperty(CPUPropertyType.PAE))
                    Utils.appendWithComma(accelerationBuf, "PAE/NX")
                view.acceleration.text = accelerationBuf

                val bootOrderBuf = StringBuffer()
                for (i in 1..99) {
                    val b = node.machine.getBootOrder(i)
                    if (b == DeviceType.NULL) break
                    Utils.appendWithComma(bootOrderBuf, b.toString())
                }
                view.bootOrder.text = bootOrderBuf

                val size = resources.getDimensionPixelSize(R.dimen.screenshot_size)

                val state = if(refresh) node.machine.getStateNoCache() else node.machine.getState()

                if (state == MachineState.SAVED) {
                    node.screenshot = node.machine.readSavedScreenshot(0)
                    node.screenshot!!.scaleBitmap(size, size)
                } else if (state == MachineState.RUNNING) {
                    try {
                        node.screenshot = node.machine.api.vbox!!.takeScreenshot(node.machine, size, size)
                    } catch (e: IOException) {
                        Timber.e(e, "Exception taking screenshot")
                    }
                }
                if (node.screenshot != null) {
                    view.preview.adjustViewBounds = true
                    view.preview.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    view.preview.setImageBitmap(node.screenshot!!.bitmap)
                    view.previewPanel.expand(false)
                } else {
                    view.previewPanel.collapse(false)
                }
            }
        }
    }
}
