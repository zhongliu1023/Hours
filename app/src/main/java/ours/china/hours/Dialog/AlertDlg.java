package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ours.china.hours.R;

public class AlertDlg extends Dialog {

    public Button btnOK;
    public TextView txtTitle;
    public TextView txtMessage;

    public AlertDlg(Context context) {
        super(context);
    }

    public AlertDlg(Context context, int themeResId) {
        super( context, themeResId );

        setContentView(R.layout.dialog_alert);

        btnOK = (Button)this.findViewById(R.id.btn_dlgOk);
        txtTitle = (TextView)this.findViewById(R.id.txt_dlgTitle);
        txtMessage = (TextView)this.findViewById(R.id.txt_dlgMessage);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
