package com.training_android.daisuke.dbmanage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.NavUtils;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "school.db";
    public static final String TABLE_NAME = "tbl_stature";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "SURNAME";
    public static final String COL_4 = "STATURE";

    SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID integer primary key autoincrement, NAME text, SURNAME text, STATURE integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Student> getAllData() {
        ArrayList<Student> results = new ArrayList<Student>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Student row = new Student();

                row.id = cursor.getString(0);
                row.name = cursor.getString(1);
                row.surname = cursor.getString(2);
                row.stature = cursor.getString(3);

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
                String tmp = cursor.getString(0);
                tmp += "  " + cursor.getString(1);
                tmp += "  " + cursor.getString(2);
                tmp += "  " + cursor.getString(3);

                results.add(tmp);
            } while (cursor.moveToNext());
        }

        return results;
    }

    public boolean insertData(String name, String surname, String stature) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, surname);
        contentValues.put(COL_4, stature);
        long row = db.insert(TABLE_NAME, null, contentValues);
        if (row == -1)
            return false;
        else
            return true;
    }

    public boolean updateData(Student student) {
        ContentValues cv = new ContentValues();
        cv.put(COL_2, student.name);
        cv.put(COL_3, student.surname);
        cv.put(COL_4, student.stature);
        int cnt = db.update(TABLE_NAME, cv, "id = " + student.id, null);
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
