package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import ours.china.hours.R;

public class OutDlg extends Dialog {

    private TextView tvOutConfirm, tvOutCancel;


    public OutDlg(Context context) {
        super(context);
    }


    public OutDlg(Context context, int themeResId) {
        super( context, themeResId );

        setContentView(R.layout.dialog_out);

        tvOutCancel = (TextView)this.findViewById(R.id.tvOutCancel);
        tvOutConfirm = (TextView)this.findViewById(R.id.tvOutConfirm);

    }
}
