package com.kedzie.vbox.machine.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ExpandableListView.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.fragment.app.Fragment
import com.kedzie.vbox.R
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IMedium
import com.kedzie.vbox.api.IStorageController
import com.kedzie.vbox.api.jaxb.DeviceType
import com.kedzie.vbox.api.jaxb.IMediumAttachment
import com.kedzie.vbox.api.jaxb.StorageBus
import com.kedzie.vbox.app.Utils
import kotlinx.android.synthetic.main.settings_storage_controller_list_item.view.*
import kotlinx.android.synthetic.main.settings_storage_tree.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * Expandable list of storage controllers and associated attachments
 *
 * @apiviz.stereotype Fragment
 */
class StorageListFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private lateinit var listAdapter: ItemAdapter

    private var controllerListener: OnStorageControllerClickedListener? = null
    private var mediumListener: OnMediumAttachmentClickedListener? = null

    private lateinit var machine: IMachine

    private var controllers = mutableListOf<IStorageController>()
    private var dataListMap = mutableMapOf<IStorageController, MutableList<IMediumAttachment>>()

    /**
     * Listener for Storage Controller clicks
     */
    interface OnStorageControllerClickedListener {

        /**
         * @param element        the [IStorageController] click event
         */
        fun onStorageControllerClicked(element: IStorageController)
    }

    /**
     * Listener for Medium attachment clicks
     */
    interface OnMediumAttachmentClickedListener {

        /**
         * Handle Medium attachment click event
         * @param element        the [IMediumAttachment] click event
         */
        fun onMediumAttachmentClicked(element: IMediumAttachment)
    }

    private fun loadData() = launch {
            val systemProperties = machine.api.vbox!!.getSystemProperties()
            val chipset = machine.getChipsetType()
            for (bus in StorageBus.values()) {
                systemProperties.getMaxInstancesOfStorageBus(chipset, bus)
                systemProperties.getDeviceTypesForStorageBus(bus)
            }
            controllers.clear()
            controllers.addAll(machine.getStorageControllers())
            for (c in controllers) {
                dataListMap[c] = mutableListOf<IMediumAttachment>().apply {
                    addAll(machine.getMediumAttachmentsOfController(c.getName()))
                }
            }
            listAdapter = ItemAdapter()
            storage_tree.setAdapter(listAdapter)
            for (i in controllers.indices)
                storage_tree.expandGroup(i)
        }


    private fun addController(bus: StorageBus) = launch {
        val controller = machine.addStorageController(bus.toString(), bus)
        controllers.add(controller)
        dataListMap[controller] = mutableListOf()
        listAdapter.notifyDataSetChanged()
    }

    private fun deleteController(controller: IStorageController) = launch {
        machine.removeStorageController(controller.getName())
        controllers.remove(controller)
        dataListMap.remove(controller)
        listAdapter.notifyDataSetChanged()
    }

    private fun detachMedium(medium: IMediumAttachment) = launch {
        machine.detachDevice(medium.controller!!, medium.port, medium.device)
        for (c in controllers) {
            if (c.getName() == medium.controller) {
                dataListMap[c]?.remove(medium)
                break
            }
        }
        listAdapter.notifyDataSetChanged()
    }

    private suspend fun listMediums(mediums: List<IMedium>, controller: IStorageController) =
        AlertDialog.Builder(activity)
                .setTitle("Select Medium")
                .setItems(mediums.map { it.getName() }.toTypedArray()) { dialog, item -> mount(mediums[item], controller) }.show()


    private fun listDVDMediums(controller: IStorageController) = launch {
        val mediums = mutableListOf<IMedium?>().apply {
            addAll(controller.api.vbox!!.getHost().getDvdDrives())
            addAll(controller.api.vbox!!.getDVDImages())
            add(null) // no disc
        }
        AlertDialog.Builder(activity)
                .setTitle("Select Disk")
                .setItems(mediums.map { it?.let { "${if(it.getHostDrive()) "Host Drive" else ""} ${it.getName()}" } ?: "No Disc" }.toTypedArray())
                    { dialog, item -> mount(mediums[item]!!, controller) }.show()
    }

    private fun mount(medium: IMedium, controller: IStorageController) = launch {
        val devicesPerPort = controller.getMaxDevicesPerPortCount()
        for (i in 0 until controller.getMaxPortCount()) {
            for (j in 0 until devicesPerPort) {
                var isUsed = false
                for (m in dataListMap[controller]!!) {
                    if (m.port == i && m.device == j) {
                        isUsed = true
                        break
                    }
                }
                if (!isUsed) {
                    machine.attachDevice(controller.getName(), i, j, medium.getDeviceType(), medium)
                    val attachment = IMediumAttachment(medium, controller.getName(), medium.getDeviceType(),
                            false, false, false, false,
                            false, false, null, i, j)
                    Timber.d("Attaching to slot: %s", attachment.slot)
                    Utils.toastShort(context, "Attached medium to slot " + attachment.slot)
                    dataListMap[controller]!!.add(attachment)
                    listAdapter.notifyDataSetChanged()
                    break
                }
            }
        }


    }

    private inner class ItemAdapter() : BaseExpandableListAdapter() {

        private val mInflater = LayoutInflater.from(context)

        override fun notifyDataSetChanged() {
            super.notifyDataSetChanged()
            for (i in controllers.indices)
                storage_tree.expandGroup(i)
        }

        override fun getGroupCount(): Int {
            return controllers.size
        }

        override fun getGroup(groupPosition: Int): Any {
            return controllers[groupPosition]
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return dataListMap.get(controllers[groupPosition])?.size ?: 0
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return dataListMap.get(controllers[groupPosition])!![childPosition]
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
            val controller = getGroup(groupPosition) as IStorageController
            var convertView = convertView ?: mInflater.inflate(R.layout.settings_storage_controller_list_item, parent, false)

            launch {
                convertView.text1.text = controller.getName()
                val linear = convertView as LinearLayout
                for (type in machine.api.vbox!!.getSystemProperties().getDeviceTypesForStorageBus(controller.getBus())) {
                    val button = ImageButton(activity)
                    button.isFocusable = false
                    if (type == DeviceType.HARD_DISK) {
                        button.setImageResource(R.drawable.ic_menu_hdd_add)
                        button.setOnClickListener {
                            launch {
                                listMediums(controller.api.vbox!!.getHardDisks(), controller)
                            }
                        }
                    } else if (type == DeviceType.DVD) {
                        button.setImageResource(R.drawable.ic_menu_dvd_add)
                        button.setOnClickListener { listDVDMediums(controller) }
                    }
                    linear.addView(button, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
                }
            }
            //	        text1.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
            return convertView
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            val (medium, _, deviceType) = getChild(groupPosition, childPosition) as IMediumAttachment

            var convertView = convertView ?: mInflater.inflate(R.layout.simple_selectable_list_item, parent, false)

            launch {
                convertView.text1.text = medium?.getBase()?.getName() ?: "Empty"
                if (deviceType == DeviceType.HARD_DISK)
                    convertView.text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_hdd, 0, 0, 0)
                if (deviceType == DeviceType.DVD)
                    convertView.text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_button_dvd, 0, 0, 0)
            }
            return convertView
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        machine = arguments!!.getParcelable(IMachine.BUNDLE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_storage_tree, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage_tree.setOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
            parent.expandGroup(groupPosition)
            parent.setSelectedGroup(groupPosition)
            controllerListener?.onStorageControllerClicked(listAdapter.getGroup(groupPosition) as IStorageController)
            true
        })
        storage_tree.setOnChildClickListener(OnChildClickListener { parent, v, groupPosition, childPosition, id ->
            parent.setSelectedChild(groupPosition, childPosition, true)
            mediumListener?.onMediumAttachmentClicked(listAdapter.getChild(groupPosition, childPosition) as IMediumAttachment)
            true
        })
        registerForContextMenu(storage_tree)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (parentFragment is OnStorageControllerClickedListener)
            controllerListener = parentFragment as OnStorageControllerClickedListener
        if (parentFragment is OnMediumAttachmentClickedListener)
            mediumListener = parentFragment as OnMediumAttachmentClickedListener
    }

    override fun onStart() {
        super.onStart()
        loadData()
        if (dataListMap == null)
            loadData()
        else {
            listAdapter = ItemAdapter()
            storage_tree.setAdapter(listAdapter)
            for (i in 0 until dataListMap.keys.size)
                storage_tree.expandGroup(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.storage_actions, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_controller -> {
                launch {
                    val itemList = ArrayList<CharSequence>(5)
                    val chipset = machine.getChipsetType()
                    for (bus in Utils.removeNull(StorageBus.values())) {
                        if (getNumStorageControllersOfType(bus) < machine.api.vbox!!.getSystemProperties().getMaxInstancesOfStorageBus(chipset, bus))
                            itemList.add(bus.toString())
                    }
                    val items = itemList.toTypedArray()
                    AlertDialog.Builder(activity)
                            .setTitle("Controller Type")
                            .setItems(items) { dialog, item -> addController(StorageBus.fromValue(items[item].toString())) }.show()
                }
                return true
            }
            R.id.menu_refresh -> {
                loadData()
                return true
            }
        }
        return false
    }

    private suspend fun getNumStorageControllersOfType(bus: StorageBus): Int {
        var numControllers = 0
        for (controller in dataListMap.keys) {
            if (controller.getBus() == bus)
                numControllers++
        }
        return numControllers
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        menu.add(Menu.NONE, R.id.menu_delete, Menu.NONE, R.string.delete)
    }

    override fun onContextItemSelected(item: android.view.MenuItem): Boolean {
        val info = item.menuInfo as ExpandableListContextMenuInfo
        val groupNum = ExpandableListView.getPackedPositionGroup(info.packedPosition)
        when (ExpandableListView.getPackedPositionType(info.packedPosition)) {
            ExpandableListView.PACKED_POSITION_TYPE_GROUP -> {
                val controller = listAdapter.getGroup(groupNum) as IStorageController
                deleteController(controller)
            }
            ExpandableListView.PACKED_POSITION_TYPE_CHILD -> {
                val childNum = ExpandableListView.getPackedPositionChild(info.packedPosition)
                val attachment = listAdapter.getChild(groupNum, childNum) as IMediumAttachment
                detachMedium(attachment)
            }
        }
        return false
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}
