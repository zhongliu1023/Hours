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
        values.put(DatabaseManager.KEY_bookName, data.getBookName());
        values.put(DatabaseManager.KEY_bookAuthor, data.getBookAuthor());
        values.put(DatabaseManager.KEY_bookLocalUrl, data.getBookLocalUrl());
        values.put(DatabaseManager.KEY_bookImageLocalUrl, data.getBookImageLocalUrl());

        database.insert(dbManager.tableName, null, values);
    }

    // get one data
    public Book getOneData(String bookName) {
        database = dbManager.getReadableDatabase();

        Cursor cursor = database.query(DatabaseManager.tableName, DatabaseManager.columns,
                DatabaseManager.KEY_bookName + "= ?", new String[]{bookName},
                null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        } else {
            return null;
        }

        Book data = new Book(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        return data;
    }

    // get all data
    public List<Book> getAllData() {
        List<Book> data = new ArrayList<>();
        SQLiteDatabase db = dbManager.getWritableDatabase();

        String selectquery = "SELECT * FROM " + DatabaseManager.tableName;
        Cursor cursor = db.rawQuery(selectquery, null);

        if (cursor == null) {
            return null;
        }

        if(cursor.moveToFirst()){
            do{
                Book one = new Book();

                one.setBookName(cursor.getString(1));
                one.setBookAuthor(cursor.getString(2));
                one.setBookLocalUrl(cursor.getString(3));
                one.setBookImageLocalUrl(cursor.getString(4));

                data.add(one);

            } while (cursor.moveToNext());
        }

        return data;
    }

    public void deleteOneData(String bookName) {
        SQLiteDatabase db = dbManager.getWritableDatabase();

        db.delete(DatabaseManager.tableName, DatabaseManager.KEY_bookName + " = ?", new String[]{bookName});

        db.close();
    }
}
