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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.Utils.AnimationAdapter;
import com.nineoldandroids.view.animation.AnimatorProxy;

/**
 * Animated collapsible Panel
 */
public class PanelView extends LinearLayout implements OnClickListener {
    
    /**
     * Animation for collapsing/expanding the panel contents
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
            	mStartRotation = _collapseRotation;
            	mDeltaRotation = _collapseRotation*-1;
            } else {
            	mStartRotation = 0;
            	mDeltaRotation = _collapseRotation;
            }
            setAnimationListener(new AnimationAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if(isCollapsing())
                        _frame.setVisibility(View.GONE);
                }
            });
        }   

        @Override
        protected void applyTransformation(float interpolatedTime,  Transformation t) {
            ViewGroup.LayoutParams lp = _contents.getLayoutParams();
            lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
            _contents.setLayoutParams(lp);
            AnimatorProxy.wrap(_collapseButton).setRotation(mStartRotation + mDeltaRotation*interpolatedTime);
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
    
    private final int DEFAULT_COLLAPSE_ROTATION=90;
    
    private boolean _expanded=true;
    protected ImageView _collapseButton;
    private View _titleView;
    private LinearLayout _contents;
    private int _contentGravity=0;
    private FrameLayout _frame;
    private Drawable _icon;
    private int _contentHeight;
    private String _title;
    private int _collapseRotation=DEFAULT_COLLAPSE_ROTATION;
    
    public PanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Panel, 0, 0);
        try {
            _title = a.hasValue(R.styleable.Panel_name) ? a.getString(R.styleable.Panel_name) : "";
            _icon = a.getDrawable(R.styleable.Panel_headerIcon);
            _collapseRotation = a.getInt(R.styleable.Panel_collapseRotation, DEFAULT_COLLAPSE_ROTATION);
        } finally {
            a.recycle();
        }
        init(context);
    }
    
    public PanelView(Context context) {
        super(context);
        init(context);
    }
    
    protected void init(Context context) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);
        _titleView=getTitleLayout();
        
        _contents = new LinearLayout(context);
        _contents.setOrientation(VERTICAL);
       	_contents.setGravity(_contentGravity);
        
        _frame = new FrameLayout(context);
        _frame.setBackgroundResource(R.drawable.panel_body);
        _frame.addView(_contents, lp);
        
        super.addView(_titleView, lp);
        super.addView(_frame, lp);
        
        if(Utils.isVersion(Build.VERSION_CODES.HONEYCOMB))
	    	setLayerType(LAYER_TYPE_HARDWARE, null);
    }
    
    /**
     * Override to create custom Panel header.  Make sure to also override {@link PanelView#getCollapseButton}.
     * @return  the initialized panel header
     */
    protected View getTitleLayout() {
        LinearLayout titleLayout = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.panel_header, this, false);
        _collapseButton = (ImageView)titleLayout.findViewById(R.id.group_collapse);
        _collapseButton.setOnClickListener(this);
        Utils.setImageView(titleLayout, R.id.panel_icon, _icon);
        Utils.setTextView(titleLayout, R.id.group_title, _title);
        return titleLayout;
    }
    
    @Override
    public void addView(View child) {
        _contents.addView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        _contents.addView(child, params);
    }
    
    @Override
    public void setGravity(int gravity) {
    	_contentGravity=gravity;
    	if(_contents!=null)
    		_contents.setGravity(gravity);
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
        if(selected)
            for(int i=0; i<_contents.getChildCount(); i++)
                _contents.getChildAt(i).setSelected(false);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (_contentHeight == 0) {
            _contents.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
            _contentHeight = _contents.getMeasuredHeight();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    public void expand() {
    	if(_expanded) return;
    	_frame.setVisibility(View.VISIBLE);
        _contents.startAnimation(new PanelAnimation(0, _contentHeight));
        _expanded = true;
    }
    
    public void collapse() {
    	if(!_expanded) return;
    	if(_contents.getHeight()<_contentHeight)
    		_contentHeight = _contents.getHeight();
        _contents.startAnimation(new PanelAnimation(_contentHeight, 0));
        _expanded=false;
    }

    public void onClick(View v) {
        if(_expanded)
        	collapse();
        else
            expand();
    }
    
    protected boolean isExpanded() {
    	return _expanded;
    }
    
    protected void setCollapseRotation(int rotation) {
    	_collapseRotation=rotation;
    }
}