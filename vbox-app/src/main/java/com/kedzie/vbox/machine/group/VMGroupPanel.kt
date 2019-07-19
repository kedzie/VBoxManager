package com.kedzie.vbox.machine.group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.kedzie.vbox.R
import com.kedzie.vbox.app.CollapsiblePanelView

/**
 * Group of Virtual Machines.  Collapsible component like one introduced in VirtualBox 4.2.x
 * @author Marek Kędzierski
 */
class VMGroupPanel(context: Context,
                   /** The group this panel represents  */
                   val group: VMGroup) : CollapsiblePanelView(context) {

    private var _titleLabel: TextView? = null
    private var _drillDownButton: ImageView? = null
    private var _drillDownListener: OnDrillDownListener? = null
    private var _numGroupsText: TextView? = null
    private var _numMachinesText: TextView? = null

    init {
        isClickable = true
        isFocusable = true
        descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        setCollapseRotation(COLLAPSE_ROTATION)
        _titleLabel!!.text = this.group.name
        _numGroupsText!!.text = this.group.numGroups.toString() + ""
        _numMachinesText!!.text = this.group.numMachines.toString() + ""
    }

    override fun getTitleView(): View {
        if (mTitleView == null) {
            mTitleView = LayoutInflater.from(context).inflate(R.layout.vmgroup_title, this, false) as CollapsiblePanelView.ExpandableLinearLayout
            setCollapseButton(mTitleView.findViewById(R.id.group_collapse))
            _drillDownButton = mTitleView.findViewById<View>(R.id.group_enter) as ImageView
            _drillDownButton!!.setOnClickListener {
                if (_drillDownListener != null)
                    _drillDownListener!!.onDrillDown(group)
            }
            _numGroupsText = mTitleView.findViewById<View>(R.id.group_num_groups) as TextView
            _numMachinesText = mTitleView.findViewById<View>(R.id.group_num_machine) as TextView
            _titleLabel = mTitleView.findViewById<View>(R.id.group_title) as TextView
        }
        return mTitleView
    }

    fun addChild(view: View) {
        addView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
    }

    fun setOnDrillDownListener(listener: OnDrillDownListener) {
        _drillDownListener = listener
    }

    companion object {
        const val COLLAPSE_ROTATION = -90
    }
}

/**
 * Listener for Drill-Down button
 */
interface OnDrillDownListener {

    /**
     * The drill-down button has been pressed for a group
     * @param group        the group to focus on
     */
    fun onDrillDown(group: VMGroup)
}
