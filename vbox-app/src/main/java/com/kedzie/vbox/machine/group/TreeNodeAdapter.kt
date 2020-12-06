package com.spoton.mobile.android.discovery.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kedzie.vbox.R
import com.kedzie.vbox.VBoxApplication
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.machine.MachineView
import com.kedzie.vbox.machine.group.*
import kotlinx.android.synthetic.main.machine_view.view.*
import kotlinx.android.synthetic.main.vmgroup_title.view.*

class TreeNodeAdapter(private val listener: MachineGroupListFragment.OnTreeNodeSelectListener) :
        PagedListAdapter<TreeNode, TreeNodeAdapter.TreeNodeAdapterViewHolder>(object : DiffUtil.ItemCallback<TreeNode>() {

    override fun areItemsTheSame(first: TreeNode, second: TreeNode): Boolean {
        return first == second::class && when (first) {
            is GroupTreeNode -> first.group.name == (second as GroupTreeNode).group.name
            is MachineTreeNode -> first.machine.idRef == (second as MachineTreeNode).machine.idRef
        }
    }

    override fun areContentsTheSame(first: TreeNode, second: TreeNode): Boolean {
        return first::class == second::class && when (first) {
            is MachineTreeNode ->  {
                first.machine == (second as MachineTreeNode).machine
            }
            is GroupTreeNode -> first.group == (second as GroupTreeNode).group
        }
    }
}) {

    class TreeNodeAdapterViewHolder constructor(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeNodeAdapterViewHolder {
        return when (viewType) {
            TYPE_MACHINE -> TreeNodeAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.machine_view, parent, false))
            TYPE_GROUP -> TreeNodeAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.vmgroup_title, parent, false))
            else -> TreeNodeAdapterViewHolder(ProgressBar(parent.context))
        }
    }

    override fun onBindViewHolder(holder: TreeNodeAdapterViewHolder, position: Int) {
        getItem(position)?.let { item ->
            when (item) {
                is MachineTreeNode -> {
                    val machineView = holder.itemView as MachineView
                    machineView.machine_list_item_ostype.setImageResource(VBoxApplication.getOSDrawable(machineView.context, item.machine.osTypeId))
                    machineView.machine_list_item_name.text = item.machine.name
                    item.machine.state?.let {
                        machineView.machine_list_item_state.setImageResource(it.drawable())
                        machineView.machine_list_item_state_text.text = it.value()
                    }
                    machineView.machine_list_item_snapshot.text = item.machine.currentSnapshot?.let {
                        "($it) ${if (item.machine.currentStateModified == true) "*" else ""}"
                    } ?: ""
                    holder.itemView.setOnClickListener { v -> listener.onTreeNodeSelect(item) }
                }
                is GroupTreeNode -> {
                    holder.itemView.group_title.text = item.group.simpleGroupName
                    holder.itemView.setOnClickListener { v -> listener.onTreeNodeSelect(item) }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MachineTreeNode -> TYPE_MACHINE
            else -> TYPE_GROUP
        }
    }

    companion object {
        private const val TYPE_MACHINE = 1
        private const val TYPE_GROUP = 2
    }
}
