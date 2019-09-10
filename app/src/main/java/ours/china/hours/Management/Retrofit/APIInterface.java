package ours.china.hours.Management.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ours.china.hours.Model.Confirm;
import ours.china.hours.Model.Login;
import ours.china.hours.Model.Register;
import ours.china.hours.Model.UploadIdentify;
import ours.china.hours.Model.VerifyCode;
import ours.china.hours.Model.VerifyForgot;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {

    @FormUrlEncoded
    @POST("verifyCode")
    Call<VerifyCode> verifyprocess(@Field("mobile") String mobile, @Field("password") String password);

    @FormUrlEncoded
    @POST("confirmVerify")
    Call<Confirm> confirmVerify(@Field("mobile") String mobile, @Field("otp") String otp);

    @FormUrlEncoded
    @POST("register")
    Call<Register> register(@Field("name") String name, @Field("mobile") String mobile,
                            @Field("identyStatus") String identyStatus, @Field("faceState") String faceState,
                            @Field("school") String school, @Field("class") String classs,
                            @Field("studId") String studId);

    @Multipart
    @POST("uploadIdentify")
    Call<UploadIdentify> uploadIdentify(@Part MultipartBody.Part file, @Part("name") RequestBody nam );


    @FormUrlEncoded
    @POST("verifyForgot")
    Call<VerifyForgot> verifyForgot(@Field("mobile") String mobile, @Field("password") String password);

    @FormUrlEncoded
    @POST("confirmForgot")
    Call<VerifyForgot> confirmForgot(@Field("mobile") String mobile, @Field("password") String password, @Field("otp") String otp);


    @FormUrlEncoded
    @POST("login")
    Call<Login> login(@Field("grant_type") String grant_type, @Field("username") String mobile,
                    @Field("password") String password,@Field("client_id") String client_id,
                    @Field("client_secret") String client_secret, @Field("scope") String scope);

    @FormUrlEncoded
    @POST("feedback")
    Call<VerifyCode> feedback(@Field("access_token") String access_token, @Field("mobile") String mobile,@Field("feedback") String feedback);

    @FormUrlEncoded
    @POST("updatePassword")
    Call<VerifyCode> updatePassword(@Field("access_token") String access_token, @Field("mobile") String mobile,@Field("oldPass") String oldPass, @Field("newPass") String newPass);

    @FormUrlEncoded
    @POST("updateMobile")
    Call<VerifyCode> updateMobile(@Field("access_token") String access_token, @Field("oldMobile") String oldMobile,@Field("newMobile") String newMobile);

    @FormUrlEncoded
    @POST("confirmUpdateMobile")
    Call<VerifyCode> confirmUpdateMobile(@Field("access_token") String access_token, @Field("oldMobile") String oldMobile,@Field("newMobile") String newMobile, @Field("otp") String otp);


//    @FormUrlEncoded
//    @POST("register")
//    Call<Register> registerprocess(@Field("name") String name, @Field("email") String email, @Field("phone") String phone, @Field("pass") String pass, @Field("refer") String refer, @Field("promo") String promo, @Field("fb") String fb);
//
//    @FormUrlEncoded
//    @POST("new_apl")
//    Call<Apl> new_apl(@Field("u_id") String id, @Field("age") String age, @Field("doc_count") Integer docCount, @Field("sal") String sal, @Field("ded") String ded, @Field("lot") String lot, @Field("loa") double loa, @Field("eir") String eir, @Field("min") double min, @Field("ind") String ind, @Field("chk") String chk);
//
//    @FormUrlEncoded
//    @POST("update")
//    Call<Update> updateprocess(@Field("id") String id, @Field("name") String name, @Field("email") String email, @Field("phone") String phone, @Field("pass") String pass, @Field("refer") String refer, @Field("promo") String promo, @Field("fb") Integer fb);
//
//    @FormUrlEncoded
//    @POST("all_apl")
//    Call<All_Apl> all_apl(@Field("u_id") String id);
//
//    @FormUrlEncoded
//    @POST("update_apl")
//    Call<Update_Apl> update_apl(@Field("id") String id, @Field("status") String status);
//
//    @Multipart
//    @POST("upload")
//    Call<Upload> upload(@Part MultipartBody.Part file, @Part("u_id") String u_id, @Part("a_id") String a_id);
//
//    @FormUrlEncoded
//    @POST("prefer")
//    Call<Prefer> prefer(@Field("id") String id);
}