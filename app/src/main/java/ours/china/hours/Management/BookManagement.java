package ours.china.hours.Management;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ours.china.hours.Activity.Global;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.Favorites;
import ours.china.hours.Services.BookFile;

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

    public static ArrayList<Favorites> getFavorites(SharedPreferencesManager sharedPreferencesManager){
        ArrayList<Favorites> favorites = new ArrayList<Favorites>(){};
        ArrayList<Favorites> returnFavorites = new ArrayList<Favorites>();
        String userInfoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.FAVORITES);
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Favorites>>() {
            }.getType();
            favorites = gson.fromJson(userInfoStr, type);
        }

        for (Favorites one : favorites) {
            if (one.favorite.equals("")) {
                continue;
            } else {
                returnFavorites.add(one);
            }
        }
        return returnFavorites;
    }

    public static void saveFavorites(ArrayList<Favorites> favorites, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Favorites>>() {
        }.getType();
        String json = gson.toJson(favorites, type);
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.FAVORITES, json);
    }

    public static void saveFullFavorites(String str, SharedPreferencesManager sharedPreferencesManager){
        Global.fullFavorites = str;
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.COLLECTIONS, str);
    }

    public static String getFullFavorites(SharedPreferencesManager sharedPreferencesManager){
        return sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.COLLECTIONS);
    }
}
