package ours.china.hours.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ours.china.hours.Adapter.NoteDisplayAdatper;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.Common.Interfaces.NoteItemInterface;
import ours.china.hours.Model.Book;
import ours.china.hours.R;
import ours.china.hours.Utility.MsgNoteItemClick;

public class NoteActivity extends AppCompatActivity implements NoteDisplayAdatper.OnCopyListiner, NoteItemInterface {
    private final String TAG = "NoteActivity";

    RecyclerView recyclerNoteView;
    NoteDisplayAdatper adapter;
    ArrayList<AppBookmark> objects;
    DocumentController dc;
    ImageView imgBack;
    RelativeLayout copyView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        dc = Global.globalDC;

        recyclerNoteView = findViewById(R.id.recyclerNote);

        copyView = findViewById(R.id.copyView);
        objects = Global.objects;
        adapter = new NoteDisplayAdatper(NoteActivity.this, objects, dc, NoteActivity.this, NoteActivity.this);
        recyclerNoteView.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
        recyclerNoteView.setAdapter(adapter);

//        objects.addAll(BookmarksData.get().getBookmarksByBook(dc.getCurrentBook()));
//        Log.i(TAG, "BookmarksData => " + objects);
        adapter.notifyDataSetChanged();

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onCopyNote() {
        copyView.setVisibility(View.VISIBLE);
        copyView.animate()
                .alpha(1.0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        copyView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onClickNoteItem(AppBookmark selectedNote, String pageNumber) {

        Gson gson = new Gson();
        Type type = new TypeToken<AppBookmark>() {
        }.getType();
        String json = gson.toJson(selectedNote, type) + "+" + pageNumber;
//
//        Log.i("NoteActivity", "pageNumber" + pageNumber);
//
//        EventBus.getDefault().post(new MsgNoteItemClick(json));

        Intent intent = new Intent();
        intent.setAction("note item click");
        intent.putExtra("message", json);
        sendBroadcast(intent);

        finish();
    }
}
