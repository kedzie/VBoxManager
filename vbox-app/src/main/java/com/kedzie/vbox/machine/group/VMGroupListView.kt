package com.kedzie.vbox.machine.group

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.os.AsyncTask
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ViewFlipper
import androidx.core.widget.NestedScrollView
import com.kedzie.vbox.R
import com.kedzie.vbox.api.IHost
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.ISession
import com.kedzie.vbox.api.jaxb.LockType
import com.kedzie.vbox.api.jaxb.SessionState
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.host.HostView
import com.kedzie.vbox.machine.MachineView
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap

/**
 * Scrollable list of [VMGroup] objects with drill-down support to focus on a particular group.
 */
//@SuppressLint("NewApi")
//class VMGroupListView(context: Context, private val _vmgr: VBoxSvc) : ViewFlipper(context),
//        View.OnClickListener,
//        View.OnLongClickListener,
//        OnDrillDownListener {
//
//    private val mDragger= Dragger()
//    private var mDraggedGroup: VMGroup? = null
//    private var mDraggedMachine: IMachine? = null
//
//    override fun onLongClick(view: View): Boolean {
//        GlobalScope.launch(Dispatchers.Main) {
//            if (view is MachineView)
//                dragMachine(view)
//            else if (view is VMGroupPanel)
//                dragGroup(view)
//        }
//        return true
//    }
//
//
//    private suspend fun dragMachine(view: MachineView) {
//        val machine = view.machine
//        withContext(Dispatchers.Default) {
//            if (machine.getSessionStateNoCache() == SessionState.UNLOCKED) {
//                machine
//            } else null
//        }?.let { result ->
//            mDraggedMachine = result
//            val data = ClipData("VM", arrayOf("vbox/machine"), ClipData.Item(result.idRef))
//            view.startDrag(data, DragShadowBuilder(view), null, 0)
//        }
//    }
//
//    private suspend fun hasLockedMachines(group: VMGroup): Boolean {
//        var locked = false
//        for (child in group.children) {
//            if (child is IMachine) {
//                locked = locked or (child.getSessionState() != SessionState.UNLOCKED)
//            } else {
//                val g = child as VMGroup
//                locked = locked or hasLockedMachines(g)
//            }
//        }
//        return locked
//    }
//
//    private suspend fun dragGroup(view: VMGroupPanel) {
//        val group = view.group
//
//        withContext(Dispatchers.Default) {
//            if (!hasLockedMachines(group)) group else null
//        }?.let { result ->
//            mDraggedGroup = result
//            val data = ClipData(result.name, arrayOf("vbox/group"), ClipData.Item(result.name))
//            view.startDrag(data, View.DragShadowBuilder(view.titleView), null, 0)
//        }
//    }
//
//    private inner class Dragger : View.OnDragListener {
//
//        private var mGroupView: VMGroupPanel? = null
//        private var mSectionView: GroupSection? = null
//        private var mParentGroup: VMGroup? = null
//        private var mNewParentViews: List<VMGroupPanel>? = null
//
//        override fun onDrag(view: View, event: DragEvent): Boolean {
//            mSectionView = view as GroupSection
//
//            val action = event.action
//            when (action) {
//                DragEvent.ACTION_DRAG_STARTED -> return true
//                DragEvent.ACTION_DRAG_ENTERED -> {
//                    mSectionView!!.setBackgroundColor(DRAG_ACCEPT_COLOR)
//                    mSectionView!!.invalidate()
//                    return true
//                }
//                DragEvent.ACTION_DRAG_LOCATION -> {
//                    val current = Utils.TouchUtils.getDeepestView(mSectionView,
//                            PointF(event.x, event.y)
//                    ) { view -> VMGroupPanel::class.java.isAssignableFrom(view!!.javaClass) } as VMGroupPanel
//
//                    if (mGroupView != null && current !== mGroupView) { //exited group panel
//                        Timber.d( "Exited %s", mGroupView!!.group)
//                        mParentGroup = null
//                        mGroupView!!.setBackgroundColor(resources.getColor(VIEW_BACKGROUND, null))
//                        mGroupView!!.invalidate()
//                    }
//                    if (current != null && current !== mGroupView) { //entered group panel
//                        Timber.d( "Entered %s", current.group)
//                        mParentGroup = current.group
//                        if (doAcceptDragEnter()) {
//                            current.setBackgroundColor(DRAG_ACCEPT_COLOR)
//                            current.invalidate()
//                        }
//                    }
//                    if (current == null && mGroupView != null) { //entered root group
//                        Timber.d( "Entered Root")
//                        mParentGroup = mSectionView!!.group
//                        if (doAcceptDragEnter()) {
//                            mSectionView!!.setBackgroundColor(DRAG_ACCEPT_COLOR)
//                            mSectionView!!.invalidate()
//                        }
//                    } else if (current != null && mGroupView == null) { //exited root group
//                        Timber.d( "Exited Root")
//                        mParentGroup = null
//                        mSectionView!!.setBackgroundColor(resources.getColor(VIEW_BACKGROUND, null))
//                        mSectionView!!.invalidate()
//                    }
//                    mGroupView = current
//                    return true
//                }
//                DragEvent.ACTION_DRAG_EXITED -> {
//                    mParentGroup = null
//                    view.setBackgroundColor(resources.getColor(VIEW_BACKGROUND, null))
//                    view.invalidate()
//                    return true
//                }
//                DragEvent.ACTION_DROP -> {
//                    if (!doAcceptDragEnter())
//                        return false
//
//                    mNewParentViews = mGroupViewMap[mParentGroup!!.name]
//
//                    if (mDraggedMachine != null)
//                        dropMachine(mDraggedMachine!!, mParentGroup!!)
//                    else if (mDraggedGroup != null)
//                        dropGroup(mDraggedGroup!!, mParentGroup!!)
//                    return true
//                }
//                DragEvent.ACTION_DRAG_ENDED -> {
//                    if (mGroupView != null) {
//                        mGroupView!!.setBackgroundColor(resources.getColor(VIEW_BACKGROUND, null))
//                        mGroupView!!.invalidate()
//                    }
//                    view.setBackgroundColor(resources.getColor(VIEW_BACKGROUND, null))
//                    view.invalidate()
//                    mGroupView = null
//                    mDraggedGroup = null
//                    mDraggedMachine = null
//                    mParentGroup = null
//                    return true
//                }
//            }
//            return false
//        }
//
//        private fun doAcceptDragEnter(): Boolean {
//            if (mDraggedMachine != null) {
//                if (mDraggedMachine!!.getGroupsCacheOnly()[0].equals(mParentGroup!!.name))
//                    return false
//            } else if (mDraggedGroup != null) {
//                if (mDraggedGroup == mParentGroup)
//                    return false
//                val oldParentName = mDraggedGroup!!.name.substring(0, mDraggedGroup!!.name.lastIndexOf('/'))
//                if (oldParentName == mParentGroup!!.name)
//                    return false
//            }
//            return true
//        }
//
//        private fun dropMachine(draggedMachine: IMachine, parent: VMGroup) {
//            val machineViews = mMachineViewMap[draggedMachine.idRef]
//            //move the views
//            for (mv in machineViews!!)
//                (mv.parent as ViewGroup).removeView(mv)
//            for (i in machineViews.indices) {
//                val mv = machineViews[i]
//                if (mNewParentViews != null && i < mNewParentViews!!.size)
//                //in case we are dragging to root group, there are less group panels than machine views
//                    mNewParentViews!![i].addView(mv, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT))
//                else if (mSectionView != null)
//                    mSectionView!!.addView(mv, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT))
//            }
//
//            GlobalScope.launch(Dispatchers.Main) {
//                //update the data
//                val oldParent = mGroupCache[draggedMachine.getGroups()[0]]
//                oldParent!!.removeChild(draggedMachine)
//                parent.addChild(draggedMachine)
//
//                val session = _vmgr.vbox!!.getSessionObject()
//                draggedMachine.lockMachine(session, LockType.WRITE)
//                val mutable = session.getMachine()
//                mutable.setGroups(listOf(parent.name))
//                mutable.saveSettings()
//                session.unlockMachine()
//            }
//        }
//
//        private fun dropGroup(draggedGroup: VMGroup, parent: VMGroup) {
//            val oldParentName = draggedGroup.name.substring(0, draggedGroup.name.lastIndexOf('/'))
//            //move the views
//            val groupViews = mGroupViewMap[draggedGroup.name]
//            for (gv in groupViews!!)
//                (gv.parent as ViewGroup).removeView(gv)
//
//            for (i in groupViews.indices) {
//                val gv = groupViews[i]
//                if (mNewParentViews != null && i < mNewParentViews!!.size)
//                //in case we are dragging to root group, there are less parent group panels than child views
//                    mNewParentViews!![i].addView(gv, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
//                else if (mSectionView != null)
//                    mSectionView!!.addView(gv, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
//            }
//
//            //update the data
//            val oldParent = mGroupCache[oldParentName]
//            Timber.d("Moving group [%1\$s]  from %2\$s --> %3\$s", draggedGroup, oldParentName, parent)
//            oldParent!!.removeChild(draggedGroup)
//            parent.addChild(draggedGroup)
//
//            GlobalScope.launch(Dispatchers.Main) {
//                moveGroup(_vmgr.vbox!!.getSessionObject(), parent, draggedGroup)
//            }
//        }
//
//        private suspend fun moveGroup(session: ISession, parent: VMGroup, group: VMGroup) {
//            Timber.d( "Processing group: $group")
//            val oldName = group.name
//            group.name = parent.name + (if (parent.name.endsWith("/")) "" else "/") + group.simpleGroupName
//            //update the group cache
//            mGroupCache.remove(oldName)
//            mGroupCache[group.name] = group
//           Timber.d("Changed group name %1\$s --> %2\$s", oldName, group.name)
//            for (c in group.children) {
//                if (c is IMachine) {
//                    Timber.d("Updating Machine: %s", c.getName())
//                    c.lockMachine(session, LockType.WRITE)
//                    val mutable = session.getMachine()
//                    mutable.setGroups(listOf(group.name))
//                    mutable.saveSettings()
//                    session.unlockMachine()
//                } else {
//                    val child = c as VMGroup
//                    moveGroup(session, group, child)
//                }
//            }
//        }
//    }
//
//    companion object {
//        internal const val DRAG_ACCEPT_COLOR = Color.GREEN
//        internal const val VIEW_BACKGROUND = android.R.color.background_light
//    }
//}