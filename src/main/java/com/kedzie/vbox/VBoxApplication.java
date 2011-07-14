package com.kedzie.vbox;


import android.app.Application;

public class VBoxApplication extends Application {

	private DictionaryOpenHelper _db;
	
	@Override
	public void onCreate() {
		super.onCreate();
		_db = new DictionaryOpenHelper(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	public DictionaryOpenHelper getDB() { return _db;	}
}
