package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.kedzie.vbox.R;

/**
 * Collapsible Panel
 * @author Marek Kędzierski
 */
public class Panel extends LinearLayout implements OnClickListener {
    public static final int COLLAPSE_DURATION = 300;
    protected int BORDER_WIDTH = Utils.dpiToPixels(8);
    
    protected boolean _expanded=true;
    protected ImageButton _collapseButton;
    protected View _titleView;
    protected LinearLayout _contents;
    protected FrameLayout _frame;
    protected Drawable _collapseDrawable, _expandDrawable;
    protected Drawable _icon;
    protected int _contentHeight;
    private String _title;
    
    public Panel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Panel, 0, 0);
        try {
            _title = a.hasValue(R.styleable.Panel_name) ? a.getString(R.styleable.Panel_name) : "";
            _icon = a.getDrawable(R.styleable.Panel_headerIcon);
        } finally {
            a.recycle();
        }
        init(context);
    }
    
    public Panel(Context context) {
        super(context);
        init(context);
    }
    
    protected void init(Context context) {
        _collapseDrawable = context.getResources().getDrawable(R.drawable.ic_navigation_collapse);
        _expandDrawable = context.getResources().getDrawable(R.drawable.ic_navigation_expand);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        
        setOrientation(VERTICAL);
        super.addView(_titleView=getTitleLayout(), lp);
        
        _frame = new FrameLayout(context);
        _frame.setBackgroundResource(R.drawable.panel_body);
        super.addView(_frame, lp);
        
        _contents = new LinearLayout(context);
        _contents.setClipChildren(false);
        _contents.setOrientation(VERTICAL);
        
        _frame.addView(_contents, lp);
    }
    
    /**
     * Override to create custom Panel header.  Make sure to also override {@link Panel#getCollapseButton}.
     * @return  the initialized panel header
     * @see {@link Panel#getCollapseButton}
     */
    protected View getTitleLayout() {
        LinearLayout titleLayout = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.panel_header, this, false);
        _collapseButton = (ImageButton)titleLayout.findViewById(R.id.group_collapse);
        _collapseButton.setOnClickListener(this);
        Utils.setImageView(titleLayout, R.id.panel_icon, _icon);
        Utils.setTextView(titleLayout, R.id.group_title, _title);
        return titleLayout;
    }
    
    /**
     * Specify custom collapse button if {@link Panel#getTitleLayout} is overridden
     * @return  the {@link ImageButton} to collapse/expand the panel
     */
    protected ImageButton getCollapseButton() {
        return _collapseButton;
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (_contentHeight == 0) {
            _contents.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
            _contentHeight = _contents.getMeasuredHeight();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onClick(View v) {
        if(_expanded) { //collapsing
            _contents.setVisibility(View.INVISIBLE);
            _contents.startAnimation(new PanelAnimation(_contentHeight, 0));
        } else {                //expanding
            _frame.setVisibility(View.VISIBLE);
            _contents.startAnimation(new PanelAnimation(0, _contentHeight));
        }
        _expanded = !_expanded;
    }

    protected class PanelAnimation extends Animation {
        private final int mStartHeight;
        private final int mDeltaHeight;

        public PanelAnimation(int startHeight, int endHeight) {
            setDuration(COLLAPSE_DURATION);
            mStartHeight = startHeight;
            mDeltaHeight = endHeight - startHeight;
            setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mStartHeight==0) { //expanding 
                        _contents.setVisibility(View.VISIBLE);
                        _collapseButton.setImageDrawable(_collapseDrawable);
                    } else {       //collapsing
                        _frame.setVisibility(View.GONE);
                        _collapseButton.setImageDrawable(_expandDrawable);
                    }
                }
            });
        }   

        @Override
        protected void applyTransformation(float interpolatedTime,  Transformation t) {
            ViewGroup.LayoutParams lp = _contents.getLayoutParams();
            lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
            _contents.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}