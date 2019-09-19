package ours.china.hours.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Model.Book;
import ours.china.hours.Model.LocalBook;

public class DBController {

    private DatabaseManager dbManager;
    private SQLiteDatabase database;

    public DBController(Context context) {
        this.dbManager = new DatabaseManager(context);
    }

    public void insertData(Book data) {
        database = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseManager.KEY_bookid, data.getBookID());
        values.put(DatabaseManager.KEY_bookName, data.getBookName());
        values.put(DatabaseManager.KEY_bookLocalUrl, data.getBookLocalUrl());
        values.put(DatabaseManager.KEY_bookImageLocalUrl, data.getBookImageLocalUrl());
        values.put(DatabaseManager.KEY_bookSpecifiedTime, data.getSpecifiedTime());
        values.put(DatabaseManager.KEY_bookTotalPage, data.getTotalPage());
        values.put(DatabaseManager.KEY_bookLibraryPosition, data.getLibraryPosition());


        database.insert(DatabaseManager.bookTable, null, values);
    }

    public void insertBookStateData(Book data) {
        database = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseManager.KEY_bookid, data.getBookID());
        values.put(DatabaseManager.KEY_bookPages, data.getPagesArray());
        values.put(DatabaseManager.KEY_bookReadTime, data.getReadTime());
        values.put(DatabaseManager.KEY_bookLastTime, data.getLastTime());

        database.insert(DatabaseManager.bookStateTable, null, values);
    }

    public void updateBookStateData(Book data) {
        ContentValues updateValues = new ContentValues();

        updateValues.put(DatabaseManager.KEY_bookPages, data.getPagesArray());
        updateValues.put(DatabaseManager.KEY_bookReadTime, data.getReadTime());
        updateValues.put(DatabaseManager.KEY_bookLastTime, data.getLastTime());

        String where = "bookID = ?";
        database = dbManager.getReadableDatabase();

        database.update(DatabaseManager.bookStateTable, updateValues, where, new String[]{data.getBookID()});
        database.close();
    }

    public void updateBookPageNumbersState(Book data) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseManager.KEY_bookPages, data.getPagesArray());

        String where = "bookID = ?";
        database = dbManager.getReadableDatabase();

        database.update(DatabaseManager.bookStateTable, updateValues, where, new String[]{data.getBookID()});
        database.close();
    }

    public void updateBookReadTimeState(Book data) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseManager.KEY_bookReadTime, data.getReadTime());

        String where = "bookID = ?";
        database = dbManager.getReadableDatabase();

        database.update(DatabaseManager.bookStateTable, updateValues, where, new String[]{data.getBookID()});
        database.close();
    }

    public void updateBookLastTimeState(Book data) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseManager.KEY_bookLastTime, data.getLastTime());

        String where = "bookID = ?";
        database = dbManager.getReadableDatabase();

        database.update(DatabaseManager.bookStateTable, updateValues, where, new String[]{data.getBookID()});
        database.close();
    }

    public void updateBookTotalPage(Book data) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseManager.KEY_bookTotalPage, data.getTotalPage());
        database = dbManager.getReadableDatabase();
        String[] strArr = new String[]{data.getBookID()};
        database.update(DatabaseManager.bookTable, updateValues, "bookID = ?", strArr);
        database.close();
    }

//
//    public Book getOneData(String bookID) {
//        database = dbManager.getReadableDatabase();
//
//        Cursor cursor = database.rawQuery("select * from " + DatabaseManager.bookTable + " where " + DatabaseManager.KEY_bookid + " = " + bookID, null);
//
//        if(cursor != null){
//            cursor.moveToFirst();
//        } else {
//            return null;
//        }
//
//        Book data = new Book(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
//        return data;
//    }

    public Book getBookStateData(String bookID) {
        database = dbManager.getWritableDatabase();

        Cursor cursor = database.query(DatabaseManager.bookStateTable, DatabaseManager.columns,
                DatabaseManager.KEY_bookid + "= ?", new String[]{bookID},
                null, null, null);
//        Cursor cursor = database.query("select * from " + DatabaseManager.bookStateTable + " where " + DatabaseManager.KEY_bookid + " = '" + bookID + "'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        Book data = new Book();
        data.setPagesArray(cursor.getString(2));
        data.setReadTime(cursor.getString(3));
        data.setLastTime(cursor.getString(4));

        return data;
    }

    public ArrayList<Book> getAllData() {
        ArrayList<Book> results = new ArrayList();
        this.database = this.dbManager.getReadableDatabase();
        Cursor cursor = this.database.rawQuery("select * from bookTable", null);
        if (cursor.moveToFirst()) {
            do {
                Book row = new Book();
                row.setBookID(cursor.getString(1));
                row.setBookName(cursor.getString(2));
                row.setBookLocalUrl(cursor.getString(3));
                row.setBookImageLocalUrl(cursor.getString(4));
                row.setSpecifiedTime(cursor.getString(5));
                row.setTotalPage(cursor.getString(6));
                row.setLibraryPosition(cursor.getString(7));
                results.add(row);
            } while (cursor.moveToNext());
        }
        return results;
    }

    public ArrayList<Book> getAllBookStateData() {
        ArrayList<Book> results = new ArrayList<Book>();

        database = dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + DatabaseManager.bookStateTable, null);
        if (cursor.moveToFirst()) {
            do {
                Book row = new Book();

                row.setBookID(cursor.getString(1));
                row.setPagesArray(cursor.getString(2));
                row.setReadTime(cursor.getString(3));
                row.setLastTime(cursor.getString(4));

                results.add(row);
            } while (cursor.moveToNext());
        }

        return results;
    }


//    public void deleteOneData(String bookName) {
//        SQLiteDatabase db = dbManager.getWritableDatabase();
//
//        db.delete(DatabaseManager.tableName, DatabaseManager.KEY_bookName + " = ?", new String[]{bookName});
//
//        db.close();
//    }
}
