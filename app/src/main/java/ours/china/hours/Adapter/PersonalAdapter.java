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

import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.HomeBookViewHolder> {

    public List<Book> bookList;
    public Context context;

    public PersonalAdapter(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new HomeBookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeBookViewHolder holder, int position) {
        Book one = bookList.get(position);

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
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class HomeBookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        ImageView downloadStateImage;
        ImageView readStateImage;
        TextView bookName;

        public HomeBookViewHolder(View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.item_book_image);
            downloadStateImage = itemView.findViewById(R.id.downState);
            readStateImage = itemView.findViewById(R.id.readState);
            bookName = itemView.findViewById(R.id.item_bookName);
        }
    }
}
