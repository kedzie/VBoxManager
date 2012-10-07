package com.kedzie.vbox.api;

/**
 * Base class for elements in the Machine List.  
 * Can be either a {@link Group} or {@link IMachine}
 * @author Marek Kędzierski
 */
public interface TreeNode {
    public String getName();
}
