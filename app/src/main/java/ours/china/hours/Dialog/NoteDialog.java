package ours.china.hours.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ours.china.hours.R;
import ours.china.hours.Utility.AlertAddFavorites;

public class NoteDialog  extends Dialog {

    RelativeLayout relCancel, relConfirm;
    EditText edtStr;
    TextView alertContent;
    OnAddNoteListener listener;

    String tempStr;
    String tmpNote;

    public NoteDialog(@NonNull Context context, int themeResId, String content, String note, OnAddNoteListener listener) {
        super(context, themeResId);
        this.listener = listener;
        this.tmpNote = note;

        tempStr = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_note);

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
        edtStr.setText(tmpNote);
        relConfirm = findViewById(R.id.relConfirm);
        relConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempString = edtStr.getText().toString();
                listener.addNote(tempString);

                edtStr.setText("");
                dismiss();
            }
        });
    }

    public interface OnAddNoteListener {
        void addNote(String str);
    }
    public void setEditText(String str){
        edtStr.setText(str);
    }
}

