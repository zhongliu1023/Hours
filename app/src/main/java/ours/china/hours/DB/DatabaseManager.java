package ours.china.hours.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final int DB_version = 1;
    private static final String DB_name = "Hour_DB";

    int a = 0;

    public static final String tableName = "bookTable";
    public static final String KEY_id = "id";
    public static final String KEY_bookName = "bookName";
    public static final String KEY_bookAuthor = "bookAuthor";
    public static final String KEY_bookLocalUrl = "bookLocalUrl";
    public static final String KEY_bookImageLocalUrl = "bookImageLocalUrl";

    public static final String[] columns = new String[] {
            DatabaseManager.KEY_id, DatabaseManager.KEY_bookName, DatabaseManager.KEY_bookAuthor, DatabaseManager.KEY_bookLocalUrl, DatabaseManager.KEY_bookImageLocalUrl
    };

    public DatabaseManager(@Nullable Context context) {
        super(context, DB_name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + tableName
                + "(" + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_bookName + " TEXT,"
                + KEY_bookAuthor + " TEXT,"
                + KEY_bookLocalUrl + " TEXT,"
                + KEY_bookImageLocalUrl + " TEXT"
                + ");";

        Log.d("debug", createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        a++;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("db", "update");
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
