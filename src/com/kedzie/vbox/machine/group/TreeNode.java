package com.kedzie.vbox.machine.group;

import java.security.acl.Group;

import android.os.Parcelable;

import com.kedzie.vbox.api.IMachine;

/**
 * Base class for elements in the Machine List.  
 * Can be either a {@link Group} or {@link IMachine}
 * @author Marek Kędzierski
 */
public interface TreeNode extends Parcelable {
    public String getName();
}
