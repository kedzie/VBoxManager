package com.kedzie.vbox.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

/**
 * Definition of a {@link Fragment} which can be instantiated.
 * @author Marek Kedzierski
 */
public class TabFragmentInfo implements Parcelable {
    public static String BUNDLE = "info";

    public final String name;
    public final int icon;
    public final Class<?>  clazz;
    public final Bundle args;
    public Fragment fragment;

    public TabFragmentInfo(String name, Class<?> _class, Bundle _args) {
        this(name, 0, _class, _args);
    }
    
    public TabFragmentInfo(String name, int icon, Class<?> _class, Bundle _args) {
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
    
    public boolean equals(Object that) {
        return  (that instanceof TabFragmentInfo)  && ((TabFragmentInfo)that).name.equals(name);
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
    
    public static final Parcelable.Creator<TabFragmentInfo> CREATOR = new Parcelable.Creator<TabFragmentInfo>() {
        public TabFragmentInfo createFromParcel(Parcel in) {
            return new TabFragmentInfo(in.readString(), in.readInt(), (Class<?>)in.readSerializable(), in.readBundle());
        }
        public TabFragmentInfo[] newArray(int size) {
            return new TabFragmentInfo[size];
        }
    };
}
