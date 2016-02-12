package com.goldenbros.buzzbee.chats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by jinge on 7/29/15.
 */
public class DatabaseLocalConnector implements chatdb_final {
    private SQLiteDatabase database; // database object
    private DatabaseOpenHelper databaseOpenHelper; // database helper

    public DatabaseLocalConnector(Context context)
    {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void open() throws SQLException
    {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close()
    {
        if (database != null)
            database.close();
    }

    public void insertPer(String send_id, String reci_id, String message){
        ContentValues data= new ContentValues();
        data.put(MESSAGE_SEND_ID, send_id);
        data.put(MESSAGE_RECEIVER_ID, reci_id);
        data.put(MESSAGE_CONTENT , message);

        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        database.insert(TABLE_MESSAGE, null, data);
        close();
    }
    public void insertGroup(String send_id, String event_id, String message){

        ContentValues data= new ContentValues();
        data.put(MESSAGE_SEND_ID, send_id);
        data.put(MESSAGE_EVENT_ID, event_id);
        data.put(MESSAGE_CONTENT, message);
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        database.insert(TABLE_MESSAGE, null, data);
        close();
    }


    public void insertChats(String receive_id, String event_id, String name){

        ContentValues data= new ContentValues();
        if(event_id!=null){
            data.put(CHATS_EVENT_ID, event_id);
        }
        if(receive_id!=null)
        data.put(CHATS_RECEIVER_ID, receive_id);
        if(name!=null)
        data.put(CHATS_NAME, name);
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        database.insert(TABLE_CHATS, null, data);
        close();
    }
    public void deleteTable(){
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        databaseOpenHelper.onUpgrade(database, 1, 2);
        close();
    }



    public Cursor getAll(){
        return database.query(TABLE_MESSAGE, null, null, null, null, null, null);
    }
    public Cursor getOne( String send_id, String reci_id) {
        return database.query(TABLE_MESSAGE, null, "( send_id= '" + send_id + "' and reci_id= '" + reci_id
                + "') or ( send_id= '" + reci_id + "' and reci_id= '" + send_id + "')", null, null, null, null);

    }
    public Cursor getOne(String event_id){
        return database.query(TABLE_MESSAGE, null, "event_id= "+event_id, null, null, null,null);
    }


    public Cursor getAllChats(){
        return database.query(TABLE_CHATS, null, null, null, null, null, null);
    }
    public Cursor getOneChat(int id){
        Log.i("cursor___id____", id+"");
        return database.query(TABLE_CHATS, null, "_id= "+id, null, null, null, null);
    }



    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        public DatabaseOpenHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // query to create a new table named chatting
            String createQuery = "CREATE TABLE IF NOT EXISTS '" +TABLE_MESSAGE+
                    "'(_id integer primary key autoincrement, '"+MESSAGE_SEND_ID+"' TEXT, '"+MESSAGE_RECEIVER_ID+"' TEXT , '"
                    +MESSAGE_EVENT_ID+"' TEXT ,'"+MESSAGE_CONTENT+"' TEXT );";
            Log.i("sql__mess", createQuery);
            db.execSQL(createQuery);

            createQuery = "CREATE TABLE IF NOT EXISTS '"+ TABLE_CHATS+"'(_id integer primary key autoincrement, '"
                    +CHATS_RECEIVER_ID+"' TEXT, '"+ CHATS_EVENT_ID+ "' TEXT,  '"+ CHATS_NAME+"' TEXT);";
            Log.i("sql___chat", createQuery);
            db.execSQL(createQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            String sql = "drop TABLE IF EXISTS '"+ TABLE_CHATS +"';";
            db.execSQL(sql);
            String sqlit = "drop TABLE IF EXISTS '"+TABLE_MESSAGE+ "' ;";
            db.execSQL(sqlit);
            onCreate(db);
        }
    }
}
