package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class User {
    @SerializedName("userId") @Expose public String userId ="";
    @SerializedName("userSerial") @Expose public String userSerial ="";
    @SerializedName("name") @Expose public String name ="";
    @SerializedName("password") @Expose public String password ="";
    @SerializedName("studId") @Expose public String studId ="";
    @SerializedName("class") @Expose public String className ="";
    @SerializedName("school") @Expose public String school ="";
    @SerializedName("isDeleted") @Expose public boolean isDeleted =false;
    @SerializedName("mobile") @Expose public String mobile ="";
    @SerializedName("identyStatus") @Expose public String identyStatus ="";
    @SerializedName("useFaceLogin") @Expose public boolean useFaceLogin =false;
    @SerializedName("faceStatus") @Expose public String faceStatus ="";
    @SerializedName("verified") @Expose public boolean verified =false;
    @SerializedName("isFaceUsing") @Expose public boolean isFaceUsing =false;
    @SerializedName("faceStateInfo") @Expose public String faceStateInfo ="";
    @SerializedName("faceState") @Expose public String faceState ="";
    @SerializedName("created_at") @Expose public String created_at ="";
    @SerializedName("nickName") @Expose public String nickName ="";
    @SerializedName("faceInfoUrl") @Expose public String faceInfoUrl ="";
    @SerializedName("faceImageUrl") @Expose public String faceImageUrl ="";
    @SerializedName("faceHash") @Expose public String faceHash ="";
    @SerializedName("idCardFront") @Expose public String idCardFront ="";
    @SerializedName("idCardBack") @Expose public String idCardBack ="";
    @SerializedName("attentionBookIds") @Expose public String attentionBookIds = "";
    @SerializedName("lastReadBookId") @Expose public String lastReadBookId ="";
}
