package com.kedzie.vbox.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.VMGroupPanel.OnDrillDownListener;
import com.kedzie.vbox.machine.MachineView;

/**
 * 
 * @author Marek Kędzierski
 */
public class VMGroupListView extends ViewFlipper implements OnClickListener, OnDrillDownListener {
    
    public static interface TreeNodeClickListener {
        public void onTreeNodeClick(TreeNode node);
    }
    
    private VMGroup _group;
    private TreeNodeClickListener _listener;
    private Animation _slideInLeft, _slideInRight, _slideOutLeft, _slideOutRight;
    
    public VMGroupListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VMGroupListView(Context context) {
        super(context);
        init();
    }
    
    public void init() {
        _slideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        _slideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        _slideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        _slideOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
    }
    
    public void setRoot(VMGroup group) {
        _group=group;
        addView(createGroupListView(group));
    }

    public VMGroup getRoot() {
        return _group;
    }
    
    public void setSelectTreeNodeListener(TreeNodeClickListener listener) {
        _listener = listener;
    }

    private View createGroupListView(VMGroup group) {
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout _contents = new LinearLayout(getContext());
        _contents.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(_contents);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(!group.getName().equals("/")) {
            LinearLayout header = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.vmgroup_list_header, null);
            ((ImageButton)header.findViewById(R.id.group_back)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drillOut();
                }
            });
            ((TextView)header.findViewById(R.id.group_title)).setText(group.getName());
            ((TextView)header.findViewById(R.id.group_num_groups)).setText(group.getNumGroups()+"");
            ((TextView)header.findViewById(R.id.group_num_machine)).setText(group.getNumMachines()+"");
            _contents.addView(header, lp);
        }
        for(TreeNode child : group.getChildren()) 
            _contents.addView(createView(child), lp);
        return scrollView;
    }
    
    /**
     * Create the tree components from the data model
     * @param context  the {@link Context}
     * @param node      tree structure
     * @return  Fully populated view representing the node
     */
    public View createView(TreeNode node) {
        if(node instanceof IMachine) {
            MachineView view = new MachineView(getContext());
            view.update((IMachine)node);
            view.setClickable(true);
            view.setOnClickListener(this);
            return view;
        } else if (node instanceof VMGroup) {
            VMGroup group = (VMGroup)node;
            VMGroupPanel groupView = new VMGroupPanel(getContext(), group);
            groupView.setOnClickListener(this);
            groupView.setOnDrillDownListener(this);
            for(TreeNode child : group.getChildren())
                groupView.addChild(createView(child));
            return groupView;
        }
        throw new IllegalArgumentException("Only views of type MachineView or VMGroupView are allowed");
    }
    
    @Override
    public void onClick(View v) {
        if(_listener==null)
            return;
        if(v instanceof MachineView)
            _listener.onTreeNodeClick(((MachineView)v).getMachine());
        else if(v instanceof VMGroupPanel)
            _listener.onTreeNodeClick(((VMGroupPanel)v).getGroup());
        v.setSelected(true);
    }
    
    public void drillOut() {
        setInAnimation(_slideInLeft);
        setOutAnimation(_slideOutRight);
        showPrevious();
        removeViewAt(getChildCount()-1);
    }
    
    @Override
    public void onDrillDown(VMGroup group) {
        addView(createGroupListView(group));
        setInAnimation(_slideInRight);
        setOutAnimation(_slideOutLeft);
        showNext();
    }
}
