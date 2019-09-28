package ours.china.hours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaceInfo {
    @SerializedName("mobile") @Expose public String mobile = "";
    @SerializedName("faceInfoUrl") @Expose public String faceInfoUrl = "";
    @SerializedName("faceImageUrl") @Expose public String faceImageUrl = "";
    @SerializedName("faceStatus") @Expose public String faceStatus = "";
}
