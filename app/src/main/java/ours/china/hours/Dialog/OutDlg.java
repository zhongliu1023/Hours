package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import ours.china.hours.Activity.Auth.LoginOptionActivity;
import ours.china.hours.R;
import ours.china.hours.Utility.SessionManager;

public class OutDlg extends Dialog {

    SessionManager sessionManager;
    public TextView tvOutConfirm, tvOutCancel;
    public Button btnOk;


    public OutDlg(Context context) {
        super(context);

        setContentView(R.layout.dialog_out);
        sessionManager = new SessionManager(context);

        tvOutCancel = (TextView)this.findViewById(R.id.tvOutCancel);
        tvOutConfirm = (TextView)this.findViewById(R.id.tvOutConfirm);

        tvOutCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                dismiss();
            }
        });

        tvOutConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                sessionManager.setMobileNumber("");
                sessionManager.setPassword("");

                Intent intent = new Intent(getContext(), LoginOptionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }


    public OutDlg(Context context, int themeResId) {
        super( context, themeResId );

        setContentView(R.layout.dialog_out);

        tvOutCancel = (TextView)this.findViewById(R.id.tvOutCancel);
        tvOutConfirm = (TextView)this.findViewById(R.id.tvOutConfirm);

        tvOutCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                dismiss();
            }
        });
    }
}
