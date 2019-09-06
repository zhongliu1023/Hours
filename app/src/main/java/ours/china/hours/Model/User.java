package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    @Expose
    public String name ="";

    @SerializedName("fb")
    @Expose
    public String fb;

    @SerializedName("pass")
    @Expose
    public String pass;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("promo")
    @Expose
    public String promo;

    @SerializedName("res")
    @Expose
    public String res;

    @SerializedName("refer")
    @Expose
    public String refer;

    @SerializedName("err")
    @Expose
    public String err;

}
