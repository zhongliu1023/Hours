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

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class ReadingCompleteStatusBookAdapter extends RecyclerView.Adapter<ReadingCompleteStatusBookAdapter.ReadingStatusBookViewHolder> {

    private List<Book> bookList;
    public Context context;

    public ReadingCompleteStatusBookAdapter(Context context, ArrayList<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ReadingStatusBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_book_reading_complete_item, parent, false);
        return new ReadingStatusBookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadingStatusBookViewHolder holder, int position) {
        Book one = bookList.get(position);

        holder.bookName.setText(one.bookName);
        holder.txtReadTime.setText(one.bookStatus.time);
        holder.txtSpecifiedTime.setText(one.demandTime);

        Glide.with(context)
                .load(one.coverUrl)
                .placeholder(R.drawable.book_image)
                .into(holder.bookImage);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ReadingStatusBookViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImage;
        TextView bookName;
        TextView txtReadTime;
        TextView txtSpecifiedTime;

        public ReadingStatusBookViewHolder(View view) {
            super(view);

            bookImage = view.findViewById(R.id.bookImage);
            bookName = view.findViewById(R.id.bookName);
            txtReadTime = view.findViewById(R.id.txtReadingTime);
            txtSpecifiedTime = view.findViewById(R.id.txtSpecifiedTime);

        }
    }
}
