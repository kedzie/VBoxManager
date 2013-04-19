package com.kedzie.vbox.machine.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.machine.group.VMGroupPanel.OnDrillDownListener;

/**
 * Scrollable list of {@link VMGroup} objects with drill-down support to focus on a particular group.
 * 
 * @author Marek Kędzierski
 */
public class VMGroupListView extends ViewFlipper implements OnClickListener, OnLongClickListener, OnDrillDownListener { 
    private static final String TAG = "VMGroupListView";

    /**
     * Callback for element selection
     */
    public static interface OnTreeNodeSelectListener {
        /**
         * An element has been selected
         * @param node	the selected element
         */
        public void onTreeNodeSelect(TreeNode node);
    }

    /** View associated with the currently selected element */
    private View _selected;

    /** Is element selection enabled */
    private boolean _selectionEnabled;

    private OnTreeNodeSelectListener _listener;

    /** Maintains reference from a particular Machine to all views which reference it.  Used for updating views when events are received. */
    private Map<String, List<MachineView>> _machineViewMap = new HashMap<String, List<MachineView>>();
    /** Maintains reference from a particular <code>VMGroup</code> to all views which reference it.  Used for updating views when groups chnage. */
    private Map<String, List<VMGroupPanel>> _groupViewMap = new HashMap<String, List<VMGroupPanel>>();

    private Animation _slideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
    private Animation _slideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
    private Animation _slideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
    private Animation _slideOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);

    private Dragger mDragger;
    private IMachine mDraggedMutable;

    public VMGroupListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VMGroupListView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if(Utils.isVersion(Build.VERSION_CODES.HONEYCOMB))
            mDragger = new Dragger();
    }

    public void setRoot(VMGroup group) {
        addView(createGroupListView(group));
    }

    @Override
    public void onDrillDown(VMGroup group) {
        addView(createGroupListView(group));
        setInAnimation(_slideInRight);
        setOutAnimation(_slideOutLeft);
        showNext();
    }

    public void drillOut() {
        setInAnimation(_slideInLeft);
        setOutAnimation(_slideOutRight);
        showPrevious();
        removeViewAt(getChildCount()-1);
    }

    public void setOnTreeNodeSelectListener(OnTreeNodeSelectListener listener) {
        _listener = listener;
    }

    public boolean isSelectionEnabled() {
        return _selectionEnabled;
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        _selectionEnabled = selectionEnabled;
    }

    /**
     * Build a scrollable list of everything below a group
     * @param group		the root
     * @return	scrollable list of things below the group
     */
    private View createGroupListView(VMGroup group) {
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout _contents = new LinearLayout(getContext());
        _contents.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(_contents);
        if(!group.getName().equals("/")) {
            LinearLayout header = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.vmgroup_list_header, null);
            ((ImageView)header.findViewById(R.id.group_back)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drillOut();
                }
            });
            Utils.setTextView(header, R.id.group_title, group.getName());
            Utils.setTextView(header, R.id.group_num_groups, group.getNumGroups());
            Utils.setTextView(header, R.id.group_num_machine, group.getNumMachines());
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = Utils.dpiToPixels(getContext(), 4);
            _contents.addView(header, lp);
        }
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        for(TreeNode child : group.getChildren()) 
            _contents.addView(createView(child), lp);
        return scrollView;
    }

    /**
     * Create a view for a single node in the tree
     * @param context  the {@link Context}
     * @param node      tree node
     * @return  Fully populated view representing the node
     */
    public View createView(TreeNode node) {
        if(node instanceof IMachine) {
            MachineView view = new MachineView(getContext());
            IMachine m = (IMachine)node;
            view.update(m);
            view.setBackgroundResource(R.drawable.list_selector_color);
            view.setClickable(true);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            if(!_machineViewMap.containsKey(m.getIdRef()))
                _machineViewMap.put(m.getIdRef(), new ArrayList<MachineView>());
            _machineViewMap.get(m.getIdRef()).add(view);
            return view;
        } else if (node instanceof VMGroup) {
            VMGroup group = (VMGroup)node;
            VMGroupPanel groupView = new VMGroupPanel(getContext(), group);
            groupView.setOnClickListener(this);
            groupView.setOnDrillDownListener(this);
            if(Utils.isVersion(Build.VERSION_CODES.HONEYCOMB))
                groupView.setOnDragListener(mDragger);
            for(TreeNode child : group.getChildren())
                groupView.addChild(createView(child));
            if(!_groupViewMap.containsKey(group.getName()))
                _groupViewMap.put(group.getName(), new ArrayList<VMGroupPanel>());
            _groupViewMap.get(group.getName()).add(groupView);
            return groupView;
        }
        throw new IllegalArgumentException("Only views of type MachineView or VMGroupView are allowed");
    }

    /**
     * Update all machine views with new data
     * @param machine       the machine to update (properties must be cached)
     */
    public void update(IMachine machine) {
        for(MachineView view : _machineViewMap.get(machine.getIdRef()))
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
        if(_selected==v) {      //Deselect existing selection
            _selected.setSelected(false);
            _selected=null;
            _listener.onTreeNodeSelect(null);
            return;
        } 
        if(_selected!=null)	//Make new Selection
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
        if(!Utils.isVersion(Build.VERSION_CODES.HONEYCOMB))
            return true;
        if(v instanceof MachineView) {
            MachineView view = (MachineView)v;
            new DragMachineTask().execute(view);
        }
        return true;
    }

    private class DragMachineTask extends AsyncTask<MachineView, Void, IMachine> {

        private MachineView mView;

        @Override
        protected IMachine doInBackground(MachineView... params) {
            mView = params[0];
            IMachine machine = mView.getMachine();
            try {
                if(machine.getSessionState().equals(SessionState.UNLOCKED)) {
                    ISession session = machine.getAPI().getVBox().getSessionObject();
                    machine.lockMachine(session, LockType.WRITE);
                    return session.getMachine();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(IMachine result) {
            super.onPostExecute(result);
            if(result!=null) {
                mDraggedMutable = result;
                ClipData data = ClipData.newPlainText("Machine Drag", mView.getMachine().getIdRef());
                mView.startDrag(data, new DragShadowBuilder(mView), null, 0);
            }
        }
    }

    private class Dragger implements OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if(!(v instanceof VMGroupPanel))
                return false;
            final VMGroupPanel view = (VMGroupPanel)v;

            final int action = event.getAction();
            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED: 
                    view.setBackgroundColor(Color.RED);
                    view.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    for(int i=0; i<view.getChildCount(); i++) {
                        View child = view.getChildAt(i);
                        Rect frame = new Rect();
                        child.getHitRect(frame);
                        if(frame.contains((int)event.getX(), (int)event.getY())) {
                            Log.d(TAG, "Drag inside " + child);
                        }
                    }
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    view.setBackgroundColor(Color.TRANSPARENT);
                    view.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    view.setBackgroundColor(Color.TRANSPARENT);
                    view.invalidate();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mDraggedMutable.setGroups(view.getGroup().getName());
                                mDraggedMutable.saveSettings();
                                mDraggedMutable.getAPI().getVBox().getSessionObject().unlockMachine();
                                mDraggedMutable=null;
                            } catch (IOException e) {
                                Log.e(TAG, "Error", e);
                            }
                        }
                    }).start();
                    String machineIdRef = event.getClipData().getItemAt(0).getText().toString();
                    List<MachineView> machineViews = _machineViewMap.get(machineIdRef);
                    List<VMGroupPanel> groupViews = _groupViewMap.get(view.getGroup().getName());
                    groupViews.get(0).getGroup().addChild(machineViews.get(0).getMachine());
                    for(MachineView mv : machineViews) {
                        ((ViewGroup)mv.getParent()).removeView(mv);
                    }
                    for(int i=0; i<machineViews.size(); i++) {
                        MachineView mv = machineViews.get(i);
                        VMGroupPanel gv = groupViews.get(i);
                        gv.addView(mv, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    view.setBackgroundColor(Color.TRANSPARENT);
                    view.invalidate();
                    if (event.getResult())
                        Utils.toastShort(getContext(), "The drop was handled.");
                    else
                        Utils.toastShort(getContext(), "The drop didn't work.");
                    return true;
            }
            return false;
        }
    }
}
