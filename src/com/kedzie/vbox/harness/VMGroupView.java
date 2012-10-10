package com.kedzie.vbox.harness;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.TreeNode;
import com.kedzie.vbox.api.VMGroup;
import com.kedzie.vbox.machine.MachineView;

/**
 * 
 * @author Marek Kędzierski
 */
public class VMGroupView extends LinearLayout {
    private static final String TAG = "VMGroupView";

    private Animation _expand;
    private Animation _shrink;
    private LinearLayout _titleLayout;
    private LinearLayout _contents;
    private ImageButton _collapseButton;
    private ImageButton _enterButton;
    private ImageView _numGroupsImage;
    private ImageView _numMachinesImage;
    private TextView _numGroupsText;
    private TextView _numMachinesText;
    private TextView _titleLabel;
    private LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private LayoutTransition mTransitioner;
    private VMGroup _group;
    private int _numMachines;
    private int _numGroups;
    
    public VMGroupView(Context context, VMGroup group) {
        super(context);
        _expand = AnimationUtils.loadAnimation(context, R.anim.expand);
        _shrink = AnimationUtils.loadAnimation(context, R.anim.shrink);
        
        _group = group;
        setOrientation(LinearLayout.VERTICAL);
        setPadding(8, 8, 8, 8);
        setLayoutTransition(getLayoutTransition());
        setClipChildren(false);
        
        _contents = new LinearLayout(context);
        _contents.setClipChildren(false);
        _contents.setOrientation(LinearLayout.VERTICAL);
        _contents.setBackgroundColor(0xFF646464);
        _contents.setLayoutTransition(getLayoutTransition());
        _contents.setShowDividers(SHOW_DIVIDER_BEGINNING & SHOW_DIVIDER_MIDDLE & SHOW_DIVIDER_END);
        
        _titleLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.vmgroup_title, this, false);
        _collapseButton = (ImageButton)_titleLayout.findViewById(R.id.group_collapse);
        _enterButton = (ImageButton)_titleLayout.findViewById(R.id.group_enter);
        _numGroupsImage  = (ImageView)_titleLayout.findViewById(R.id.group_num_groups_image);
        _numGroupsText  = (TextView)_titleLayout.findViewById(R.id.group_num_groups);
        _numMachinesImage  = (ImageView)_titleLayout.findViewById(R.id.group_num_machine_image);
        _numMachinesText  = (TextView)_titleLayout.findViewById(R.id.group_num_groups);
        _titleLabel  = (TextView)_titleLayout.findViewById(R.id.group_title);
        
        addView(_titleLayout, lp);
        addView(_contents, lp);

        _collapseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "collapse");
                ObjectAnimator.ofInt(_contents, "height", _contents.getHeight(), 0).setDuration(1000).start();
//                _contents.startAnimation(_contents.getVisibility()==View.VISIBLE ? _shrink : _expand);
            }
        });
        
        _enterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "enter");
                _contents.setVisibility(_contents.getVisibility()==View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        _titleLabel.setText(_group.getName());
    }
    
    public LayoutTransition getLayoutTransition() {
        if(mTransitioner==null) {
            mTransitioner = new LayoutTransition();
            setupCustomAnimations();
            mTransitioner.setAnimateParentHierarchy(true);
        }
        return mTransitioner;
    }
    
    public void addChild(View view) {
        if(view instanceof MachineView) 
            _numMachinesText.setText(++_numMachines+"");
        else if(view instanceof VMGroupView)
            _numGroupsText.setText(++_numGroups+"");
        _contents.addView(view, lp);
    }
    
    public void init() {
        for(TreeNode child : _group.getChildren()) 
            addChild(populate(child));
    }
    
    public View populate(TreeNode node) {
        if(node instanceof IMachine) {
            return new MachineView(VBoxApplication.getInstance(), getContext());
        } else if (node instanceof VMGroup) {
            VMGroup group = (VMGroup)node;
            VMGroupView groupView = new VMGroupView(getContext(), group);
            for(TreeNode child : group.getChildren())
                groupView.addChild(populate(child));
            return groupView;
        }
        throw new IllegalArgumentException("Only views of type MachineView or VMGroupView are allowed");
    }
    
    private void setupCustomAnimations() {
        // Changing while Adding
        PropertyValuesHolder pvhLeft =
                PropertyValuesHolder.ofInt("left", 0, 1);
        PropertyValuesHolder pvhTop =
                PropertyValuesHolder.ofInt("top", 0, 1);
        PropertyValuesHolder pvhRight =
                PropertyValuesHolder.ofInt("right", 0, 1);
        PropertyValuesHolder pvhBottom =
                PropertyValuesHolder.ofInt("bottom", 0, 1);
        PropertyValuesHolder pvhScaleX =
                PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
        PropertyValuesHolder pvhScaleY =
                PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);
        final ObjectAnimator changeIn = ObjectAnimator.ofPropertyValuesHolder(
                        this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhScaleX, pvhScaleY).
                setDuration(mTransitioner.getDuration(LayoutTransition.CHANGE_APPEARING));
        mTransitioner.setAnimator(LayoutTransition.CHANGE_APPEARING, changeIn);
        changeIn.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
        });

        // Changing while Removing
        Keyframe kf0 = Keyframe.ofFloat(0f, 0f);
        Keyframe kf1 = Keyframe.ofFloat(.9999f, 360f);
        Keyframe kf2 = Keyframe.ofFloat(1f, 0f);
        PropertyValuesHolder pvhRotation =
                PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1, kf2);
        final ObjectAnimator changeOut = ObjectAnimator.ofPropertyValuesHolder(
                        this, pvhLeft, pvhTop, pvhRight, pvhBottom, pvhRotation).
                setDuration(mTransitioner.getDuration(LayoutTransition.CHANGE_DISAPPEARING));
        mTransitioner.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, changeOut);
        changeOut.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotation(0f);
            }
        });

        // Adding
        ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "rotationY", 90f, 0f).
                setDuration(mTransitioner.getDuration(LayoutTransition.APPEARING));
        mTransitioner.setAnimator(LayoutTransition.APPEARING, animIn);
        animIn.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotationY(0f);
            }
        });

        // Removing
        ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "rotationX", 0f, 90f).
                setDuration(mTransitioner.getDuration(LayoutTransition.DISAPPEARING));
        mTransitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
        animOut.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator anim) {
                View view = (View) ((ObjectAnimator) anim).getTarget();
                view.setRotationX(0f);
            }
        });

    }

}
