package ours.china.hours.Utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ours.china.hours.R;

public class AlertAddFavorites extends Dialog {

    RelativeLayout relCancel, relConfirm;
    EditText edtStr;
    TextView alertContent;
    addFavoriteListener listener;

    String tempStr;

    public AlertAddFavorites(@NonNull Context context, int themeResId, String content) {
        super(context, themeResId);
        this.listener = (addFavoriteListener) context;

        tempStr = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_add_favorites);

        init();
        event();
    }

    public void init() {
        alertContent = findViewById(R.id.alertContent);
        alertContent.setText(tempStr);
    }

    public void event() {
        relCancel = findViewById(R.id.relCancel);
        relCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        edtStr = findViewById(R.id.edtStr);

        relConfirm = findViewById(R.id.relConfirm);
        relConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempString = edtStr.getText().toString();
                listener.addFavorite(tempString);

                edtStr.setText("");
                dismiss();
            }
        });
    }

    public interface addFavoriteListener {
        void addFavorite(String str);
    }
    public void setEditText(String str){
        edtStr.setText(str);
    }
}
