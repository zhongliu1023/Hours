package ours.china.hours.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.pdf.info.OutlineHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.R;

public class NoteDisplayAdatper extends RecyclerView.Adapter<NoteDisplayAdatper.NoteViewHolder> {
    private static String TAG = "NoteDisplayAdapter";

    private Context context;
    private ArrayList<AppBookmark> objects;

    private DocumentController controller;

    public NoteDisplayAdatper(Context context, ArrayList<AppBookmark> objects, DocumentController controller) {
        this.context = context;
        this.objects = objects;
        this.controller = controller;
//        this.onRefresh = onRefresh;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        AppBookmark one = objects.get(position);

        String pageNumber = TxtUtils.deltaPage(one.getPage(controller.getPageCount()));
        OutlineHelper.Info info = OutlineHelper.getForamtingInfo(controller, false);
        String totalPageCount = info.textPage;

        Log.i(TAG, "pageNumber => " + pageNumber + "totalPageCount => " + totalPageCount);
        holder.txtPageNumber.setText(context.getString(R.string.note_page, pageNumber, totalPageCount));
        holder.txtNoteContent.setText(one.text);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        ImageView imgNoteColorIcon;
        ImageView imgNoteAddOrUpdate;
        ImageView imgNoteMore;
        TextView txtNoteContent;
        TextView txtNoteAddOrUpdate;
        TextView txtPageNumber;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            imgNoteColorIcon = itemView.findViewById(R.id.imgNoteColorIcon);
            imgNoteAddOrUpdate = itemView.findViewById(R.id.imgNoteAddOrUpdate);
            imgNoteMore = itemView.findViewById(R.id.imgNoteMore);
            txtNoteContent = itemView.findViewById(R.id.txtNoteContent);
            txtNoteAddOrUpdate = itemView.findViewById(R.id.txtNoteAddOrUpdate);
            txtPageNumber = itemView.findViewById(R.id.txtPageNumber);
        }
    }
}
