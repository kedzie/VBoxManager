package com.kedzie.vbox.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;

import java.util.Objects;


/**
 * Description of a fragment.  Also has methods to instantiate and track a reference.
 */
public class FragmentElement implements Parcelable {
    public final static String BUNDLE = "info";
    
    public static final Parcelable.Creator<FragmentElement> CREATOR = new Parcelable.Creator<FragmentElement>() {
        public FragmentElement createFromParcel(Parcel in) {
            return new FragmentElement(in.readString(), in.readInt(), (Class<?>)in.readSerializable(), in.readBundle());
        }
        public FragmentElement[] newArray(int size) {
            return new FragmentElement[size];
        }
    };

    public final String name;
    public final int icon;
    public Class<?>  clazz;
    public final Bundle args;
    public Fragment fragment;

    public FragmentElement(String name, Class<?> clazz, Bundle args) {
        this(name, -1, clazz, args);
    }
    
    public FragmentElement(String name, int icon, Class<?> clazz, Bundle args) {
        this.name=name;
        this.icon=icon;
        this.clazz = clazz;
        this.args = args;
    }
    
    /**
     * Instantiate the {@link Fragment} and keep a reference
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
        return Objects.equals(this.name, that.name) && Objects.equals(this.clazz,that.clazz);
    }
    
    public int hashCode() {
        return Objects.hash(name, clazz);
    }
}
