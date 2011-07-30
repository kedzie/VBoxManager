package com.kedzie.vbox.server;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ServerDB extends SQLiteOpenHelper {
	private static final String TAG = ServerDB.class.getName();
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME="vbox.db";
	private static final String TABLE_NAME = "servers";
	private static final String ROW_ID = "id";
    private static final String ROW_HOST = "host";
    private static final String ROW_PORT = "port";
    private static final String ROW_USERNAME = "username";
    private static final String ROW_PASSWORD = "password";
    private static final String TABLE_CREATE ="CREATE TABLE " + TABLE_NAME + " (" + ROW_ID + " INTEGER PRIMARY KEY," + ROW_HOST + " TEXT, " + ROW_PORT + " INTEGER," + ROW_USERNAME + " TEXT," + ROW_PASSWORD + " TEXT);";

    public ServerDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    
    public void insertOrUpdate(Server s) {
    	ContentValues c = new ContentValues();
    	c.put(ROW_HOST, s.getHost());
    	c.put(ROW_PORT, s.getPort());
    	c.put(ROW_USERNAME, s.getUsername());
    	c.put(ROW_PASSWORD, s.getPassword());
    	if(s.getId()==-1) {
			long id = getWritableDatabase().insert(TABLE_NAME, null, c);
			s.setId(id);
			Log.i(TAG, "Inserted with new ID#" + id);
		} else {
			c.put(ROW_ID, s.getId());
			getWritableDatabase().update(TABLE_NAME, c, ROW_ID + " =  ?", new String[] {c.get(ROW_ID).toString()} );
			Log.i(TAG, "Updated ID#"+s.getId());
		}
    }
    
    public void delete(long id) {
    	getWritableDatabase().delete(TABLE_NAME, ROW_ID + " =  ?", new String[] {Long.valueOf(id).toString()} );
    	Log.i(TAG, "Deleted #"+ id);
    }
    
    public List<Server> getServers() {
    	Cursor c = getReadableDatabase().query(TABLE_NAME, new String[] { ROW_ID, ROW_HOST, ROW_PORT, ROW_USERNAME, ROW_PASSWORD }, null, null, null, null, null);
    	List<Server> ret = new ArrayList<Server>();
    	c.moveToFirst();
    	for(int i=0; i<c.getCount(); i++) {
    		Server s = new Server(c.getLong(c.getColumnIndex(ROW_ID)), 
    									c.getString(c.getColumnIndex(ROW_HOST)), 
    									c.getInt(c.getColumnIndex(ROW_PORT)), 
    									c.getString(c.getColumnIndex(ROW_USERNAME)), 
    									c.getString(c.getColumnIndex(ROW_PASSWORD)));
    		ret.add(s);
    		c.moveToNext();
    	}
    	return ret;
    }
}