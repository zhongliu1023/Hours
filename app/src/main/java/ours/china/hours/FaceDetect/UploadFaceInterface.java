package ours.china.hours.FaceDetect;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ours.china.hours.Model.UploadObject;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadFaceInterface {
    @Multipart
    @POST("bookUserFaceInfo")
    Call<UploadObject> uploadFile(@Part MultipartBody.Part file, @Part("name") RequestBody name);
}
