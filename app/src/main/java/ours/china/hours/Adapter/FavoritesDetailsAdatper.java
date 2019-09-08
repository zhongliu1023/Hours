package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ours.china.hours.Activity.Global;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.FavoriteDetailBook;
import ours.china.hours.R;

public class FavoritesDetailsAdatper extends RecyclerView.Adapter<FavoritesDetailsAdatper.FavoritesDetailsViewHolder> {

    public List<FavoriteDetailBook> bookList;
    public Context context;

    public addActionListener listener;

    public FavoritesDetailsAdatper(List<FavoriteDetailBook> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
        this.listener = (addActionListener) context;
    }

    @NonNull
    @Override
    public FavoritesDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new FavoritesDetailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoritesDetailsViewHolder holder, final int position) {
        final FavoriteDetailBook one = bookList.get(position);

        holder.bookName.setText(one.getBookName());
        if (one.getDownloadState().equals("downloaded")) {
            holder.downloadStateImage.setImageResource(R.drawable.download);
        } else {
            holder.downloadStateImage.setVisibility(View.INVISIBLE);
        }

        if (one.getReadState().equals("reading")) {
            holder.readStateImage.setImageResource(R.drawable.read);
        } else {
            holder.readStateImage.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(one.getBookImage())
                .placeholder(R.drawable.book_image)
                .into(holder.bookImage);

        if (one.getStateClick().equals("noClicked")) {
            holder.bookImage.setBackground(null);
        } else if (one.getStateClick().equals("clicked")) {
            holder.bookImage.setBackground(context.getResources().getDrawable(R.drawable.rect_book_yellow_stroke));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.editStateOfFavoritesDetails.equals("yes")) {

                    if (one.getStateClick().equals("noClicked")) {
                        holder.bookImage.setBackground(context.getResources().getDrawable(R.drawable.rect_book_yellow_stroke));
                        one.setStateClick("clicked");

                    } else if (one.getStateClick().equals("clicked")) {
                        holder.bookImage.setBackground(null);
                        one.setStateClick("noClicked");
                    }

                    listener.enableComponentAction();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class FavoritesDetailsViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        ImageView downloadStateImage;
        ImageView readStateImage;
        TextView bookName;

        public FavoritesDetailsViewHolder(View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.item_book_image);
            downloadStateImage = itemView.findViewById(R.id.downState);
            readStateImage = itemView.findViewById(R.id.readState);
            bookName = itemView.findViewById(R.id.item_bookName);
        }
    }

    public interface addActionListener {
        public void enableComponentAction();
    }
}
