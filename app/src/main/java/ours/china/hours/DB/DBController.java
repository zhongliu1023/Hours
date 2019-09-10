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
        values.put(DatabaseManager.KEY_bookLocalUrl, data.getBookLocalUrl());
        values.put(DatabaseManager.KEY_bookImageLocalUrl, data.getBookImageLocalUrl());

        database.insert(DatabaseManager.bookTable, null, values);
    }

    // get one data
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

    public ArrayList<Book> getAllData() {
        ArrayList<Book> results = new ArrayList<Book>();

        database = dbManager.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + DatabaseManager.bookTable, null);
        if (cursor.moveToFirst()) {
            do {
                Book row = new Book();

                row.setBookID(cursor.getString(1));
                row.setBookLocalUrl(cursor.getString(2));
                row.setBookImageLocalUrl(cursor.getString(3));

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
