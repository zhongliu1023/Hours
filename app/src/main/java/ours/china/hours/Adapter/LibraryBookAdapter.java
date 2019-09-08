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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.List;

import ours.china.hours.Activity.BookDetailActivity;
import ours.china.hours.Model.LibraryBook;
import ours.china.hours.R;

public class LibraryBookAdapter extends RecyclerView.Adapter<LibraryBookAdapter.ViewHolder> {

    private Context context;
    private List<LibraryBook> mLibraryBooks;

    public LibraryBookAdapter(Context context, List<LibraryBook> mLibraryBooks) {
        this.context = context;
        this.mLibraryBooks = mLibraryBooks;
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
        LibraryBook one = mLibraryBooks.get(position);

        holder.bookName.setText(one.getBookName());
        Glide.with(context)
                .load(one.getBookImageUrl())
                .placeholder(R.drawable.book_image)
                .into(holder.bookImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLibraryBooks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView bookImage;
        public TextView bookName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.item_book_image);
            bookName = itemView.findViewById(R.id.item_bookName);
        }
    }
}
