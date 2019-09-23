package ours.china.hours.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Adapter.NoteDisplayAdatper;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.R;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = "NoteActivity";

    RecyclerView recyclerNoteView;
    NoteDisplayAdatper adapter;
    ArrayList<AppBookmark> objects;
    DocumentController dc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        dc = Global.globalDC;

        recyclerNoteView = findViewById(R.id.recyclerNote);

        objects = Global.objects;
        adapter = new NoteDisplayAdatper(NoteActivity.this, objects, dc);
        recyclerNoteView.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
        recyclerNoteView.setAdapter(adapter);

//        objects.addAll(BookmarksData.get().getBookmarksByBook(dc.getCurrentBook()));
//        Log.i(TAG, "BookmarksData => " + objects);
        adapter.notifyDataSetChanged();
    }
}
