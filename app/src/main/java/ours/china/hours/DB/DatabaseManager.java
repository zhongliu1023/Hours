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
    public static final String KEY_id = "id";
    public static final String KEY_bookid = "bookID";
    public static final String KEY_bookLocalUrl = "bookLocalUrl";
    public static final String KEY_bookImageLocalUrl = "bookImageLocalUrl";

    private static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + bookTable
            + "(" + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_bookid + " TEXT,"
            + KEY_bookLocalUrl + " TEXT,"
            + KEY_bookImageLocalUrl + " TEXT"
            + ")";
//
//    public static final String[] columns = new String[] {
//            DatabaseManager.KEY_id, DatabaseManager.KEY_bookName, DatabaseManager.KEY_bookAuthor, DatabaseManager.KEY_bookLocalUrl, DatabaseManager.KEY_bookImageLocalUrl
//    };

    public DatabaseManager(@Nullable Context context) {
        super(context, DB_name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("db", "update");
        db.execSQL("DROP TABLE IF EXISTS '" + bookTable + "'");
        onCreate(db);
    }

}
