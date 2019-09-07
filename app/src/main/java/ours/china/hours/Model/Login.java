package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("access_token")
    @Expose
    public String access_token;

    @SerializedName("expires_in")
    @Expose
    public String expires_in;

    @SerializedName("token_type")
    @Expose
    public String token_type;

    @SerializedName("scope")
    @Expose
    public String scope;

    @SerializedName("refresh_token")
    @Expose
    public String refresh_token;

    @SerializedName("error")
    @Expose
    public String error;

}
