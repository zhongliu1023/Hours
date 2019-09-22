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

import ours.china.hours.Activity.Global;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.FavoriteDetailBook;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;

public class FavoritesDetailsAdatper extends RecyclerView.Adapter<FavoritesDetailsAdatper.FavoritesDetailsViewHolder> {

    public List<Book> bookList;
    public List<Book> selectedbookList;
    public Context context;

    public addActionListener listener;

    public FavoritesDetailsAdatper(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
        this.listener = (addActionListener) context;
        this.selectedbookList = new ArrayList<Book>();
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
        final Book one = bookList.get(position);

        holder.bookName.setText(one.bookName);
        if (!one.bookLocalUrl.isEmpty()) {
            holder.downloadStateImage.setImageResource(R.drawable.download);
            Glide.with(context)
                    .load(one.bookImageLocalUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        } else {
            holder.downloadStateImage.setVisibility(View.INVISIBLE);
            Glide.with(context)
                    .load(Url.domainUrl + "/" + one.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }

        if (!one.bookStatus.isRead.equals("1")) {
            holder.readStateImage.setImageResource(R.drawable.read);
        } else {
            holder.readStateImage.setVisibility(View.INVISIBLE);
        }

        holder.bookImage.setBackground(null);
        if (Global.bookAction == QueryBook.BookAction.SELECTTION){
            if (selectedbookList.contains(one)){
                holder.bookImage.setBackground(context.getResources().getDrawable(R.drawable.rect_book_yellow_stroke));
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickBookItem(one, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void reloadBookList(ArrayList<Book> updatedBookList){
        bookList = updatedBookList;
        notifyDataSetChanged();
    }
    public void reloadBookListWithSelection(ArrayList<Book> updatedBookList){
        selectedbookList = updatedBookList;
        notifyDataSetChanged();
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
        public void onClickBookItem(Book selectedBook, int position);
    }
}
