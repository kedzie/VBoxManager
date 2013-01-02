package com.kedzie.vbox.app;

import android.os.Parcelable;

public class Tuple<T extends Parcelable, S extends Parcelable> {
    final static ClassLoader LOADER = Tuple.class.getClassLoader();
    
    public T first;
    public S second;

    public Tuple(T first, S second) {
        this.first=first;
        this.second=second;
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
}
