package ours.china.hours.Management.WebServices.mapper;

import android.content.Context;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Common.Sharedpreferences.SharedPreferencesKeys;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Common.Utils.JsonKeys;
import ours.china.hours.Common.Utils.JsonUtil;
import ours.china.hours.Constants.URLConstant;


/**
 * Created by ashishshah on 13/05/17.
 */

public class AccountMapper {
    public RequestParams mapAccessTokenRequest(String generatedCode, long currentTime) {
        RequestParams params = new RequestParams();
        params.put(JsonKeys.CLIENT_ID, URLConstant.CLIENT_ID);
        params.put(JsonKeys.CODE, generatedCode);
        //params.put(JsonKeys.TIMESTAMP, currentTime);
        params.put(JsonKeys.TIMESTAMP, currentTime);
        return params;
    }
    public String mapAccessTokenResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        SharedPreferencesManager.getInstance(context).setPrefernceValueString(SharedPreferencesKeys.ACCESS_TOKEN,
                (String) jsonObjectResponse.get(JsonKeys.ACCESS_TOKEN));
        return JsonUtil.getString(jsonObjectResponse, JsonKeys.ACCESS_TOKEN);
    }
    public RequestParams mapLogin(String mobileNumber) {
        RequestParams params = new RequestParams();
        params.put(JsonKeys.CLIENT_ID, URLConstant.CLIENT_ID);
        params.put(JsonKeys.GRANTTYPE, URLConstant.SMS_CON);
        params.put(JsonKeys.PHONENUMBER, mobileNumber);
        return params;
    }
    public RequestParams mapStartPhoneNumRequest(String mobileNumber) {
        RequestParams params = new RequestParams();
        params.put(JsonKeys.CLIENT_ID, URLConstant.CLIENT_ID);
        params.put(JsonKeys.CONNECTION, URLConstant.SMS_CON);
        params.put(JsonKeys.PHONENUMBER, mobileNumber);
        params.put(JsonKeys.EMAIL, "dev-sensingself@culabs.org");
        params.put(JsonKeys.SEND, URLConstant.SEND_CODE);
        return params;
    }
    public String mapStartPhoneNumResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONObject jsonAPI = JsonUtil.getJsonObject(jsonObjectResponse, JsonKeys.API);
        return JsonUtil.getString(jsonAPI, JsonKeys.ERROR_STRING);
    }


    public RequestParams mapVerificationCodeRequest(String mobileNumber, String verificationCode) {
        RequestParams params = new RequestParams();
        params.put(JsonKeys.CLIENT_ID, URLConstant.CLIENT_ID);
        params.put(JsonKeys.CONNECTION, URLConstant.SMS_CON);
        params.put(JsonKeys.PHONENUMBER, mobileNumber);
        params.put(JsonKeys.EMAIL, "dev-sensingself@culabs.org");
        params.put(JsonKeys.VERIFICATION_CODE, verificationCode);
        return params;
    }

    public String mapVerifyCodeResponse(Context context, JSONObject jsonObjectResponse) throws JSONException {
        JSONObject jsonResult = JsonUtil.getJsonObject(jsonObjectResponse, JsonKeys.RESULT);
        SharedPreferencesManager.getInstance(context).setPrefernceValueString(SharedPreferencesKeys.ID_TOKEN,
                (String) jsonObjectResponse.get(JsonKeys.ID_TOKEN));
        return JsonUtil.getString(jsonObjectResponse, JsonKeys.SESSION);
    }
}
