package com.kedzie.vbox.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class TabFragmentInfo {
	public final String _name;
	public final Class<? extends Fragment> clazz;
    public final Bundle args;

    TabFragmentInfo(String name, Class<? extends Fragment> _class, Bundle _args) {
    	_name=name;
        clazz = _class;
        args = _args;
    }
    
    public boolean equals(Object that) {
    	return  (that instanceof TabFragmentInfo)  && ((TabFragmentInfo)that)._name.equals(_name);
    }
}
