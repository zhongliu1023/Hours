package ours.china.hours.Fragment.AuthFragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ours.china.hours.Activity.Auth.ForgotPassActivity;
import ours.china.hours.Activity.Auth.RegisterActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Activity.Personality.FeedbackActivity;
import ours.china.hours.Activity.Personality.ProblemActivity;
import ours.china.hours.Activity.Personality.UpdatePasswordActivity;
import ours.china.hours.Activity.Personality.UpdateinforActivity;
import ours.china.hours.Activity.Personality.UpdatemobileActivity;
import ours.china.hours.Management.Retrofit.APIClient;
import ours.china.hours.Management.Retrofit.APIInterface;
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
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

//                final String mobile = edFrMobile.getText().toString();
//                final String password = edFrPassword.getText().toString();
//                final String grant_type = "password";
//                final String client_id = "testclient";
//                final String client_secret = "testpass";
//                final String scope = "userinfo cloud file node";
//
//                if (mobile.equals("")){
//                    Global.alert(getContext(), getResources().getString(R.string.register), getResources().getString(R.string.login_mobile), getResources().getString(R.string.confirm));
//                    edFrMobile.requestFocus();
//                    return;
//                }
//
//                if (password.equals("")){
//                    Global.alert(getContext(), getResources().getString(R.string.register), getResources().getString(R.string.login_password), getResources().getString(R.string.confirm));
//                    edFrPassword.requestFocus();
//                    return;
//                }
//
//                try {
//                    Global.showLoading(getContext(),"generate_report");
//
//                    Call<Login> call = apiInterface.login(grant_type, mobile, password, client_id, client_secret, scope);
//                    call.enqueue(new Callback<Login>() {
//                        @Override public void onResponse(Call<Login> call, Response<Login> response) {
//                            Global.hideLoading();
//
//                            if (response.code() == 404){
//                                Toast.makeText(getContext(), "404", Toast.LENGTH_SHORT).show();
//                            }else if (response.code() == 422){
//                                Toast.makeText(getContext(), "422", Toast.LENGTH_SHORT).show();
//                            }else if (response.code() == 500){
//                                Toast.makeText(getContext(), "500", Toast.LENGTH_SHORT).show();
//                            }else if (response.code() == 200){
//                                String res = response.body().access_token;
////                                if (response.body().error.equals("invalid_grant")){
//                                    Toast.makeText(getContext(),getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
////                                }else {
////                                    Toast.makeText(getContext(), res.toString(), Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(getContext(), MainActivity.class);
//                                    startActivity(intent);
////                                }
//                            }
//                        }
//
//                        @Override public void onFailure(Call<Login> call, Throwable t) {
//                            Global.hideLoading();
//                            Toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }
//                catch (Exception e) {
//                    Log.i("register" ,e.getMessage());
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
            }
        });


    }

}
