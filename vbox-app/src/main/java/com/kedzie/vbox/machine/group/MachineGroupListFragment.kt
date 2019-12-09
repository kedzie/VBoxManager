package com.kedzie.vbox.machine.group

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ViewFlipper
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IMachineStateChangedEvent
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.machine.MachineListViewModel
import com.kedzie.vbox.machine.MachineView
import kotlinx.android.synthetic.main.vmgroup_list.view.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class MachineGroupListFragment : Fragment(),
        View.OnClickListener,
        OnDrillDownListener {

    private val model: MachineListViewModel by sharedViewModel()

    private lateinit var root: VMGroup

    private lateinit var selectListener: OnTreeNodeSelectListener

    /** Currently selected view  */
    private var _selected: View? = null

    /** Is element selection enabled  */
    var isSelectionEnabled: Boolean = false

    /** Maps Machine ID to all views which reference it.  Used for updating views when events are received.  */
    private val mMachineViewMap = HashMap<String, MutableList<MachineView>>()
    /** Maps [VMGroup] to all views which reference it.  Used for updating views when groups change.  */
    private val mGroupViewMap = HashMap<String, MutableList<VMGroupPanel>>()


    private val groupCache = HashMap<String, VMGroup>()

    private fun get(name: String): VMGroup {
        if (!groupCache.containsKey(name))
            groupCache[name] = VMGroup(name)
        return groupCache[name]!!
    }

    private fun loadGroups() {
        model.vbox.value?.let { vbox ->
            model.viewModelScope.launch {
                for (group in vbox.getMachineGroups()) {
                    if (group == "/") continue
                    var previous = get(group)
                    var lastIndex = group.lastIndexOf('/')
                    var tmp = group
                    while (lastIndex > 0) {
                        tmp = tmp.substring(0, lastIndex)
                        val current = get(tmp)
                        current.addChild(previous)
                        previous = current
                        lastIndex = tmp.lastIndexOf('/')
                    }
                    get("/").addChild(get(tmp))
                }
                for (machine in vbox.getMachines()) {
                    val groups = machine.getGroups()
                    get(groups[0]).addChild(machine)
                }

                root = get("/")
                setRoot(root)
            }
        }
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        try {
            selectListener = activity as OnTreeNodeSelectListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity does not implement OnTreeNodeSelectListener")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = SwipeRefreshLayout(activity!!)
        view.setOnRefreshListener { loadGroups() }

        flipper = ViewFlipper(activity!!)
        view.addView(flipper)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.events.observe(this, Observer {
            when(it) {
                is IMachineStateChangedEvent -> {
                    model.vmgr.value?.let { vmgr ->
                        model.viewModelScope.launch {
                            val machine = vmgr.vbox.findMachine(it.getMachineId())
                            Timber.d("Got machine state changed event %s", machine)
                            update(machine)
                        }
                    }
                }
            }
        })

        model.vmgr.observe(this, Observer {
            it?.let { vmgr ->
                Timber.d("Got VMGR")
                loadGroups()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isSelectionEnabled = activity!!.findViewById<View>(R.id.details) != null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //save current machine
        outState.putParcelable("checkedItem", selectedObject)
    }

    /**
     * Callback for element selection
     */
    interface OnTreeNodeSelectListener {
        /**
         * An element has been selected
         * @param node    the selected element
         */
        fun onTreeNodeSelect(node: TreeNode?)
    }

    suspend fun setRoot(group: VMGroup) {
        view.addView(GroupSection(activity!!).setGroup(group))
    }

    override fun onDrillDown(group: VMGroup) {
        findNavController().navigate(MachineGroupListFragmentDirections.drillDown(group))
    }

    fun drillOut() {
        findNavController().popBackStack()
    }

    /**
     * Build a scrollable list of everything below a group
     */
    private inner class GroupSection(context: Context) : LinearLayout(context) {
        private val mContents = LinearLayout(context)

        val nodeViews: List<VMGroupPanel>
            get() {
                val children = ArrayList<VMGroupPanel>(mContents.childCount)
                for (i in 0 until mContents.childCount)
                    if (mContents.getChildAt(i) is VMGroupPanel)
                        children.add(mContents.getChildAt(i) as VMGroupPanel)
                return children
            }

        lateinit var group: VMGroup

        init {
            orientation = VERTICAL
            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
            mContents.orientation = VERTICAL

            val scrollView = NestedScrollView(getContext())
            scrollView.addView(mContents)
            super.addView(scrollView)
//            setOnDragListener(mDragger)
        }

        suspend fun setGroup(group: VMGroup): GroupSection {
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            if (this.group.name != "/") {
                val header = LayoutInflater.from(context).inflate(R.layout.vmgroup_list, this, false) as LinearLayout
                header.group_back.setOnClickListener { drillOut() }
                header.group_title.setText(group.name)
                header.group_num_groups.setText("${group.numGroups}")
                header.group_num_machine.setText("${group.numMachines}")
                lp.bottomMargin = Utils.dpiToPx(context, 4)
                super.addView(header, lp)
            }
            for (child in group.children)
                mContents.addView(createView(child), lp)
            return this
        }

        override fun addView(child: View, params: ViewGroup.LayoutParams) {
            mContents.addView(child, params)
        }

        /**
         * Create a view for a single node in the tree
         * @param node      tree node
         * @return  Fully populated view representing the node
         */
        suspend fun createView(node: TreeNode): View {
            if (node is IMachine) {
                if (!mMachineViewMap.containsKey(node.idRef))
                    mMachineViewMap[node.idRef] = ArrayList()
                val view = MachineView(context).apply {
                    update(node)
                    setBackgroundResource(R.drawable.list_selector_color)
                    isClickable = true
                    setOnClickListener(this@MachineGroupListFragment)
//                    setOnLongClickListener(this@MachineGroupListFragment)
                }
                mMachineViewMap[node.idRef]!!.add(view)
                return view
            } else if (node is VMGroup) {
                val groupView = VMGroupPanel(context, node)
                descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
                groupView.setOnClickListener(this@MachineGroupListFragment)
                groupView.setOnDrillDownListener(this@MachineGroupListFragment)
//                groupView.setOnLongClickListener(this@MachineGroupListFragment)
                for (child in node.children)
                    groupView.addChild(createView(child))
                if (!mGroupViewMap.containsKey(node.name))
                    mGroupViewMap[node.name] = ArrayList()
                mGroupViewMap[node.name]!!.add(groupView)
//                groupView.setBackgroundColor(resources.getColor(VMGroupListView.VIEW_BACKGROUND, null))
                return groupView
            }
            throw IllegalArgumentException("Only views of type MachineView or VMGroupView are allowed")
        }
    }

    /**
     * Update all machine views with new data
     * @param machine       the machine to update (properties must be cached)
     */
    suspend fun update(machine: IMachine) {
        for (view in mMachineViewMap[machine.idRef]!!)
            view.update(machine)
    }

    override fun onClick(v: View) {
        if (selectListener == null)
            return
        if (!isSelectionEnabled) {
            notifyListener(v)
            return
        }
        //Deselect existing selection
        if (_selected === v) {
            _selected!!.isSelected = false
            _selected = null
            selectListener.onTreeNodeSelect(null)
            return
        }
        //Make new Selection
        _selected?.let { it.isSelected = false }
        _selected = v
        _selected!!.isSelected = true
        notifyListener(_selected!!)
    }

    private fun notifyListener(v: View) {
        if (v is MachineView)
            selectListener.onTreeNodeSelect(v.machine)
        else if (v is VMGroupPanel)
            selectListener.onTreeNodeSelect(v.group)
    }
}
