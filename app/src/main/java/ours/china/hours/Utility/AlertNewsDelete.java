package ours.china.hours.Utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import ours.china.hours.R;

public class AlertNewsDelete extends Dialog {

    RelativeLayout relCancel;
    RelativeLayout recDelete;

    deleteButtonListener listener;

    public AlertNewsDelete(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.listener = (deleteButtonListener) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_news_delete_all);

        event();
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
