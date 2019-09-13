package ours.china.hours.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final int DB_version = 1;
    private static final String DB_name = "Hour.db";

    public static final String bookTable = "bookTable";
    public static final String bookStateTable = "bookStateTable";

    // common column
    public static final String KEY_id = "id";
    public static final String KEY_bookid = "bookID";

    // for bookTable
    public static final String KEY_bookLocalUrl = "bookLocalUrl";
    public static final String KEY_bookImageLocalUrl = "bookImageLocalUrl";

    // for bookStateTable.
    public static final String KEY_bookPages = "bookPages";
    public static final String KEY_bookReadTime = "bookReadTime";
    public static final String KEY_bookLastTime = "bookLastTime";

    private static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + bookTable
            + "(" + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_bookid + " TEXT,"
            + KEY_bookLocalUrl + " TEXT,"
            + KEY_bookImageLocalUrl + " TEXT"
            + ")";

    private static final String CREATE_TABLE_STATE_BOOKS = "CREATE TABLE " + bookStateTable
            + "(" + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_bookid + " TEXT,"
            + KEY_bookPages + " TEXT,"
            + KEY_bookReadTime + " TEXT,"
            + KEY_bookLastTime + " TEXT"
            + ")";

    public static final String[] columns = new String[] {
            DatabaseManager.KEY_id, DatabaseManager.KEY_bookid, DatabaseManager.KEY_bookPages, DatabaseManager.KEY_bookReadTime, DatabaseManager.KEY_bookLastTime
    };

    public DatabaseManager(@Nullable Context context) {
        super(context, DB_name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKS);
        db.execSQL(CREATE_TABLE_STATE_BOOKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("db", "update");
        db.execSQL("DROP TABLE IF EXISTS '" + bookTable + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + bookStateTable + "'");
        onCreate(db);
    }

}
