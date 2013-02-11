package com.kedzie.vbox.machine.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.PanelView;

/**
 * Group of Virtual Machines.  Collapsible component like one introduced in VirtualBox 4.2.x
 * @author Marek Kędzierski
 */
public class VMGroupPanel extends PanelView {
    
    /**
     * Listener for Drill-Down button
     */
    public static interface OnDrillDownListener {
    	
        /**
         * The drill-down button has been pressed for a group
         * @param group		the group to focus on 
         */
        public void onDrillDown(VMGroup group);
    }
    
    private TextView _titleLabel;
    private ImageView _drillDownButton;
    private OnDrillDownListener _drillDownListener;
    private TextView _numGroupsText;
    private TextView _numMachinesText;
    /** The group this panel represents */
    private VMGroup _group;
    
    public VMGroupPanel(Context context, VMGroup group) {
        super(context);
        setClickable(true);
        setCollapseRotation(-90);
        _group = group;
        _titleLabel.setText(_group.getName());
        _numGroupsText.setText(_group.getNumGroups()+"");
        _numMachinesText.setText(_group.getNumMachines()+"");
    }
    
    protected View getTitleLayout() {
        LinearLayout titleLayout = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.vmgroup_title, this, false);
        _collapseButton = (ImageView)titleLayout.findViewById(R.id.group_collapse);
        _collapseButton.setOnClickListener(this);
        _drillDownButton = (ImageView)titleLayout.findViewById(R.id.group_enter);
        _drillDownButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_drillDownListener!=null)
                    _drillDownListener.onDrillDown(_group);
            }
        });
        _numGroupsText  = (TextView)titleLayout.findViewById(R.id.group_num_groups);
        _numMachinesText  = (TextView)titleLayout.findViewById(R.id.group_num_machine);
        _titleLabel  = (TextView)titleLayout.findViewById(R.id.group_title);
        return titleLayout;
    }
    
    public void addChild(View view) {
        addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
    
    public VMGroup getGroup() {
        return _group;
    }
    
    public void setOnDrillDownListener(OnDrillDownListener listener) {
        _drillDownListener=listener;
    }
}
