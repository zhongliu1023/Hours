package ours.china.hours.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ours.china.hours.Activity.BookDetailActivity;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class LibraryBookAdapter extends RecyclerView.Adapter<LibraryBookAdapter.ViewHolder> {

    private Context context;
    private List<Book> bookList;

    public LibraryBookAdapter(Context context, List<Book> mLibraryBooks) {
        this.context = context;
        this.bookList = mLibraryBooks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_library_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book one = bookList.get(position);

        holder.bookName.setText(one.getBookName());
        if (one.getDownloadState().equals("已下载")) {
            holder.downloadStateImage.setImageResource(R.drawable.download);
        } else {
            holder.downloadStateImage.setVisibility(View.INVISIBLE);
        }

        if (one.getReadState().equals("已阅")) {
            holder.readStateImage.setImageResource(R.drawable.read);
        } else {
            holder.readStateImage.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(one.getBookImage())
                .placeholder(R.drawable.book_image)
                .into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImage;
        ImageView downloadStateImage;
        ImageView readStateImage;
        TextView bookName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.item_book_image);
            downloadStateImage = itemView.findViewById(R.id.downState);
            readStateImage = itemView.findViewById(R.id.readState);
            bookName = itemView.findViewById(R.id.item_bookName);
        }
    }
}
