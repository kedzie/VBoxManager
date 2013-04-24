package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.Utils.AnimationAdapter;
import com.nineoldandroids.view.animation.AnimatorProxy;

/**
 * Animated collapsible Panel
 */
public class CollapsiblePanelView extends LinearLayout implements OnClickListener {
    private final int DEFAULT_COLLAPSE_ROTATION=90;
    
    /**
     * Animation for collapsing/expanding
     */
    private class PanelAnimation extends Animation {
    	
        private final int mStartHeight;
        private final int mDeltaHeight;
        private final int mStartRotation;
        private final int mDeltaRotation;

        public PanelAnimation(int startHeight, int endHeight) {
            setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
            setInterpolator(new AccelerateInterpolator());
            mStartHeight = startHeight;
            mDeltaHeight = endHeight - startHeight;
            if(isExpanding()) {
            	mStartRotation = mCollapseRotation;
            	mDeltaRotation = mCollapseRotation*-1;
            } else {
            	mStartRotation = 0;
            	mDeltaRotation = mCollapseRotation;
            }
            setAnimationListener(new AnimationAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if(isCollapsing())
                        mFrame.setVisibility(View.GONE);
                }
            });
        }   

        @Override
        protected void applyTransformation(float interpolatedTime,  Transformation t) {
            ViewGroup.LayoutParams lp = mContents.getLayoutParams();
            lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
            mContents.setLayoutParams(lp);
            AnimatorProxy.wrap(mCollapseButton).setRotation(mStartRotation + mDeltaRotation*interpolatedTime);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
        
        private boolean isExpanding() {
        	return mStartHeight==0;
        }
        
        private boolean isCollapsing() {
        	return !isExpanding();
        }
    }
    
    private boolean mExpanded=true;
    protected View mTitleView;
    private FrameLayout mFrame;
    private LinearLayout mContents;
    private Drawable mIcon;
    private View mCollapseButton;
    private int mContentGravity;
    private int mContentHeight;
    private String mTitle;
    private int mCollapseRotation=DEFAULT_COLLAPSE_ROTATION;
    
    public CollapsiblePanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Panel, 0, 0);
        try {
            mTitle = a.hasValue(R.styleable.Panel_name) ? a.getString(R.styleable.Panel_name) : "";
            mIcon = a.getDrawable(R.styleable.Panel_headerIcon);
            mCollapseRotation = a.getInt(R.styleable.Panel_collapseRotation, DEFAULT_COLLAPSE_ROTATION);
            mExpanded = a.getBoolean(R.styleable.Panel_expanded, true);
        } finally {
            a.recycle();
        }
        init(context);
    }
    
    public CollapsiblePanelView(Context context) {
        super(context);
        init(context);
    }
    
    protected void init(Context context) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);
        
        mContents = new LinearLayout(context);
        mContents.setOrientation(VERTICAL);
       	mContents.setGravity(mContentGravity);
        
        mFrame = new FrameLayout(context);
        mFrame.setBackgroundResource(R.drawable.panel_body);
        mFrame.addView(mContents, lp);
        
        super.addView(getTitleView(), lp);
        super.addView(mFrame, lp);
        
        if(!mExpanded) {
            mFrame.setVisibility(GONE);
            AnimatorProxy.wrap(mCollapseButton).setRotation(mCollapseRotation);
        }
        
        if(Utils.isVersion(Build.VERSION_CODES.HONEYCOMB))
	    	setLayerType(LAYER_TYPE_HARDWARE, null);
    }
    
    /**
     * Override to create custom Panel header.  Make sure to also override {@link CollapsiblePanelView#getCollapseButton}.
     * @return  the initialized panel header
     */
    public View getTitleView() {
        if(mTitleView==null) {
            mTitleView = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.panel_header, this, false);
            setCollapseButton(mTitleView.findViewById(R.id.group_collapse));
            Utils.setImageView(mTitleView, R.id.panel_icon, mIcon);
            Utils.setTextView(mTitleView, R.id.group_title, mTitle);
        }
        return mTitleView;
    }
    
    protected void setCollapseButton(View view) {
        mCollapseButton = view;
        mCollapseButton.setOnClickListener(this);
    }
    
    @Override
    public void addView(View child) {
        mContents.addView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        mContents.addView(child, params);
    }
    
    @Override
    public void setGravity(int gravity) {
    	mContentGravity=gravity;
    	if(mContents!=null)
    		mContents.setGravity(gravity);
    }
    
    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        mTitleView.setPressed(pressed);
        mFrame.setPressed(pressed);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        mTitleView.setSelected(selected);
        mFrame.setSelected(selected);
        if(selected)
            for(int i=0; i<mContents.getChildCount(); i++)
                mContents.getChildAt(i).setSelected(false);
    }
    
    @Override
    public void onClick(View v) {
        if(mExpanded)
            collapse();
        else
            expand();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mContentHeight == 0) {
            mContents.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
            mContentHeight = mContents.getMeasuredHeight();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    /**
     * Expand the panel
     */
    public void expand() {
    	if(mExpanded) return;
    	mFrame.setVisibility(View.VISIBLE);
        mContents.startAnimation(new PanelAnimation(0, mContentHeight));
        mExpanded = true;
    }
    
    /**
     * Collapse the panel
     */
    public void collapse() {
    	if(!mExpanded) return;
    	if(mContents.getHeight()<mContentHeight)
    		mContentHeight = mContents.getHeight();
        mContents.startAnimation(new PanelAnimation(mContentHeight, 0));
        mExpanded=false;
    }

    /**
     * @return  <em>true</em> if panel is expanded, <em>false</em> otherwise
     */
    public boolean isExpanded() {
    	return mExpanded;
    }
    
    /**
     * @param rotation      the rotation of the collapse button when the panel is collapsing
     */
    public void setCollapseRotation(int rotation) {
    	mCollapseRotation=rotation;
    }
}