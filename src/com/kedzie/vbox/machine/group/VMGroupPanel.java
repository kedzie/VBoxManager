package com.kedzie.vbox.machine.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.Panel;

/**
 * Group of Virtual Machines.  Collapsible component like one introduced in VirtualBox 4.2.x
 * @author Marek Kędzierski
 */
public class VMGroupPanel extends Panel {
    
    public static interface OnDrillDownListener {
        public void onDrillDown(VMGroup group);
    }
    
    private TextView _titleLabel;
    private ImageButton _drillDownButton;
    private OnDrillDownListener _drillDownListener;
    private TextView _numGroupsText;
    private TextView _numMachinesText;
    
    private VMGroup _group;
    
    public VMGroupPanel(Context context, VMGroup group) {
        super(context);
        setClickable(true);
        _group = group;
        _titleLabel.setText(_group.getName());
        _numGroupsText.setText(_group.getNumGroups()+"");
        _numMachinesText.setText(_group.getNumMachines()+"");
    }
    
    protected View getTitleLayout() {
        LinearLayout titleLayout = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.vmgroup_title, this, false);
        _collapseButton = (ImageButton)titleLayout.findViewById(R.id.group_collapse);
        _collapseButton.setOnClickListener(this);
        _drillDownButton = (ImageButton)titleLayout.findViewById(R.id.group_enter);
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
    
    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        _titleView.setPressed(pressed);
        _frame.setPressed(pressed);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        _titleView.setSelected(selected);
        _frame.setSelected(selected);
        if(selected) {  //if group is selected, unselect all of it's children
            for(int i=0; i<_contents.getChildCount(); i++)
                _contents.getChildAt(i).setSelected(false);
        }
    }

    public void addChild(View view) {
        _contents.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
    
    public VMGroup getGroup() {
        return _group;
    }
    public void setOnDrillDownListener(OnDrillDownListener listener) {
        _drillDownListener=listener;
    }
}