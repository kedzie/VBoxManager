package com.kedzie.vbox.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.common.base.Objects;

/**
 * Definitions of one-or-more {@link Fragment}s and optionally the style used to display multiple {@link Fragment}s.  
 * i.e. {@link ActionBar} tabs or Horizontal {@link LinearLayout}
 * @author Marek Kedzierski
 */
public class FragmentInfo implements Parcelable {
    private static final String TAG = "FragmentInfo";
    
    /**
     * Definition of single {@link Fragment} which can be instantiated.
     */
    public static class FragmentElement implements Parcelable {
        public static String BUNDLE = "info";

        public final String name;
        public final int icon;
        public final Class<?>  clazz;
        public final Bundle args;
        public Fragment fragment;

        public FragmentElement(String name, Class<?> _class, Bundle _args) {
            this(name, 0, _class, _args);
        }
        
        public FragmentElement(String name, int icon, Class<?> _class, Bundle _args) {
            this.name=name;
            this.icon=icon;
            clazz = _class;
            args = _args;
        }
        
        /**
         * Instantiate the {@link Fragment}
         * @param context Android {@ Context}
         * @return the instantiated {@link Fragment}
         */
        public Fragment instantiate(Context context) {
            fragment = Fragment.instantiate(context, clazz.getName(), args);
            return fragment;
        }
        
        public String getFragmentClass() {
            return clazz.getName();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeInt(icon);
            dest.writeSerializable(clazz);
            dest.writeBundle(args);
        }
        
        public boolean equals(Object obj) {
            if (obj==this) return true;
            if(obj==null) return false;
            if(!getClass().equals(obj.getClass())) return false;
            final FragmentElement that = (FragmentElement)obj;
            return Objects.equal(this.name, that.name) && this.clazz.equals(that.clazz);
        }
        
        public int hashCode() {
            return Objects.hashCode(name, clazz);
        }
        
        public static final Parcelable.Creator<FragmentElement> CREATOR = new Parcelable.Creator<FragmentElement>() {
            public FragmentElement createFromParcel(Parcel in) {
                return new FragmentElement(in.readString(), in.readInt(), (Class<?>)in.readSerializable(), in.readBundle());
            }
            public FragmentElement[] newArray(int size) {
                return new FragmentElement[size];
            }
        };
    }

    public static final int DISPLAY_STYLE_LINEAR_LAYOUT = 0;
    public static final int DISPLAY_STYLE_TABS = 1;

    private final String name;
    private final int icon;
    private FragmentElement[] elements;
    private final int displayStyle;
    
    public FragmentInfo(String name, FragmentElement...elements) {
        this(name, -1, 0, elements);
    }
    
    public FragmentInfo(String name, int icon, FragmentElement...elements) {
        this(name, -1, icon, elements);
    }
    
    public FragmentInfo(String name, int style, int icon, FragmentElement...elements) {
        this.name=name;
        this.displayStyle=style;
        this.icon=icon;
        this.elements=elements;
    }
    
    public int getDisplayStyle() {
        return displayStyle;
    }

    public FragmentElement[] getElements() {
        return elements;
    }

    public int getIcon() {
        return icon;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Create the Fragment layout in the targetContainer.  Depending the fragment elements this will:
     * <ul>
     * <li>Instantiate a single Fragment and add it to the <code>targetContainer</code></li>
     * <li>Use {@link TabSupport} to create Action Bar Tabs for each element</li>
     * <li>Create a LinearLayout and horizontally add each Fragment</li>
     * </ul>
     * @param activity          the activity
     * @param targetContainer       the target view for the fragments
     */
    public void applyFragments(SherlockFragmentActivity activity, int targetContainer) {
        if(elements.length==1) {
            Log.i(TAG, "Adding single Fragment");
            FragmentTransaction tx = activity.getSupportFragmentManager().beginTransaction();
            Utils.addOrAttachFragment(activity, activity.getSupportFragmentManager(), tx, targetContainer, elements[0]);
            tx.commit();
        } else if(displayStyle==DISPLAY_STYLE_TABS) {
            TabSupport support = new TabSupportFragment(activity, targetContainer);
            for(FragmentElement element : elements) 
                support.addTab(element.name, element.clazz, element.args);
        } else if(displayStyle==DISPLAY_STYLE_LINEAR_LAYOUT) {
            ViewGroup target = (ViewGroup)activity.findViewById(targetContainer);
            LinearLayout layout = new LinearLayout(activity);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            target.addView(layout);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.weight=1;
            for(int i=0; i<elements.length;i++) {
                FrameLayout frame = new FrameLayout(activity);
                frame.setId(i);
                layout.addView(frame, params);
                FragmentTransaction tx = activity.getSupportFragmentManager().beginTransaction();
                Utils.addOrAttachFragment(activity, activity.getSupportFragmentManager(), tx, i, elements[i]);
                tx.commit();
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(displayStyle);
        dest.writeInt(icon);
        dest.writeParcelableArray(elements, flags);
    }
    
    public static final Parcelable.Creator<FragmentInfo> CREATOR = new Parcelable.Creator<FragmentInfo>() {
        public FragmentInfo createFromParcel(Parcel in) {
            return new FragmentInfo(in.readString(), in.readInt(), in.readInt(), (FragmentElement[])in.readParcelableArray(FragmentInfo.class.getClassLoader()));
        }
        public FragmentInfo[] newArray(int size) {
            return new FragmentInfo[size];
        }
    };
}
