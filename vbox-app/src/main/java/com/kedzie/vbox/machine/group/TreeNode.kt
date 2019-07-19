package com.kedzie.vbox.machine.group

import java.security.acl.Group

import android.os.Parcelable

import com.kedzie.vbox.api.IMachine
import kotlinx.android.parcel.Parcelize

/**
 * Base class for elements in the Machine List.
 * Can be either a [Group] or [IMachine]
 * @author Marek Kędzierski
 */
sealed class TreeNode : Parcelable

@Parcelize
class MachineTreeNode(val machine: IMachine): TreeNode()

@Parcelize
class GroupTreeNode(val group: VMGroup): TreeNode()