package com.kedzie.vbox.app;

import android.support.v4.app.Fragment;
import com.kedzie.vbox.SettingsActivity;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import com.nineoldandroids.view.ViewHelper;

/**
 *  Special {@link FragmentTabHost} which allows side-swiping.  
 *  When placing this in a view hierarchy, after inflating
 * the hierarchy you must call {@link #setup(Context, FragmentManager, int)}
 * to complete the initialization of the tab host.
 */
public class PagerTabHost extends TabHost implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, TabSupport  {

   private FragPagerAdapter mAdapter;
   private ViewPager mRealTabContent;
   private int mContainerId;
   private DummyTabFactory mDummy;

   static class DummyTabFactory implements TabHost.TabContentFactory {
      private final Context mContext;

      public DummyTabFactory(Context context) {
         mContext = context;
      }

      @Override
      public View createTabContent(String tag) {
         View v = new View(mContext);
         v.setMinimumWidth(0);
         v.setMinimumHeight(0);
         return v;
      }
   }

   static class SavedState extends BaseSavedState {

      String curTab;

      SavedState(Parcelable superState) {
         super(superState);
      }

      private SavedState(Parcel in) {
         super(in);
         curTab = in.readString();
      }

      @Override
      public void writeToParcel(Parcel out, int flags) {
         super.writeToParcel(out, flags);
         out.writeString(curTab);
      }

      @Override
      public String toString() {
         return "FragmentTabHost.SavedState{"
               + Integer.toHexString(System.identityHashCode(this))
               + " curTab=" + curTab + "}";
      }

      public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
         public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
         }

         public SavedState[] newArray(int size) {
            return new SavedState[size];
         }
      };
   }

   public PagerTabHost(Context context) {
      // Note that we call through to the version that takes an AttributeSet,
      // because the simple Context construct can result in a broken object!
      super(context, null);
      initFragmentTabHost(context, null);
   }

   public PagerTabHost(Context context, AttributeSet attrs) {
      super(context, attrs);
      initFragmentTabHost(context, attrs);
   }

   private void initFragmentTabHost(Context context, AttributeSet attrs) {
      TypedArray a = context.obtainStyledAttributes(attrs,
            new int[] { android.R.attr.inflatedId }, 0, 0);
      mContainerId = a.getResourceId(0, 0);
      a.recycle();

      super.setOnTabChangedListener(this);
      mDummy = new DummyTabFactory(context);

      mAdapter = new FragPagerAdapter();

      // If owner hasn't made its own view hierarchy, then as a convenience
      // we will construct a standard one here.
      if (findViewById(android.R.id.tabs) == null) {
         LinearLayout ll = new LinearLayout(context);
         ll.setOrientation(LinearLayout.VERTICAL);
         addView(ll, new FrameLayout.LayoutParams(
               ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.MATCH_PARENT));

         TabWidget tw = new TabWidget(context);
         tw.setId(android.R.id.tabs);
         tw.setOrientation(TabWidget.HORIZONTAL);
         ll.addView(tw, new LinearLayout.LayoutParams(
               ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.WRAP_CONTENT, 0));

         FrameLayout fl = new FrameLayout(context);
         fl.setId(android.R.id.tabcontent);
         ll.addView(fl, new LinearLayout.LayoutParams(0, 0, 0));

         mRealTabContent = new ViewPager(context);
         mRealTabContent.setId(mContainerId);
         mRealTabContent.setOffscreenPageLimit(4);
         String transition = Utils.getStringPreference(getContext(), SettingsActivity.PREF_TAB_TRANSITION);
         if(transition.equals("Flip"))
            mRealTabContent.setPageTransformer(false, new FlipPageTransformer());
         else if(transition.equals("Slide"))
            mRealTabContent.setPageTransformer(false, new ZoomOutPageTransformer());
         else if(transition.equals("Accordian"))
            mRealTabContent.setPageTransformer(false, new AccordianPageTransformer());

         ll.addView(mRealTabContent, new LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
      }
   }

   /**
    * @deprecated Don't call the original TabHost setup, you must instead
    * call {@link #setup(Context, FragmentManager)} or
    * {@link #setup(Context, FragmentManager, int)}.
    */
   @Override @Deprecated
   public void setup() {
      throw new IllegalStateException("Must call setup() that takes a Context and FragmentManager");
   }

   public void setup(Context context, FragmentManager manager) {
      super.setup();
      ensureContent();
      mAdapter.setup(context, manager);
      mRealTabContent.setAdapter(mAdapter);
      mRealTabContent.setOnPageChangeListener(this);
   }

   public void setup(Context context, FragmentManager manager, int containerId) {
      super.setup();
      mContainerId = containerId;
      ensureContent();
      mRealTabContent.setId(containerId);
      mAdapter.setup(context, manager);
      mRealTabContent.setAdapter(mAdapter);
      mRealTabContent.setOnPageChangeListener(this);
      // We must have an ID to be able to save/restore our state.  If
      // the owner hasn't set one at this point, we will set it ourself.
      if (getId() == View.NO_ID) {
         setId(android.R.id.tabhost);
      }
   }

   private void ensureContent() {
      if (mRealTabContent == null) {
         mRealTabContent = (ViewPager)findViewById(mContainerId);
         if (mRealTabContent == null) {
            throw new IllegalStateException("No tab content FrameLayout found for id " + mContainerId);
         }
      }
   }

   @Override
   public void onPageSelected(int position) {
      // Unfortunately when TabHost changes the current tab, it kindly
      // also takes care of putting focus on it when not in touch mode.
      // The jerk.
      // This hack tries to prevent this from pulling focus out of our
      // ViewPager.
      TabWidget widget = getTabWidget();
      int oldFocusability = widget.getDescendantFocusability();
      widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
      setCurrentTab(position);
      widget.setDescendantFocusability(oldFocusability);
   }

   @Override
   public void onTabChanged(String tabId) {
      mRealTabContent.setCurrentItem(getCurrentTab());
   }

   @Override
   protected Parcelable onSaveInstanceState() {
       SavedState ss = new SavedState(super.onSaveInstanceState());
      ss.curTab = getCurrentTabTag();
      return ss;
   }

   @Override
   protected void onRestoreInstanceState(Parcelable state) {
      SavedState ss = (SavedState)state;
      super.onRestoreInstanceState(ss.getSuperState());
      setCurrentTabByTag(ss.curTab);
   }

   @Override
   public void addTab(FragmentElement tab) {
      TabSpec spec = newTabSpec(tab.name).setContent(mDummy);
      if(tab.icon!=-1)
         spec.setIndicator(tab.name, getResources().getDrawable(tab.icon));
      else
         spec.setIndicator(tab.name);
      addTab(spec);
      mAdapter.add(tab);
   }

   @Override
   public void removeTab(String name) {
      removeTab(name);
   }

   @Override
   public void removeAllTabs() {
      mAdapter.clear();
      clearAllTabs();
   }

   /**
    * Get the {@link Fragment} belonging to the current tab
    * @return	the {@link Fragment}
    */
   public Fragment getCurrentFragment() {
      return mAdapter.getCurrentTab();
   }

   @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
   @Override public void onPageScrollStateChanged(int state) {}
}
