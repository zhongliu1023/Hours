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

    public static final String KEY_bookId = "bookId";
    public static final String KEY_bookSerial = "bookSerial";
    public static final String KEY_coverUrl = "coverUrl";
    public static final String KEY_bookName = "bookName";
    public static final String KEY_bookNameUrl = "bookNameUrl";
    public static final String KEY_publishDate = "publishDate";
    public static final String KEY_author = "author";
    public static final String KEY_averageProgress = "averageProgress";
    public static final String KEY_averageTime = "averageTime";
    public static final String KEY_allAverageTime = "allAverageTime";
    public static final String KEY_deadline = "deadline";
    public static final String KEY_demandTime = "demandTime";
    public static final String KEY_favouriteCount = "favouriteCount";
    public static final String KEY_downloadedCount = "downloadedCount";
    public static final String KEY_attentionCount = "attentionCount";
    public static final String KEY_readingCount = "readingCount";
    public static final String KEY_isReadCount = "isReadCount";
    public static final String KEY_isDeleted = "isDeleted";
    public static final String KEY_summary = "summary";
    public static final String KEY_category = "category";
    public static final String KEY_publishingHouse = "publishingHouse";
    public static final String KEY_pageCount = "pageCount";
    public static final String KEY_isbn = "isbn";
    public static final String KEY_edition = "edition";
    public static final String KEY_fileName = "fileName";

    public static final String KEY_bookLocalUrl = "bookLocalUrl";
    public static final String KEY_bookImageLocalUrl = "bookImageLocalUrl";
    public static final String KEY_libraryPosition = "libraryPosition";

    // for bookStateTable.

    public static final String KEY_pages = "pages";
    public static final String KEY_time = "time";
    public static final String KEY_progress = "progress";
    public static final String KEY_lastRead = "lastRead";
    public static final String KEY_isRead = "isRead";
    public static final String KEY_isAttention = "isAttention";
    public static final String KEY_collection = "collection";
    public static final String KEY_notes = "notes";
    public static final String KEY_bookmarks = "bookmarks";

    public static final String CREATE_TABLE_BOOKS = "CREATE TABLE " + bookTable
            + "(" + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_bookId + " TEXT,"
            + KEY_bookSerial + " TEXT,"
            + KEY_coverUrl + " TEXT,"
            + KEY_bookName + " TEXT,"
            + KEY_bookNameUrl + " TEXT,"
            + KEY_publishDate + " TEXT,"
            + KEY_author + " TEXT,"
            + KEY_averageProgress + " TEXT,"
            + KEY_averageTime + " TEXT,"
            + KEY_allAverageTime + " TEXT,"
            + KEY_deadline + " TEXT,"
            + KEY_demandTime + " TEXT,"
            + KEY_favouriteCount + " TEXT,"
            + KEY_downloadedCount + " TEXT,"
            + KEY_attentionCount + " TEXT,"
            + KEY_readingCount + " TEXT,"
            + KEY_isReadCount + " TEXT,"
            + KEY_isDeleted + " TEXT,"
            + KEY_summary + " TEXT,"
            + KEY_category + " TEXT,"
            + KEY_publishingHouse + " TEXT,"
            + KEY_pageCount + " TEXT,"
            + KEY_isbn + " TEXT,"
            + KEY_edition + " TEXT,"
            + KEY_fileName + " TEXT,"
            + KEY_bookLocalUrl + " TEXT,"
            + KEY_bookImageLocalUrl + " TEXT,"
            + KEY_libraryPosition + " TEXT"
            + ")";

    public static final String CREATE_TABLE_STATE_BOOKS = "CREATE TABLE " + bookStateTable
            + "(" + KEY_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_bookId + " TEXT,"
            + KEY_pages + " TEXT,"
            + KEY_time + " TEXT,"
            + KEY_progress + " TEXT,"
            + KEY_lastRead + " TEXT,"
            + KEY_isRead + " TEXT,"
            + KEY_isAttention + " TEXT,"
            + KEY_collection + " TEXT,"
            + KEY_notes + " TEXT,"
            + KEY_bookmarks + " TEXT"
            + ")";

    public static final String[] columns = new String[] {
            DatabaseManager.KEY_id,
            DatabaseManager.KEY_bookId,
            DatabaseManager.KEY_pages,
            DatabaseManager.KEY_time,
            DatabaseManager.KEY_progress,
            DatabaseManager.KEY_lastRead,
            DatabaseManager.KEY_isRead,
            DatabaseManager.KEY_isAttention,
            DatabaseManager.KEY_collection,
            DatabaseManager.KEY_notes,
            DatabaseManager.KEY_bookmarks
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
