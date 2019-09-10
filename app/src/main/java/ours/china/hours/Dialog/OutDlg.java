package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import ours.china.hours.R;

public class OutDlg extends Dialog {

    public TextView tvOutConfirm, tvOutCancel, tvOutTitle;
    public Button btnOk;


    public OutDlg(Context context) {
        super(context);

        setContentView(R.layout.dialog_out);

        tvOutCancel = (TextView)this.findViewById(R.id.tvOutCancel);
        tvOutConfirm = (TextView)this.findViewById(R.id.tvOutConfirm);
        tvOutTitle = (TextView)this.findViewById(R.id.tvOutTitle);

        tvOutCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                dismiss();
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
