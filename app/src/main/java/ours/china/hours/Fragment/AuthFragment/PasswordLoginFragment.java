package ours.china.hours.Fragment.AuthFragment;


import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import ours.china.hours.Activity.Auth.ForgotPassActivity;
import ours.china.hours.Activity.Auth.RegisterActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Login;
import ours.china.hours.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PasswordLoginFragment extends Fragment {

    private EditText edFrMobile, edFrPassword;
    private Button btnFrLogin;
    private TextView tvFrForgot, tvFrRegister;
    APIInterface apiInterface;


    public PasswordLoginFragment() {
        // Required empty public constructor
    }


    public static PasswordLoginFragment newInstance() {

        return new PasswordLoginFragment();
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiInterface = APIClient.getClient().create(APIInterface.class);
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
                                if (e == null) {
                                    JSONObject resObj = null;
                                    try {
                                        resObj = new JSONObject(result.toString());
                                        Global.token = resObj.getString("access_token");

                                        Toast.makeText(getContext(), Global.token, Toast.LENGTH_SHORT).show();

                                        if (Global.token != null && !Global.token.equals("")) {
                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }

                                } else {
                                    Toast.makeText(getContext(), "This is error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });


    }

}
