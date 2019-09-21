package ours.china.hours.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.BookLib.foobnix.pdf.search.view.BookshelfView;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.BookStatus;
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
        values.put(DatabaseManager.KEY_bookId, data.bookId);
        values.put(DatabaseManager.KEY_bookSerial, data.bookSerial);
        values.put(DatabaseManager.KEY_coverUrl, data.coverUrl);
        values.put(DatabaseManager.KEY_bookName, data.bookName);
        values.put(DatabaseManager.KEY_bookNameUrl, data.bookNameUrl);
        values.put(DatabaseManager.KEY_publishDate, data.publishDate);
        values.put(DatabaseManager.KEY_author, data.author);
        values.put(DatabaseManager.KEY_averageProgress, data.averageProgress);
        values.put(DatabaseManager.KEY_averageTime, data.averageTime);
        values.put(DatabaseManager.KEY_allAverageTime, data.allAverageTime);
        values.put(DatabaseManager.KEY_deadline, data.deadline);
        values.put(DatabaseManager.KEY_demandTime, data.demandTime);
        values.put(DatabaseManager.KEY_favouriteCount, data.favouriteCount);
        values.put(DatabaseManager.KEY_downloadedCount, data.downloadedCount);
        values.put(DatabaseManager.KEY_attentionCount, data.attentionCount);
        values.put(DatabaseManager.KEY_readingCount, data.readingCount);
        values.put(DatabaseManager.KEY_isReadCount, data.isReadCount);
        values.put(DatabaseManager.KEY_isDeleted, data.isDeleted);
        values.put(DatabaseManager.KEY_summary, data.summary);
        values.put(DatabaseManager.KEY_category, data.category);
        values.put(DatabaseManager.KEY_publishingHouse, data.publishingHouse);
        values.put(DatabaseManager.KEY_pageCount, data.pageCount);
        values.put(DatabaseManager.KEY_isbn, data.isbn);
        values.put(DatabaseManager.KEY_edition, data.edition);
        values.put(DatabaseManager.KEY_fileName, data.fileName);

        values.put(DatabaseManager.KEY_bookLocalUrl, data.bookLocalUrl);
        values.put(DatabaseManager.KEY_bookImageLocalUrl, data.bookImageLocalUrl);
        values.put(DatabaseManager.KEY_libraryPosition, data.libraryPosition);

        database.insert(DatabaseManager.bookTable, null, values);
    }

    public void insertBookStateData(BookStatus data, String bookID) {
        database = dbManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseManager.KEY_bookId, bookID);
        values.put(DatabaseManager.KEY_pages, data.pages);
        values.put(DatabaseManager.KEY_time, data.time);
        values.put(DatabaseManager.KEY_progress, data.progress);
        values.put(DatabaseManager.KEY_lastRead, data.lastRead);
        values.put(DatabaseManager.KEY_isRead, data.isRead);
        values.put(DatabaseManager.KEY_isAttention, data.isAttention);
        values.put(DatabaseManager.KEY_collection, data.collection);
        values.put(DatabaseManager.KEY_notes, data.notes);
        values.put(DatabaseManager.KEY_bookmarks, data.bookmarks);


        database.insert(DatabaseManager.bookStateTable, null, values);
    }

    public void updateBookStateData(BookStatus data, String bookID) {
        ContentValues updateValues = new ContentValues();

        updateValues.put(DatabaseManager.KEY_bookId, bookID);
        updateValues.put(DatabaseManager.KEY_pages, data.pages);
        updateValues.put(DatabaseManager.KEY_time, data.time);
        updateValues.put(DatabaseManager.KEY_progress, data.progress);
        updateValues.put(DatabaseManager.KEY_lastRead, data.lastRead);
        updateValues.put(DatabaseManager.KEY_isRead, data.isRead);
        updateValues.put(DatabaseManager.KEY_isAttention, data.isAttention);
        updateValues.put(DatabaseManager.KEY_collection, data.collection);
        updateValues.put(DatabaseManager.KEY_notes, data.notes);
        updateValues.put(DatabaseManager.KEY_bookmarks, data.bookmarks);

        String where = DatabaseManager.KEY_bookId + " = ?";
        database = dbManager.getReadableDatabase();

        database.update(DatabaseManager.bookStateTable, updateValues, where, new String[]{bookID});
        database.close();
    }

    public void updateBookPageNumbersState(BookStatus data, String bookID) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseManager.KEY_pages, data.pages);

        String where = DatabaseManager.KEY_bookId + " = ?";
        database = dbManager.getReadableDatabase();

        database.update(DatabaseManager.bookStateTable, updateValues, where, new String[]{bookID});
        database.close();
    }

    public void updateBookReadTimeState(BookStatus data, String bookID) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseManager.KEY_time, data.time);

        String where = DatabaseManager.KEY_bookId + " = ?";
        database = dbManager.getReadableDatabase();

        database.update(DatabaseManager.bookStateTable, updateValues, where, new String[]{bookID});
        database.close();
    }

    public void updateBookLastTimeState(BookStatus data, String bookID) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseManager.KEY_lastRead, data.lastRead);

        String where = DatabaseManager.KEY_bookId + " = ?";
        database = dbManager.getReadableDatabase();

        database.update(DatabaseManager.bookStateTable, updateValues, where, new String[]{bookID});
        database.close();
    }

    public void updateBookTotalPage(Book data) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(DatabaseManager.KEY_pageCount, data.pageCount);
        database = dbManager.getReadableDatabase();
        String[] strArr = new String[]{data.bookId};
        database.update(DatabaseManager.bookTable, updateValues, DatabaseManager.KEY_bookId + " = ?", strArr);
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

    public BookStatus getBookStateData(String bookID) {
        database = dbManager.getWritableDatabase();

        Cursor cursor = database.query(DatabaseManager.bookStateTable, DatabaseManager.columns,
                DatabaseManager.KEY_bookId + "= ?", new String[]{bookID},
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        BookStatus data = new BookStatus();
        data.pages = cursor.getString(2);
        data.time = cursor.getString(3);
        data.progress = cursor.getString(4);
        data.lastRead = cursor.getString(5);
        data.isRead = cursor.getString(6);
        data.isAttention = cursor.getString(7);
        data.collection = cursor.getString(8);
        data.notes = cursor.getString(9);
        data.bookmarks = cursor.getString(10);
        return data;
    }

    public ArrayList<Book> getAllData() {
        ArrayList<Book> results = new ArrayList();
        this.database = this.dbManager.getReadableDatabase();
        Cursor cursor = this.database.rawQuery("select * from bookTable", null);
        if (cursor.moveToFirst()) {
            do {
                Book row = new Book();
                row.bookId = cursor.getString(1);
                row.bookName = cursor.getString(2);
                row.bookLocalUrl = cursor.getString(3);
                row.bookImageLocalUrl = cursor.getString(4);
                row.demandTime = cursor.getString(5);
                row.pageCount = cursor.getString(6);
                row.libraryPosition = cursor.getString(7);

                row.bookId = cursor.getString(1);
                row.bookSerial = cursor.getString(2);
                row.coverUrl = cursor.getString(3);
                row.bookName = cursor.getString(4);
                row.bookNameUrl = cursor.getString(5);
                row.publishDate = cursor.getString(6);
                row.author = cursor.getString(7);
                row.averageProgress = cursor.getString(8);
                row.averageTime = cursor.getString(9);
                row.allAverageTime = cursor.getString(10);
                row.deadline = cursor.getString(11);
                row.demandTime = cursor.getString(12);
                row.favouriteCount = cursor.getString(13);
                row.downloadedCount = cursor.getString(14);
                row.attentionCount = cursor.getString(15);
                row.readingCount = cursor.getString(16);
                row.isReadCount = cursor.getString(17);
                row.isDeleted = cursor.getString(18);
                row.summary = cursor.getString(19);
                row.category = cursor.getString(20);
                row.publishingHouse = cursor.getString(21);
                row.pageCount = cursor.getString(22);
                row.isbn = cursor.getString(23);
                row.edition = cursor.getString(24);
                row.fileName = cursor.getString(25);
                row.bookLocalUrl = cursor.getString(26);
                row.bookImageLocalUrl = cursor.getString(27);
                row.libraryPosition = cursor.getString(28);
                row.bookStatus = getBookStateData(cursor.getString(1));
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

                row.bookId = (cursor.getString(1));
                row.bookStatus.pages = cursor.getString(2);
                row.bookStatus.time = cursor.getString(3);
                row.bookStatus.lastRead = cursor.getString(4);

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
