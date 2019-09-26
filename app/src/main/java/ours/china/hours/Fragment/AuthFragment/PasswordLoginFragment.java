package ours.china.hours.Fragment.AuthFragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Activity.Auth.ForgotPassActivity;
import ours.china.hours.Activity.Auth.PerfectInforActivity;
import ours.china.hours.Activity.Auth.RegisterActivity;
import ours.china.hours.Activity.Auth.Splash;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Common.Sharedpreferences.SharedPreferencesManager;
import ours.china.hours.Management.Url;
import ours.china.hours.Management.UsersManagement;
import ours.china.hours.Model.User;
import ours.china.hours.R;
import ours.china.hours.Utility.SessionManager;


public class PasswordLoginFragment extends Fragment {

    private static String TAG = "PasswordLoginFragment";

    private static String featureFileUrl;
    private static String featureImageUrl;
    private static String featureIDFrontImageUrl;
    private String featureFileDownloadDir;

    private String isImage = "no";
    private String isIDImage = "no";

    private String tempMobileNumber;

    private EditText edFrMobile, edFrPassword;
    private Button btnFrLogin;
    private TextView tvFrForgot, tvFrRegister;

    User currentUser = new User();
    SharedPreferencesManager sessionManager;

    private FaceEngine faceEngine;
    private int faceEngineCode = -1;

    private Bitmap mainBitmap;
    private FaceFeature mainFeature;

    private int type;

    public PasswordLoginFragment() {
        // Required empty public constructor
    }


    public static PasswordLoginFragment newInstance() {
        return new PasswordLoginFragment();
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initEngine();
        sessionManager = new SharedPreferencesManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_password_login, container, false);
        initUI(view);

        gotoForgotPassActivity();
        gotoRegitsterActivity();
        login();

        return view;
    }


    private void initUI(View view){

        edFrMobile = view.findViewById(R.id.edFrMobile);
        edFrPassword = view.findViewById(R.id.edFrPassword);
        btnFrLogin = view.findViewById(R.id.btnFrLogin);
        tvFrForgot = view.findViewById(R.id.tvFrForgot);
        tvFrRegister = view.findViewById(R.id.tvFrRegister);


        featureFileDownloadDir = getContext().getFilesDir().getAbsolutePath() + File.separator + "register" + File.separator;;

        Log.i(TAG, featureFileDownloadDir);

    }


    private void gotoForgotPassActivity(){
        tvFrForgot.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ForgotPassActivity.class);
                startActivity(intent);
            }
        });
    }


    private  void gotoRegitsterActivity(){
        tvFrRegister.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
//                Intent intent = new Intent(getActivity(), UpdateinforActivity.class);
                startActivity(intent);
            }
        });
    }


    private void login(){

        btnFrLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                final String mobile = edFrMobile.getText().toString();
                final String password = edFrPassword.getText().toString();

                if (mobile.equals("")){
                    Global.alert(getContext(), getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
                    edFrMobile.requestFocus();
                    return;
                }

                if (password.equals("")){
                    Global.alert(getContext(), getResources().getString(R.string.register), getResources().getString(R.string.login_password), getResources().getString(R.string.confirm));
                    edFrPassword.requestFocus();
                    return;
                }

                Global.showLoading(getContext(),"generate_report");
                Ion.with(getActivity())
                        .load(Url.loginUrl)
                        .setTimeout(10000)
                        .setBodyParameter("grant_type", "password")
                        .setBodyParameter("client_id", Url.CLIENT_ID)
                        .setBodyParameter("client_secret", Url.CLIENT_SECRET)
                        .setBodyParameter("scope", Url.SCOPE)

                        .setBodyParameter("username", mobile)
                        .setBodyParameter("password", password)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {

                                Log.i(TAG, "result => " + result);
                                if (e == null) {
                                    JSONObject resObj = null;
                                    try {
                                        resObj = new JSONObject(result.toString());

                                        if (resObj.getString("res").equals("success")) {

                                            // save token and refresh token.
                                            Global.access_token = resObj.getString("access_token");
                                            Global.refresh_token = resObj.getString("refresh_token");

                                            // save session data.
                                            currentUser.mobile = mobile;
                                            currentUser.password = password;
                                            UsersManagement.saveCurrentUser(currentUser, sessionManager);

                                            tempMobileNumber = mobile;
                                            getUserInfo();

                                        } else {
                                            Global.hideLoading();
                                            Toast.makeText(getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                        Global.hideLoading();
                                    }

                                } else {
                                    Toast.makeText(getContext(), "发生意外错误", Toast.LENGTH_SHORT).show();
                                    Global.hideLoading();
                                }
                            }
                        });

            }
        });


    }

    public void deleteAlreadyExistFiles() {

        try {
            FileUtils.deleteDirectory(new File(featureFileDownloadDir));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    void getUserInfo(){
        Ion.with(getActivity())
                .load(Url.get_profile)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.access_token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            JSONObject resObj = null;
                            try {
                                resObj = new JSONObject(result.toString());
                                User user = new UsersManagement().mapFetchProfileResponse(resObj);
                                user.password = currentUser.password;
                                featureImageUrl = user.faceImageUrl;
                                featureFileUrl = user.faceInfoUrl;
                                featureIDFrontImageUrl = user.idCardFront;

                                UsersManagement.saveCurrentUser(user, sessionManager);

                                if (user.faceInfoUrl.isEmpty()){
                                    Global.hideLoading();
                                    Intent intent = new Intent(getActivity(), PerfectInforActivity.class);
                                    startActivity(intent);
                                }else{
                                    deleteAlreadyExistFiles();
                                    new DownloadFeatureFile(getContext()).execute(user.faceInfoUrl);
                                }
                            } catch (JSONException ex) {
                                Global.hideLoading();
                                ex.printStackTrace();
                                Toast.makeText(getActivity(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Global.hideLoading();
                            Toast.makeText(getActivity(), "发生意外错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    class DownloadFeatureFile extends AsyncTask<String, String, String> {

        private Context context;
        private String fileName;
        private String folder;

        public DownloadFeatureFile(Context context) {
            this.context = context;
        }

        /* Access modifiers changed, original: protected */
        public void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(Url.domainUrl +"/"+ f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String dir = "";

                File directory1 = new File(featureFileDownloadDir);
                if (!directory1.exists()) {
                    directory1.mkdirs();
                }

                if (isImage.equals("no")) {
                    dir = featureFileDownloadDir + "features" + File.separator;
                } else if (isImage.equals("yes") && isIDImage.equals("no")) {
                    dir = featureFileDownloadDir+ "imgs" + File.separator;
                    Global.faceFeatureImageUrl = dir + fileName;
                } else if (isImage.equals("yes") && isIDImage.equals("yes")) {
                    dir = featureFileDownloadDir+ "imgs" + File.separator;
                    Global.faceIDFeatureImageUrl = dir + fileName;
                }

                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                OutputStream output = new FileOutputStream(dir + fileName);
                Log.i(TAG, "featureDownloadFileUrl => " + dir + fileName);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

                return "success";

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "fail";
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            if (message.equals("success") && isImage.equals("no")) {
                Log.i(TAG, "feautre file download end");

                isImage = "yes";
                new DownloadFeatureFile(getContext()).execute(featureImageUrl);

            } else if (message.equals("success") && isImage.equals("yes") && isIDImage.equals("no")) {

//                Global.hideLoading();
                Log.i(TAG, "feature image file download end");

                // to get faceFeature
                try {
                    Bitmap mainBimap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(new File(Global.faceFeatureImageUrl)));
                    type = 0;
                    processImage(mainBimap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                isIDImage = "yes";
                new DownloadFeatureFile(getContext()).execute(featureIDFrontImageUrl);
            } else if (message.equals("success") && isImage.equals("yes") && isIDImage.equals("yes")) {

                Log.i(TAG, "feature ID image file download end");

                // to get faceFeature
                try {
                    Bitmap mainBimap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(new File(Global.faceIDFeatureImageUrl)));
                    type = 1;
                    processImage(mainBimap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                isIDImage = "no";
                isImage = "no";

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

                Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();

            } else {

                Intent intent = new Intent(getActivity(), PerfectInforActivity.class);
                startActivity(intent);
                Global.hideLoading();
                Toast.makeText(getContext(), "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void initEngine() {

        faceEngine = new FaceEngine();
        faceEngineCode = faceEngine.init(getActivity(), FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine.ASF_OP_0_ONLY,
                16, 6, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);

        Log.i(TAG, "initEngine: init " + faceEngineCode);

        if (faceEngineCode != ErrorInfo.MOK) {
            Toast.makeText(getActivity(), getString(R.string.init_failed, faceEngineCode), Toast.LENGTH_SHORT).show();
        }
    }

    private void unInitEngine() {
        if (faceEngine != null) {
            faceEngineCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + faceEngineCode);
        }
    }

    @Override
    public void onDestroy() {
        unInitEngine();
        super.onDestroy();
    }

    public void processImage(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        if (faceEngine == null) {
            return;
        }

        //接口需要的bgr24宽度必须为4的倍数
        bitmap = ImageUtils.alignBitmapForBgr24(bitmap);

        if (bitmap == null) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //bitmap转bgr24
        final byte[] bgr24 = ImageUtils.bitmapToBgr24(bitmap);

        if (bgr24 != null) {

            List<FaceInfo> faceInfoList = new ArrayList<>();
            //人脸检测
            int detectCode = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
            if (detectCode != 0 || faceInfoList.size() == 0) {
//                showToast("face detection finished, code is " + detectCode + ", face num is " + faceInfoList.size());
                return;
            }
            //绘制bitmap
            bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(10);
            paint.setColor(Color.YELLOW);

            if (faceInfoList.size() > 0) {

                for (int i = 0; i < faceInfoList.size(); i++) {
                    //绘制人脸框
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(faceInfoList.get(i).getRect(), paint);
                    //绘制人脸序号
                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                    paint.setTextSize(faceInfoList.get(i).getRect().width() / 2);
                    canvas.drawText("" + i, faceInfoList.get(i).getRect().left, faceInfoList.get(i).getRect().top, paint);

                }
            }

            int faceProcessCode = faceEngine.process(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList, FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);
            Log.i(TAG, "processImage: " + faceProcessCode);
            if (faceProcessCode != ErrorInfo.MOK) {
//                showToast("face process finished, code is " + faceProcessCode);
                return;
            }
            //年龄信息结果
            List<AgeInfo> ageInfoList = new ArrayList<>();
            //性别信息结果
            List<GenderInfo> genderInfoList = new ArrayList<>();
            //三维角度结果
            List<Face3DAngle> face3DAngleList = new ArrayList<>();
            //获取年龄、性别、三维角度
            int ageCode = faceEngine.getAge(ageInfoList);
            int genderCode = faceEngine.getGender(genderInfoList);
            int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);

            if ((ageCode | genderCode | face3DAngleCode) != ErrorInfo.MOK) {
//                showToast("at lease one of age、gender、face3DAngle detect failed! codes are: " + ageCode
//                        + " ," + genderCode + " ," + face3DAngleCode);
                return;
            }

            //人脸比对数据显示
            if (faceInfoList.size() > 0) {
                mainFeature = new FaceFeature();
                int res = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), mainFeature);
                if (res != ErrorInfo.MOK) {
                    mainFeature = null;
                }

                if (type == 0) {
                    Global.faceFeature = mainFeature;
                } else if (type == 1) {
                    Global.faceIDFeature = mainFeature;
                }
            } else {
                mainBitmap = null;
            }

        } else {
//            showToast("can not get bgr24 from bitmap!");
        }
    }
}
