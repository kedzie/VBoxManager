package com.kedzie.vbox.machine.group

import java.security.acl.Group

import android.os.Parcelable

import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.api.IMachineEntity
import kotlinx.android.parcel.Parcelize

/**
 * Base class for elements in the Machine List.
 * Can be either a [Group] or [IMachine]
 * @author Marek Kędzierski
 */
sealed class TreeNode

class MachineTreeNode(val machine: IMachineEntity): TreeNode()

class GroupTreeNode(val group: VMGroup): TreeNode()