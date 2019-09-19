package ours.china.hours.Fragment.AuthFragment;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import ours.china.hours.Activity.Auth.ForgotPassActivity;
import ours.china.hours.Activity.Auth.RegisterActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Management.Url;
import ours.china.hours.R;
import ours.china.hours.Utility.SessionManager;


public class PasswordLoginFragment extends Fragment {

    private static String TAG = "PasswordLoginFragment";

    private static String featureFileUrl;
    private static String featureImageUrl;
    private String featureFileDownloadDir;
    private String featureImageFileDownloadDir;

    private String isImage = "no";

    private String tempMobileNumber;

    private EditText edFrMobile, edFrPassword;
    private Button btnFrLogin;
    private TextView tvFrForgot, tvFrRegister;

    SessionManager sessionManager;


    public PasswordLoginFragment() {
        // Required empty public constructor
    }


    public static PasswordLoginFragment newInstance() {
        return new PasswordLoginFragment();
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getContext());
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


        featureFileDownloadDir = getContext().getFilesDir().getAbsolutePath() + File.separator + "register" + File.separator + "features" + File.separator;
        featureImageFileDownloadDir = getContext().getFilesDir().getAbsolutePath() + File.separator + "register" + File.separator + "imgs" + File.separator;

        Log.i(TAG, featureFileDownloadDir);
        Log.i(TAG, featureImageFileDownloadDir);

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
                final String grant_type = "password";
                final String client_id = "testclient";
                final String client_secret = "testpass";
                final String scope = "userinfo cloud file node";

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
                        .setBodyParameter("client_id", "testclient")
                        .setBodyParameter("client_secret", "testpass")
                        .setBodyParameter("scope", "userinfo cloud file node")
                        .setBodyParameter("username", mobile)
                        .setBodyParameter("password", password)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                Global.hideLoading();
                                Log.i(TAG, "result => " + result);
                                if (e == null) {
                                    JSONObject resObj = null;
                                    try {
                                        resObj = new JSONObject(result.toString());

                                        // save token and refresh token.
                                        Global.token = resObj.getString("access_token");
                                        Global.refresh_token = resObj.getString("refresh_token");

                                        // save session data.
                                        sessionManager.setPassword(password);
                                        sessionManager.setMobileNumber(mobile);

                                        if (Global.token != null && !Global.token.equals("")) {
                                            tempMobileNumber = mobile;
                                            getFeatureFileUrl();
                                        } else {
                                            Toast.makeText(getContext(), "Received data error", Toast.LENGTH_SHORT).show();
                                            Global.hideLoading();
                                        }
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                        Global.hideLoading();
                                    }

                                } else {
                                    Toast.makeText(getContext(), "Unexpected error occured.", Toast.LENGTH_SHORT).show();
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
        Log.i(TAG, "featureDir deleted");
        try {
            FileUtils.deleteDirectory(new File(featureImageFileDownloadDir));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        Log.i(TAG, "featureImageDir deleted");

    }

    public void getFeatureFileUrl() {
        Log.i(TAG, "Url => " + Url.getFaceInfoUrl);

        Ion.with(getContext())
                .load(Url.getFaceInfoUrl)
                .setTimeout(10000)
                .setBodyParameter("access_token", Global.token)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.i(TAG, "result = >" + result);

                        if (e == null) {
                            try {
                                JSONObject resObj = new JSONObject(result.toString());
                                if (resObj.getString("res").equals("success")) {

                                    JSONObject tempJsonObject = new JSONObject(resObj.getString("data"));
                                    featureFileUrl = Url.baseUrl + tempJsonObject.getString("faceInfoUrl");
                                    featureImageUrl = Url.baseUrl + tempJsonObject.getString("faceImageUrl");

                                    Log.i(TAG, "featureFileUrl => " + featureFileUrl);
                                    Log.i(TAG, "featureImageUrl => " + featureImageUrl);

                                    deleteAlreadyExistFiles();
                                    new DownloadFeatureFile(getContext()).execute(featureFileUrl);

                                } else {
                                    Global.hideLoading();
                                }
                            } catch (Exception error) {
                                Global.hideLoading();
                            }
                        } else {
                            Toast.makeText(getContext(), "发生意外错误", Toast.LENGTH_SHORT).show();
                            Global.hideLoading();
                        }
                    }
                });
    }

    public class DownloadFeatureFile extends AsyncTask<String, String, String> {

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
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String dir = "";

                if (isImage.equals("no")) {
                    dir = featureFileDownloadDir;
                } else if (isImage.equals("yes")) {
                    dir = featureImageFileDownloadDir;
                }

                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                OutputStream output = new FileOutputStream(featureFileDownloadDir + fileName);
                Log.i(TAG, "featureDownloadFileUrl => " + featureFileDownloadDir + fileName);

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

            } else if (message.equals("success") && isImage.equals("yes")) {

                Global.hideLoading();
                Log.i(TAG, "feature image file download end");

                isImage = "no";
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

                Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
            } else {
                Global.hideLoading();
                Toast.makeText(getContext(), "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
