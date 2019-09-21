package ours.china.hours.Management;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Model.Book;

public class BookManagement {
    public static void saveFocuseBook(Book book, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<Book>() {
        }.getType();
        String json = gson.toJson(book, type);
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.FOCUSE_BOOK, json);
    }
    public static Book getFocuseBook(SharedPreferencesManager sharedPreferencesManager){
        Book book = new Book();
        String userInfoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.FOCUSE_BOOK);
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Book>() {
            }.getType();
            book = gson.fromJson(userInfoStr, type);
        }
        return book;
    }
    public static ArrayList<Book> getBooks(SharedPreferencesManager sharedPreferencesManager){
        ArrayList<Book> flexStrings = new ArrayList<Book>(){};
        String userInfoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.FOCUSE_BOOKLIST);
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Book>>() {
            }.getType();
            flexStrings = gson.fromJson(userInfoStr, type);
        }
        return flexStrings;
    }
    public static void setBooks(ArrayList<Book> flexStrings, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Book>>() {
        }.getType();
        String json = gson.toJson(flexStrings, type);
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.FOCUSE_BOOKLIST, json);
    }
}
