package com.kedzie.vbox.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Tuple<T extends Parcelable, S extends Parcelable> implements Parcelable {
    private static final String TAG = "Tuple";
    final static ClassLoader LOADER = Tuple.class.getClassLoader();
    
    public T first;
    public S second;

    public Tuple(T first, S second) {
        this.first=first;
        this.second=second;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(first, flags);
        dest.writeParcelable(second, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    
    @SuppressWarnings("rawtypes")
    public boolean equals(Object other) {
        if(!other.getClass().equals(this.getClass())) 
            return false;
        Tuple that = (Tuple)other;
        return this.first.equals(that.first) && this.second.equals(that.second);
    }
    
    public String toString() {
        return String.format("{%1$s, %2$s}", first, second);
    }
    
    @SuppressWarnings("rawtypes")
    public static final Parcelable.ClassLoaderCreator<Tuple> CREATOR  = new Parcelable.ClassLoaderCreator<Tuple>() {
        
        @SuppressWarnings("unchecked")
        @Override
        public Tuple createFromParcel(Parcel source, ClassLoader loader) {
            return new Tuple(source.readParcelable(loader), source.readParcelable(loader));
        }
        
        @Override
        public Tuple[] newArray(int size) {
            return new Tuple[size];
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public Tuple createFromParcel(Parcel source) {
            Log.i(TAG, "createFromParcel(Parcel source)");
            return new Tuple(source.readParcelable(LOADER), source.readParcelable(LOADER));
        }
    };
}
