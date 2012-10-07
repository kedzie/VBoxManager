package com.kedzie.vbox.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of Virtual Machines
 * @author Marek Kędzierski
 */
public class VMGroup implements TreeNode {

    private List<TreeNode> _children;
    private String _name;
    
    public VMGroup(String name) {
        _name = name;
    }
    
    public List<TreeNode> getChildren() {
        if(_children==null)
            _children  = new ArrayList<TreeNode>();
        return _children;
    }
    public void setChildren(List<TreeNode> children) {
        _children = children;
    }
    public void addChild(TreeNode child) {
        if(!getChildren().contains(child))
            getChildren().add(child);
    }
    public String getName() {
        return _name;
    }
    public void setName(String name) {
        _name=name;
    }
    public static String getTreeString(int level, TreeNode node) {
        StringBuilder str = new StringBuilder("");
        str.append(node.getName());
        if(node instanceof IMachine) 
            return str.toString();
        VMGroup group = (VMGroup)node;
        for(TreeNode child : group.getChildren()) {
            str.append("\n");
            for(int i=0; i<level; i++) str.append("\t");
            str.append("|====>");
            str.append(getTreeString(level+1, child));
        }
        return str.toString();
    }
    public String toString() {
        return _name;
    }
    public boolean equals(Object other) {
        if(!(other instanceof VMGroup)) 
            return false;
        VMGroup that = (VMGroup)other;
        return this._name.equals(that._name);
    }
    public int hashCode() {
        return _name.hashCode();
    }
}
