package com.kedzie.vbox.server;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Table of VirtualBox servers
 * @author Marek Kędzierski
 */
public class ServerSQlite extends SQLiteOpenHelper {
    private static final String TAG = "ServerSQlite";
    
    public ServerSQlite(Context context) { 
        super(context, "vbox.db", null, 3);  
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) { 
        Log.i("ServerSQL", "Creating database schema");
        db.execSQL("CREATE TABLE SERVERS (ID INTEGER PRIMARY KEY, NAME TEXT, HOST TEXT, SSL INTEGER, PORT INTEGER, USERNAME TEXT, PASSWORD TEXT);");    
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "DB upgrade [" + oldVersion + "-->" + newVersion + "], migrating data");
        db.execSQL("ALTER TABLE SERVERS ADD COLUMN SSL INTEGER");
    }
    
    /**
     * Get a server by id
     * @param id    the id of the server
     * @return the server
     */
    public Server get(Long id) {
        Cursor c = getReadableDatabase().query("SERVERS", 
                new String[] { "ID", "NAME", "HOST", "SSL", "PORT", "USERNAME", "PASSWORD" }, 
                "ID=?", new String [] { id.toString() }, null, null, null);
        if(!c.moveToFirst())
            return null;
        return new Server(
                    c.getLong(c.getColumnIndex("ID")),  
                    c.getString(c.getColumnIndex("NAME")), 
                    c.getString(c.getColumnIndex("HOST")),
                    c.getInt(c.getColumnIndex("SSL"))>0,
                    c.getInt(c.getColumnIndex("PORT")),
                    c.getString(c.getColumnIndex("USERNAME")),
                    c.getString(c.getColumnIndex("PASSWORD")));
    }
    
    /**
     * Query all the servers
     * @return All the Servers
     */
    public List<Server> query() {
        Cursor c = getReadableDatabase().query("SERVERS", new String[] { "ID", "NAME", "HOST", "SSL", "PORT", "USERNAME", "PASSWORD" }, null, null, null, null, null);
        List<Server> ret = new ArrayList<Server>();
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            ret.add(new Server(
                    c.getLong(c.getColumnIndex("ID")),  
                    c.getString(c.getColumnIndex("NAME")), 
                    c.getString(c.getColumnIndex("HOST")),
                    c.getInt(c.getColumnIndex("SSL"))>0,
                    c.getInt(c.getColumnIndex("PORT")),
                    c.getString(c.getColumnIndex("USERNAME")),
                    c.getString(c.getColumnIndex("PASSWORD"))));
        return ret;
    }
    
    /**
     * Insert a server
     * @param s the server
     */
    public void insert(Server s) {
        ContentValues c = new ContentValues();
        c.put("NAME", s.getName());
        c.put("HOST", s.getHost());
        c.put("SSL", s.isSSL());
        c.put("PORT", s.getPort());
        c.put("USERNAME", s.getUsername());
        c.put("PASSWORD", s.getPassword());
        s.setId(getWritableDatabase().insert( "SERVERS", null, c));
    }
    
    /**
     * Update a server
     * @param s the server
     */
    public void update(Server s) {
        ContentValues c = new ContentValues();
        c.put("ID", s.getId());
        c.put("NAME", s.getName());
        c.put("HOST", s.getHost());
        c.put("SSL", s.isSSL());
        c.put("PORT", s.getPort());
        c.put("USERNAME", s.getUsername());
        c.put("PASSWORD", s.getPassword());
        getWritableDatabase().update( "SERVERS", c, "ID  =  ?", new String[] { s.getId().toString() } );
    }
    
    /**
     * Delete a server
     * @param id    the id of server
     */
    public void delete(Long id) {
        getWritableDatabase().delete( "SERVERS", "ID =  ?", new String[] { id.toString() } );
    }
}
