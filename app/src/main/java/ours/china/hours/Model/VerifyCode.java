package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyCode {

    @SerializedName("res")
    @Expose
    public String res;

    @SerializedName("userId")
    @Expose
    public String userId;

    @SerializedName("err")
    @Expose
    public String err;

}
