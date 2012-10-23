package com.kedzie.vbox.machine.group;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.IMachine;

/**
 * Group of Virtual Machines
 * @author Marek Kędzierski
 */
public class VMGroup implements TreeNode {
    public static String BUNDLE = "group";
    
    private List<TreeNode> _children;
    private String _name;
    private int numGroups;
    private int numMachines;
    
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
                numGroups++;
            else if(child instanceof IMachine)
                numMachines++;
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_name);
        dest.writeParcelableArray(  getChildren().toArray(new TreeNode [getChildren().size()]), 0);
    }
    
    public int getNumGroups() {
        return numGroups;
    }

    public void setNumGroups(int numGroups) {
        this.numGroups = numGroups;
    }

    public int getNumMachines() {
        return numMachines;
    }

    public void setNumMachines(int numMachines) {
        this.numMachines = numMachines;
    }

    public static final Parcelable.Creator<VMGroup> CREATOR  = new Parcelable.Creator<VMGroup>() {
        @Override
        public VMGroup createFromParcel(Parcel source) {
            VMGroup group = new VMGroup(source.readString());
            for(Parcelable p : source.readParcelableArray(VMGroup.class.getClassLoader()))
                group.addChild((TreeNode)p);
            return group;
        }
        @Override
        public VMGroup[] newArray(int size) {
            return new VMGroup[size];
        }
    };
}
