package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Favorites {
    @SerializedName("favorite") @Expose
    public String favorite = "";
    @SerializedName("bookList") @Expose
    public ArrayList<Book> bookList = new ArrayList<Book>(){};
    @SerializedName("isChecked") @Expose
    public Boolean isChecked = false;
}
