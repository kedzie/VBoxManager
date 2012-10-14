package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.R;

/**
 * Collapsible Panel
 * @author Marek Kędzierski
 */
public class Panel extends LinearLayout implements OnClickListener {
    private static final String TAG = "Panel";
    public static final int COLLAPSE_DURATION = 300;
    protected int BORDER_WIDTH = Utils.dpiToPixels(8);
    
    protected boolean _expanded=true;
    protected ImageButton _collapseButton;
    protected View _titleView;
    protected LinearLayout _contents;
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
        
        FrameLayout frame = new FrameLayout(context);
        frame.setPadding(BORDER_WIDTH, 0, BORDER_WIDTH, BORDER_WIDTH);
        frame.setBackgroundDrawable(getResources().getDrawable(R.drawable.panel_body));
        super.addView(frame, lp);
        
        _contents = new LinearLayout(context);
//        _contents.setBackgroundColor(R.color.w);
        _contents.setClipChildren(false);
        _contents.setOrientation(VERTICAL);
        _contents.setShowDividers(SHOW_DIVIDER_BEGINNING & SHOW_DIVIDER_MIDDLE & SHOW_DIVIDER_END);
        
        frame.addView(_contents, lp);
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
        ImageView iconImage = (ImageView)titleLayout.findViewById(R.id.panel_icon);
        iconImage.setImageDrawable(_icon);
        TextView titleLabel  = (TextView)titleLayout.findViewById(R.id.group_title);
        titleLabel.setText(_title);
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
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        Log.d(TAG, "addView("+child+","+params+")");
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
        Animation a;
        if (_expanded) {
            a = new ExpandAnimation(_contentHeight, 0);
            getCollapseButton().setImageDrawable(_expandDrawable);
        } else { 
            a = new ExpandAnimation(0, _contentHeight);
            getCollapseButton().setImageDrawable(_collapseDrawable);
        }
        a.setDuration(COLLAPSE_DURATION);
        _contents.startAnimation(a);
        _expanded = !_expanded;
    }

    protected class ExpandAnimation extends Animation {
        private final int mStartHeight;
        private final int mDeltaHeight;

        public ExpandAnimation(int startHeight, int endHeight) {
            mStartHeight = startHeight;
            mDeltaHeight = endHeight - startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime,  Transformation t) {
            android.view.ViewGroup.LayoutParams lp = _contents.getLayoutParams();
            lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
            _contents.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}