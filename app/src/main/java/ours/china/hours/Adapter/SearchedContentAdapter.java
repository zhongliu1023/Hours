package ours.china.hours.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import ours.china.hours.BookLib.foobnix.pdf.info.wrapper.DocumentController;
import ours.china.hours.Model.SearchContent;
import ours.china.hours.R;

public class SearchedContentAdapter extends RecyclerView.Adapter<SearchedContentAdapter.SearchedContentViewHolder> {
    private final String TAG = "PageBookmarkAdapter";

    ArrayList<SearchContent> objects;
    private Context context;
    private DocumentController controller;
    OnClickSearchedContent onClickSearchedContent;

    public SearchedContentAdapter(Context context, ArrayList<SearchContent> objects, final DocumentController controller, OnClickSearchedContent listener) {
        this.objects = objects;
        this.context = context;
        this.controller = controller;
        this.onClickSearchedContent = listener;
    }

    @NonNull
    @Override
    public SearchedContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content_searched, parent, false);
        return new SearchedContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedContentViewHolder holder, int position) {
        SearchContent one = objects.get(position);
        holder.titleTxt.setText(one.title);
        holder.contentText.setText(one.content);
        holder.pageTxt.setText(Integer.toString(one.currentPage));
        holder.searchedContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSearchedContent.onClickContent(one.currentPage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void reloadContents(ArrayList<SearchContent> updatedContents){
        objects = updatedContents;
        notifyDataSetChanged();
    }

    class SearchedContentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        TextView contentText;
        TextView pageTxt;
        LinearLayout searchedContentLayout;

        SearchedContentViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleText);
            contentText = itemView.findViewById(R.id.contentText);
            pageTxt = itemView.findViewById(R.id.pageTxt);
            searchedContentLayout = itemView.findViewById(R.id.searchedContentLayout);
        }
    }

    public interface OnClickSearchedContent{
        void onClickContent(int pageNumber);
    }
}

