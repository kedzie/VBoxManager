package com.kedzie.vbox.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.common.base.Objects;

public class FragmentElement implements Parcelable {
    public static String BUNDLE = "info";

    public final String name;
    public final int icon;
    public final View view;
    public final Class<?>  clazz;
    public final Bundle args;
    public Fragment fragment;

    public FragmentElement(String name, Class<?> clazz, Bundle args) {
        this(name, null, clazz, args);
    }
    
    public FragmentElement(String name, int icon, Class<?> clazz, Bundle args) {
        this.name=name;
        this.icon=icon;
        view=null;
        this.clazz = clazz;
        this.args = args;
    }
    
    public FragmentElement(String name, View view, Class<?> clazz, Bundle args) {
        this.name=name;
        icon=-1;
        this.view=view;
        this.clazz = clazz;
        this.args = args;
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
        return Objects.equal(this.name, that.name) && Objects.equal(this.clazz,that.clazz);
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
