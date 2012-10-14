package com.kedzie.vbox.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.machine.MachineView;

/**
 * Group of Virtual Machines.  Collapsible component like one introduced in VirtualBox 4.2.x
 * @author Marek Kędzierski
 */
public class VMGroupPanel extends Panel {
    private static final String TAG = "VMGroupView";
    
    private TextView _titleLabel;
    private ImageButton _enterButton;
    private TextView _numGroupsText;
    private TextView _numMachinesText;
    private VMGroup _group;
    private int _numMachines;
    private int _numGroups;
    
    public VMGroupPanel(Context context, VMGroup group) {
        super(context);
        _group = group;
        _titleLabel.setText(_group.getName());
    }
    
    protected View getTitleLayout() {
        LinearLayout titleLayout = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.vmgroup_title, this, false);
        _collapseButton = (ImageButton)titleLayout.findViewById(R.id.group_collapse);
        _collapseButton.setOnClickListener(this);
        _enterButton = (ImageButton)titleLayout.findViewById(R.id.group_enter);
        _enterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Drilling-Down to " + v);
                Utils.toastShort(getContext(), "Drill-Down into group");
            }
        });
        _numGroupsText  = (TextView)titleLayout.findViewById(R.id.group_num_groups);
        _numMachinesText  = (TextView)titleLayout.findViewById(R.id.group_num_groups);
        _titleLabel  = (TextView)titleLayout.findViewById(R.id.group_title);
        return titleLayout;
    }
    
    public void addChild(View view) {
        if(view instanceof MachineView) 
            _numMachinesText.setText(++_numMachines+"");
        else if(view instanceof VMGroupPanel)
            _numGroupsText.setText(++_numGroups+"");
        _contents.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
    
    /**
     * Create the tree components from the data model
     * @param context  the {@link Context}
     * @param node      tree structure
     * @return  Fully populated view representing the node
     */
    public static View createView(Context context, TreeNode node) {
        if(node instanceof IMachine) {
            return new MachineView(VBoxApplication.getInstance(), context);
        } else if (node instanceof VMGroup) {
            VMGroup group = (VMGroup)node;
            VMGroupPanel groupView = new VMGroupPanel(context, group);
            for(TreeNode child : group.getChildren())
                groupView.addChild(createView(context, child));
            return groupView;
        }
        throw new IllegalArgumentException("Only views of type MachineView or VMGroupView are allowed");
    }
}
