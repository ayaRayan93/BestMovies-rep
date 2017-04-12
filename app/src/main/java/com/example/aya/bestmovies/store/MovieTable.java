package com.example.aya.bestmovies.store;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by AyaAli on 12/04/2017.
 */

public class MovieTable {
    // Contacts table name
    public static final String TABLE_MOVIES ="movie" ;
    // Movies Table Columns names
    public static final String KEY_ID ="ID" ;
    public static final String KEY_TITLE ="original_title" ;
    public static final String KEY_POSTER="poster" ;
  //  public static final String KEY_OVERVIEW="overview" ;
 //   public static final String KEY_VOTE_AVERAGE="vote_average" ;
 //   public static final String KEY_RELEASE_DATE="release_date" ;
    public static final String KEY_BACKDROP_PATH="poster" ;
    public static String CREATE_Database_TABLE = "CREATE TABLE " + TABLE_MOVIES + "("
            + KEY_ID + " TEXT PRIMARY KEY,"
            + KEY_TITLE + " TEXT,"
            +KEY_POSTER+"TEXT,"
           // +KEY_OVERVIEW+"TEXT,"
        //    +KEY_VOTE_AVERAGE+"TEXT,"
          //  +KEY_RELEASE_DATE+"TEXT,"
            + KEY_BACKDROP_PATH + " TEXT" + ")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_Database_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {

        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(database);
    }
}
