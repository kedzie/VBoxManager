package com.kedzie.vbox.machine.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.machine.group.VMGroupPanel.OnDrillDownListener;

/**
 * 
 * @author Marek Kędzierski
 */
public class VMGroupListView extends ViewFlipper implements OnClickListener, OnLongClickListener, OnDrillDownListener {
    
    public static interface OnTreeNodeSelectListener {
        public void onTreeNodeSelect(TreeNode node);
    }
    
    public static interface OnTreeNodeLongClickListener {
        public void onTreeNodeLongClick(TreeNode node);
    }
    
    private VMGroup _group;
    private View _selected;
    private boolean _selectionEnabled;
    private OnTreeNodeSelectListener _listener;
    private OnTreeNodeLongClickListener _longClickListener;
    private Map<IMachine, List<MachineView>> _machineViewMap = new HashMap<IMachine, List<MachineView>>();
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
    
    public void setOnTreeNodeSelectListener(OnTreeNodeSelectListener listener) {
        _listener = listener;
    }
    
    public void setOnTreeNodeLongClickListener(OnTreeNodeLongClickListener listener) {
        _longClickListener = listener;
    }

    private View createGroupListView(VMGroup group) {
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout _contents = new LinearLayout(getContext());
        scrollView.addView(_contents);
        _contents.setOrientation(LinearLayout.VERTICAL);
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
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = Utils.dpiToPixels(4);
            _contents.addView(header, lp);
        }
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
            IMachine m = (IMachine)node;
            view.update(m);
            view.setBackgroundResource(R.drawable.list_selector_background);
            view.setClickable(true);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            if(!_machineViewMap.containsKey(m))
                _machineViewMap.put(m, new ArrayList<MachineView>());
            _machineViewMap.get(m).add(view);
            return view;
        } else if (node instanceof VMGroup) {
            VMGroup group = (VMGroup)node;
            VMGroupPanel groupView = new VMGroupPanel(getContext(), group);
            groupView.setOnClickListener(this);
            groupView.setOnLongClickListener(this);
            groupView.setOnDrillDownListener(this);
            for(TreeNode child : group.getChildren())
                groupView.addChild(createView(child));
            return groupView;
        }
        throw new IllegalArgumentException("Only views of type MachineView or VMGroupView are allowed");
    }
    
    /**
     * Update all machine views with new data
     * @param machine       the machine to update (properties must be cached)
     */
    public void update(IMachine machine) {
        for(MachineView view : _machineViewMap.get(machine))
            view.update(machine);
    }
    
    @Override
    public void onClick(View v) {
        if(_listener==null)
            return;
        if(!_selectionEnabled) {
            notifyListener(v);
            return;
        }
        //Deselect existing selection
        if(_selected==v) {      
            _selected.setSelected(false);
            _selected=null;
            _listener.onTreeNodeSelect(null);
            return;
        } 
        //Make new Selection
        if(_selected!=null)
            _selected.setSelected(false);
        _selected=v;
        _selected.setSelected(true);
        notifyListener(_selected);
    }
    
    private void notifyListener(View v) {
        if(v instanceof MachineView)
            _listener.onTreeNodeSelect(((MachineView)v).getMachine());
        else if(v instanceof VMGroupPanel)
            _listener.onTreeNodeSelect(((VMGroupPanel)v).getGroup());
    }
    
    @Override
    public boolean onLongClick(View v) {
        if(_longClickListener==null)
            return true;
        if(v instanceof MachineView)
            _longClickListener.onTreeNodeLongClick(((MachineView)v).getMachine());
        else if(v instanceof VMGroupPanel)
            _longClickListener.onTreeNodeLongClick(((VMGroupPanel)v).getGroup());
        return true;
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
    
    public boolean isSelectionEnabled() {
        return _selectionEnabled;
    }

    public void setSelectionEnabled(boolean _selectionEnabled) {
        this._selectionEnabled = _selectionEnabled;
    }
}
