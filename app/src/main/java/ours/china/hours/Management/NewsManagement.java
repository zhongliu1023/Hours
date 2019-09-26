package ours.china.hours.Management;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.NewsItem;

public class NewsManagement {

    public static void saveFoucsNews(ArrayList<NewsItem> newsList, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NewsItem>>() {
        }.getType();
        String json = gson.toJson(newsList, type);
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.FOCUS_NEWSLIST, json);
    }

    public static ArrayList<NewsItem> getFoucsNews(SharedPreferencesManager sharedPreferencesManager){
        ArrayList<NewsItem> newsList = new ArrayList<>();
        String userInfoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.FOCUS_NEWSLIST);
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<NewsItem>>() {
            }.getType();
            newsList = gson.fromJson(userInfoStr, type);
        }
        return newsList;
    }
}
