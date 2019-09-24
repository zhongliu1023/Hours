package ours.china.hours.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.BookLib.foobnix.android.utils.TxtUtils;
import ours.china.hours.BookLib.foobnix.model.AppBook;
import ours.china.hours.BookLib.foobnix.model.AppBookmark;
import ours.china.hours.BookLib.foobnix.pdf.info.OutlineHelper;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.R;

public class PageBookmarkAdapter extends RecyclerView.Adapter<PageBookmarkAdapter.PageBookmarkViewHolder> {
    private final String TAG = "PageBookmarkAdapter";

    ArrayList<AppBookmark> objects;
    private Context context;
    private DocumentController controller;
    OnClickBookItemLintener onClickBookItemLintener;

    public PageBookmarkAdapter(Context context, ArrayList<AppBookmark> objects, DocumentController controller, OnClickBookItemLintener listener) {
        this.objects = objects;
        this.context = context;
        this.controller = controller;
        this.onClickBookItemLintener = listener;

        Log.i(TAG, "constructor");
    }

    @NonNull
    @Override
    public PageBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pagebookmark, parent, false);

        Log.i(TAG, "onCreateViewHolder");
        return new PageBookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageBookmarkViewHolder holder, int position) {
        AppBookmark one = objects.get(position);
        Log.i(TAG, "onBindViewHolder");

        String pageNumber = TxtUtils.deltaPage(one.getPage(controller.getPageCount()));
        OutlineHelper.Info info = OutlineHelper.getForamtingInfo(controller, false);
        String totalPageCount = info.textPage;

        Log.i(TAG, "pageNumber => " + pageNumber + "totalPageCount => " + totalPageCount);
        holder.bookmark_subtitle.setText(one.getSubTitle());
        holder.bookmark_page_number.setText(context.getString(R.string.note_page, pageNumber, totalPageCount));
        holder.bookmark_content.setText(one.text);
        holder.bookmarkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBookItemLintener.selectedBookItem(pageNumber);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class PageBookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView bookmark_subtitle;
        TextView bookmark_page_number;
        ImageView bookmark_icon;
        TextView bookmark_content;
        LinearLayout bookmarkLayout;

        public PageBookmarkViewHolder(@NonNull View itemView) {
            super(itemView);

            bookmark_subtitle = itemView.findViewById(R.id.catalog_bookmark_subtitle);
            bookmark_page_number = itemView.findViewById(R.id.catalog_bookmark_page_number);
            bookmark_icon = itemView.findViewById(R.id.catalog_bookmark_icon);
            bookmark_content = itemView.findViewById(R.id.catalog_bookmark_content);
            bookmarkLayout = itemView.findViewById(R.id.bookmarkLayout);
        }
    }

    public interface OnClickBookItemLintener{
        void selectedBookItem(String pageNumber);
    }
}
