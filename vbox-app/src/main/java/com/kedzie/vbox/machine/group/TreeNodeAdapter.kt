package com.spoton.mobile.android.discovery.search

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.machine.group.*

class TreeNodeAdapter(private val listener: MachineGroupListFragment.OnTreeNodeSelectListener) :
        PagedListAdapter<TreeNode, TreeNodeAdapter.TreeNodeAdapterViewHolder>(object : DiffUtil.ItemCallback<TreeNode>() {

    override fun areItemsTheSame(first: TreeNode, second: TreeNode): Boolean {
        return first == second::class && when (first) {
            is GroupTreeNode -> first.group.name == (second as GroupTreeNode).group.name
            is MachineTreeNode -> (first as IMachine).idRef == (second as MachineTreeNode).idRef
        }
    }

    override fun areContentsTheSame(first: TreeNode, second: TreeNode): Boolean {
        return first::class == second::class && when (first) {
            is MachineTreeNode ->  {
                (first as IMachine) == (second as MachineTreeNode)
            }
            is GroupTreeNode -> (first as VMGroup) == (second as VMGroup)
        }
    }
}) {

    class TreeNodeAdapterViewHolder constructor(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeNodeAdapterViewHolder {
        return when (viewType) {
            TYPE_MACHINE -> TreeNodeAdapterViewHolder(TreeNodeAdapterCategoryItemView(parent.context))
            TYPE_GROUP
            -> TreeNodeAdapterViewHolder(TreeNodeAdapterMerchantItemView(parent.context))
            else -> TreeNodeAdapterViewHolder(ProgressBar(parent.context))
        }
    }

    override fun onBindViewHolder(holder: TreeNodeAdapterViewHolder, position: Int) {
        getItem(position)?.let { item ->
            when (item) {
                is IMachine -> {
                    (holder.itemView as TreeNodeAdapterMerchantItemView).configureViewForMerchant(item.merchant)
                    holder.itemView.setOnClickListener { v -> searchResultSelectedListener.onMerchantSelected(item.merchant) }
                }
                is VMGroup -> {
                    (holder.itemView as TreeNodeAdapterCategoryItemView).configureViewWithCategory(
                            item.category, false)
                    holder.itemView.setOnClickListener { _ -> searchResultSelectedListener.onCategorySelected(item.category) }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is IMachine -> TYPE_MERCHANT
            is VMGroup -> TYPE_CATEGORY
            null -> TYPE_NULL
        }
    }

    companion object {
        private const val TYPE_MACHINE = 1
        private const val TYPE_GROUP = 2
    }
}
