package ours.china.hours.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ours.china.hours.R;


public class PhoneVerifyActivity extends AppCompatActivity {

    EditText inputPhoneNumber, verifyPhone;
    Button btnTransmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        init();
        event();
    }

    public void init() {
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        verifyPhone = findViewById(R.id.verifyPhone);
    }

    public void event() {
        btnTransmit = findViewById(R.id.btnTransmit);
        btnTransmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhoneVerifyActivity.this, FaceRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
