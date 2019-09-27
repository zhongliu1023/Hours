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
import ours.china.hours.Common.Utils.JsonUtil;
import ours.china.hours.Model.User;

/**
 * Created by liujie on 1/26/18.
 */

public class UsersManagement {
    public User mapFetchProfileResponse(JSONObject responseJsonObject) throws JSONException {
        User uservo = new User();
        uservo.userId = JsonUtil.getString(responseJsonObject, "userId");
        uservo.name = JsonUtil.getString(responseJsonObject, "name");
        uservo.studId = JsonUtil.getString(responseJsonObject, "studId");
        uservo.className = JsonUtil.getString(responseJsonObject, "class");
        uservo.school = JsonUtil.getString(responseJsonObject, "school");
        uservo.lastReadBookId = JsonUtil.getString(responseJsonObject, "lastReadBookId");
        uservo.isDeleted = Boolean.parseBoolean(JsonUtil.getString(responseJsonObject, "isDeleted"));
        uservo.mobile = JsonUtil.getString(responseJsonObject, "mobile");
        uservo.identyStatus = JsonUtil.getString(responseJsonObject, "identyStatus");
        uservo.isFaceUsing = Boolean.parseBoolean(JsonUtil.getString(responseJsonObject, "isFaceUsing"));
        uservo.faceStateInfo = JsonUtil.getString(responseJsonObject, "faceStateInfo");
        uservo.faceState = JsonUtil.getString(responseJsonObject, "faceState");
        uservo.verified = Boolean.parseBoolean(JsonUtil.getString(responseJsonObject, "verified"));
        uservo.created_at = JsonUtil.getString(responseJsonObject, "created_at");
        uservo.nickName = JsonUtil.getString(responseJsonObject, "nickName");
        uservo.faceInfoUrl = JsonUtil.getString(responseJsonObject, "faceInfoUrl");
        uservo.faceImageUrl = JsonUtil.getString(responseJsonObject, "faceImageUrl");
        uservo.faceHash = JsonUtil.getString(responseJsonObject, "faceHash");
        uservo.idCardFront = JsonUtil.getString(responseJsonObject, "idCardFront");
        uservo.idCardBack = JsonUtil.getString(responseJsonObject, "idCardBack");
        uservo.attentionBookIds = JsonUtil.getString(responseJsonObject, "attentionBookIds");
        return uservo;
    }
    public static void saveCurrentUser(User userVo, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {
        }.getType();
        String json = gson.toJson(userVo, type);
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.CURRENT_USER, json);
        Global.currentUser = userVo;
    }
    public static User getCurrentUser(SharedPreferencesManager sharedPreferencesManager){
        User currentUser = new User();
        String userInfoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENT_USER);
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<User>() {
            }.getType();
            currentUser = gson.fromJson(userInfoStr, type);
        }
        return currentUser;
    }
    public static ArrayList<String> getFlexStrings(SharedPreferencesManager sharedPreferencesManager){
        ArrayList<String> flexStrings = new ArrayList<String>(){};
        String userInfoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.FLEX_STRINGS);
        if (!userInfoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            flexStrings = gson.fromJson(userInfoStr, type);
        }
        return flexStrings;
    }
    public static void setFlexStrings(ArrayList<String> flexStrings, SharedPreferencesManager sharedPreferencesManager){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        String json = gson.toJson(flexStrings, type);
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.FLEX_STRINGS, json);
    }
}
