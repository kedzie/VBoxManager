package com.kedzie.vbox.machine.group;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;
import com.kedzie.vbox.api.IMachine;

/**
 * Group of Virtual Machines
 */
public class VMGroup implements TreeNode {
    public static String BUNDLE = "group";
    
    private static final ClassLoader LOADER = VMGroup.class.getClassLoader();
    
    public static final Parcelable.Creator<VMGroup> CREATOR  = new Parcelable.Creator<VMGroup>() {
        @Override
        public VMGroup createFromParcel(Parcel source) {
            VMGroup group = new VMGroup(source.readString());
            for(Parcelable p : source.readParcelableArray(LOADER))
                group.addChild((TreeNode)p);
            return group;
        }
        @Override
        public VMGroup[] newArray(int size) {
            return new VMGroup[size];
        }
    };
    
    /** Group name */
    private String _name;
    /** # of sub-groups */
    private int _numGroups;
    /** # of sub-machines */
    private int _numMachines;
    /** Children elements, other {@link VMGroup}s or {@link IMachine}s */
    private List<TreeNode> _children;
    
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
        if(!getChildren().contains(child)) {
            getChildren().add(child);
            if(child instanceof VMGroup)
                _numGroups++;
            else if(child instanceof IMachine)
                _numMachines++;
        }
    }
    
    public String getName() {
        return _name;
    }
    public void setName(String name) {
        _name=name;
    }
    public int getNumGroups() {
        return _numGroups;
    }
    public void setNumGroups(int numGroups) {
        _numGroups = numGroups;
    }
    public int getNumMachines() {
        return _numMachines;
    }
    public void setNumMachines(int numMachines) {
        _numMachines = numMachines;
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
    
    @Override
    public String toString() {
        return _name;
    }
    
    @Override
    public boolean equals(Object other) {
    	if(this==other) return true;
        if(other==null || !(other instanceof VMGroup)) 
            return false;
        VMGroup that = (VMGroup)other;
        return Objects.equal(_name, that._name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_name);
        dest.writeParcelableArray(  getChildren().toArray(new TreeNode [getChildren().size()]), 0);
    }
}
