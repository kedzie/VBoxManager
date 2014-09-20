package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.Utils.AnimationAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.animation.AnimatorProxy;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.*;

/**
 * Animated collapsible Panel
 *
 * @attr ref com.cnh.android.widget.R.styleable#ProgressiveDisclosureView_name
 * @attr ref com.cnh.android.widget.R.styleable#ProgressiveDisclosureView_headerIcon
 * @attr ref com.cnh.android.widget.R.styleable#ProgressiveDisclosureView_expanded
 * @attr ref com.cnh.android.widget.R.styleable#ProgressiveDisclosureView_collapseRotation
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
		private final boolean mExpanding;

        public PanelAnimation(boolean expanding, int startHeight, int endHeight) {
			mExpanding = expanding;
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
                public void onAnimationStart(Animation animation) {
                    if(isCollapsing()) {
                        ViewHelper.setAlpha(mContents, 0f);
                    } else {
						setExpanded(true);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(isCollapsing()) {
						setExpanded(false);
                        mFrame.setVisibility(View.GONE);
                    } else {
                        ViewHelper.setAlpha(mContents, 1f);
                    }
                }
            });
        }   

        @Override
        protected void applyTransformation(float interpolatedTime,  Transformation t) {
            ViewGroup.LayoutParams lp = mFrame.getLayoutParams();
            lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
            mFrame.setLayoutParams(lp);
            AnimatorProxy.wrap(mCollapseButton).setRotation(mStartRotation + mDeltaRotation*interpolatedTime);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
        
        private boolean isExpanding() {
        	return mExpanding;
        }
        
        private boolean isCollapsing() {
        	return !isExpanding();
        }
    }
    
    private boolean mExpanded=true;
    protected ExpandableLinearLayout mTitleView;
    private FrameLayout mFrame;
    private LinearLayout mContents;
    private Drawable mIcon;
    private View mCollapseButton;
    private int mContentGravity;
    private int mContentHeight;
    private String mTitle;
    private int mCollapseRotation=DEFAULT_COLLAPSE_ROTATION;

	private static final int[] STATE_EXPANDED = { R.attr.state_expanded };

	public CollapsiblePanelView(Context context) {
		this(context, null);
	}

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
		setOrientation(VERTICAL);

		mContents = new LinearLayout(context);
		mContents.setOrientation(VERTICAL);
		mContents.setGravity(mContentGravity);
		mContents.setDuplicateParentStateEnabled(false);

		mFrame = new FrameLayout(context);
		mFrame.setBackgroundResource(R.drawable.panel_body_shape);
		mFrame.addView(mContents, MATCH_PARENT, WRAP_CONTENT);

		super.addView(getTitleView(), MATCH_PARENT, WRAP_CONTENT);
		super.addView(mFrame, MATCH_PARENT, WRAP_CONTENT);

		if(!mExpanded)
			collapse(false);
//		else
//			expand(false);

		if(Utils.isVersion(Build.VERSION_CODES.HONEYCOMB))
			setLayerType(LAYER_TYPE_HARDWARE, null);
	}

	/**
     * Override to create custom Panel header.  Make sure to call {@link CollapsiblePanelView#setCollapseButton}.
     * @return  the initialized panel header
     */
    public View getTitleView() {
        if(mTitleView==null) {
            mTitleView = (ExpandableLinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.panel_header, this, false);
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
    
    public List<View> getContentViews() {
        List<View> views = new ArrayList<View>(mContents.getChildCount());
        for(int i=0; i<mContents.getChildCount(); i++)
            views.add(mContents.getChildAt(i));
        return views;
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
            collapse(true);
        else
            expand(true);
    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mFrame.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
		mContentHeight = mFrame.getMeasuredHeight();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * Expand the panel
	 * @param animate whether to animate the change
	 */
	public void expand(boolean animate) {
		mFrame.setVisibility(VISIBLE);

		if(animate && !mExpanded) {
			mFrame.startAnimation(new PanelAnimation(true, 0, mContentHeight));
		} else {
			ViewGroup.LayoutParams lp = mFrame.getLayoutParams();
			lp.height = mContentHeight;
			mFrame.setLayoutParams(lp);
			ViewHelper.setRotation(mCollapseButton, 0);
			setExpanded(true);
		}
	}

	/**
	 * Collapse the panel
	 * @param animate whether to animate the change
	 */
	public void collapse(boolean animate) {
		if(mExpanded && mContents.getHeight()<mContentHeight)
			mContentHeight = mContents.getHeight();

		if(animate && mExpanded) {
			mFrame.startAnimation(new PanelAnimation(false, mContentHeight, 0));
		} else {
			mFrame.setVisibility(GONE);
			ViewGroup.LayoutParams lp = mFrame.getLayoutParams();
			lp.height = 0;
			mFrame.setLayoutParams(lp);
			ViewHelper.setRotation(mCollapseButton, mCollapseRotation);
			setExpanded(false);
		}
	}

	protected void setExpanded(boolean expanded) {
		boolean changed = mExpanded!=expanded;
		mExpanded=mTitleView.expanded=expanded;
		if(changed) {
			refreshDrawableState();
			mTitleView.refreshDrawableState();
			sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
		}
	}

    /**
     * @return  <em>true</em> if panel is expanded, <em>false</em> otherwise
     */
    public boolean isExpanded() {
    	return mExpanded;
    }

	/**
	 * @param rotation      the rotation of the collapse button when the panel is collapsing
	 *
	 * @attr ref R.styleable#ProgressiveDisclosureView_collapseRotation
	 */
    public void setCollapseRotation(int rotation) {
    	mCollapseRotation=rotation;
    }

	@Override
	protected Parcelable onSaveInstanceState() {
		SavedState ss = new SavedState(super.onSaveInstanceState());
		ss.expanded = mExpanded;
		return ss;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState)state;
		super.onRestoreInstanceState(ss.getSuperState());
		mExpanded = ss.expanded;
		if(!mExpanded)
			collapse(false);
		else
			expand(false);
	}

	/**
	 * RelativeLayout with state_expanded
	 */
	public static class ExpandableLinearLayout extends LinearLayout {

		boolean expanded=true;

		public ExpandableLinearLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected int[] onCreateDrawableState(int extraSpace) {
			int[] states =  super.onCreateDrawableState(extraSpace+1);
			if(expanded) {
				mergeDrawableStates(states, STATE_EXPANDED);
			}
			return states;
		}
	}

	public static class SavedState extends BaseSavedState {

		public boolean expanded;

		public SavedState(Parcel source) {
			super(source);
			expanded = source.readInt()==1;
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(expanded ? 1 : 0);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

			@Override
			public SavedState createFromParcel(Parcel source) {
				return new SavedState(source);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[0];
			}
		};
	}
}
