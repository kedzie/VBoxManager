package com.kedzie.vbox.machine.group

/**
 * Group of Virtual Machines
 */
data class VMGroup(var name: String,
                   var numGroups: Int = 0,
                   var numMachines: Int = 0) {

    /** Children elements, other [VMGroup]s or [IMachine]s  */
    var children = mutableListOf<TreeNode>()

    val simpleGroupName: String
        get() = if (name == "/") "" else name.substring(name.lastIndexOf('/') + 1)

    fun addChild(child: TreeNode) {
        if (!children.contains(child)) {
            children.add(child)
            if (child is GroupTreeNode)
                numGroups++
            else if (child is MachineTreeNode)
                numMachines++
        }
    }

    fun removeChild(child: TreeNode) {
        children.remove(child)
        if (child is GroupTreeNode)
            numGroups--
        else if (child is MachineTreeNode)
            numMachines--
    }
}
