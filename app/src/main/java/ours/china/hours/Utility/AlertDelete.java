package ours.china.hours.Utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ours.china.hours.R;

public class AlertDelete extends Dialog {

    RelativeLayout relCancel;
    RelativeLayout recDelete;
    TextView txtContent;

    deleteButtonListener listener;
    String tempStr;

    public AlertDelete(@NonNull Context context, int themeResId, String strContent) {
        super(context, themeResId);
        this.listener = (deleteButtonListener) context;
        tempStr = strContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_news_delete_all);

        init();
        event();
    }

    public void init() {
        txtContent = findViewById(R.id.txtContent);
        txtContent.setText(tempStr);
    }

    public void event() {
        relCancel = findViewById(R.id.relCancel);
        relCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        recDelete = findViewById(R.id.relConfirm);
        recDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete();
                dismiss();
            }
        });
    }

    public interface deleteButtonListener {
        void onDelete();
    }
}
