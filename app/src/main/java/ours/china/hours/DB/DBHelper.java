package ours.china.hours.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ours.china.hours.Model.Book;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "hour.db";
    public static final String TABLE_NAME = "book_state_table";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "BOOK_ID";
    public static final String COL_3 = "PAGES";
    public static final String COL_4 = "READ_TIME";
    public static final String COL_5 = "LAST_TIME";

    SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID integer primary key autoincrement, BOOK_ID text, PAGES text, READ_TIME text, LAST_TIME integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Book> getAllData() {
        ArrayList<Book> results = new ArrayList<Book>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Book row = new Book();

                row.bookId = cursor.getString(1);
                row.bookStatus.pages = cursor.getString(2);
                row.bookStatus.time = cursor.getString(3);
                row.bookStatus.lastRead = cursor.getString(4);

                results.add(row);
            } while (cursor.moveToNext());
        }

        return results;
    }

    public ArrayList<String> getAllData_String() {
        ArrayList<String> results = new ArrayList<String>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                String tmp = cursor.getString(1);
                tmp += "  " + cursor.getString(2);
                tmp += "  " + cursor.getString(3);
                tmp += "  " + cursor.getString(4);

                results.add(tmp);
            } while (cursor.moveToNext());
        }

        return results;
    }

    public boolean insertData(String bookID, String pages, String readTime, String lastTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, bookID);
        contentValues.put(COL_3, pages);
        contentValues.put(COL_4, readTime);
        contentValues.put(COL_5, lastTime);
        long row = db.insert(TABLE_NAME, null, contentValues);
        if (row == -1)
            return false;
        else
            return true;
    }

    public boolean updateData(Book book) {
        ContentValues cv = new ContentValues();
        cv.put(COL_2, book.bookId);
        cv.put(COL_3, book.bookStatus.pages);
        cv.put(COL_4, book.bookStatus.time);
        cv.put(COL_5, book.bookStatus.lastRead);
        int cnt = db.update(TABLE_NAME, cv, "BOOK_ID = " + book.bookId, null);
        if (cnt > 0)
            return true;
        else
            return false;
    }

    public boolean deleteRow(String id) {
        int cnt = db.delete(TABLE_NAME, "id=?", new String[]{id});

        return true;
    }
}
