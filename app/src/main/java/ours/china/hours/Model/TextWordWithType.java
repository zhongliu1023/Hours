package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.ebookdroid.droids.mupdf.codec.TextWord;

public class TextWordWithType {
    @SerializedName("textWord") @Expose
    public TextWord textWord = new TextWord();
    @SerializedName("type") @Expose public int type = 0;
}
