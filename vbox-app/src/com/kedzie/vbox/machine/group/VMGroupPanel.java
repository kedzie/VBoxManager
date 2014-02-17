package com.kedzie.vbox.machine.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.CollapsiblePanelView;

/**
 * Group of Virtual Machines.  Collapsible component like one introduced in VirtualBox 4.2.x
 * @author Marek Kędzierski
 */
public class VMGroupPanel extends CollapsiblePanelView {
    public static final int COLLAPSE_ROTATION = -90;
    
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
        setFocusable(true);
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        setCollapseRotation(COLLAPSE_ROTATION);
        _group = group;
        _titleLabel.setText(_group.getName());
        _numGroupsText.setText(_group.getNumGroups()+"");
        _numMachinesText.setText(_group.getNumMachines()+"");
    }
    
    public View getTitleView() {
        if(mTitleView==null) {
            mTitleView = (ExpandableLinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.vmgroup_title, this, false);
            setCollapseButton(mTitleView.findViewById(R.id.group_collapse));
            _drillDownButton = (ImageView)mTitleView.findViewById(R.id.group_enter);
            _drillDownButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(_drillDownListener!=null)
                        _drillDownListener.onDrillDown(_group);
                }
            });
            _numGroupsText  = (TextView)mTitleView.findViewById(R.id.group_num_groups);
            _numMachinesText  = (TextView)mTitleView.findViewById(R.id.group_num_machine);
            _titleLabel  = (TextView)mTitleView.findViewById(R.id.group_title);
        }
        return mTitleView;
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
