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
import ours.china.hours.Common.Interfaces.BookItemEditInterface;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;

public class AttentionBookAdapter extends RecyclerView.Adapter<BookViewAdapterHolder> {
    private final String TAG = "AttentionBookAdapter";

    public List<Book> bookList;
    public List<Book> selectedbookList;
    public Context context;
    BookItemInterface bookItemInterface;
    BookItemEditInterface bookItemEditInterface;

    public AttentionBookAdapter(List<Book> bookList, Context context, BookItemInterface bookItemInterface,  BookItemEditInterface bookItemEditInterface) {
        this.bookList = bookList;
        this.context = context;
        this.bookItemInterface = bookItemInterface;
        this.bookItemEditInterface = bookItemEditInterface;

        this.selectedbookList = new ArrayList<Book>();
    }

    @NonNull
    @Override
    public BookViewAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new BookViewAdapterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewAdapterHolder holder, int position) {
        Book one = bookList.get(position);

        holder.bookName.setText(one.bookName);
        if (!one.bookLocalUrl.equals("") && !one.bookImageLocalUrl.equals("")) {

            // for downloaded book
            holder.downloadStateImage.setVisibility(View.VISIBLE);
            holder.downloadStateImage.setImageResource(R.drawable.download);
            Glide.with(context)
                    .load(one.bookImageLocalUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        } else {

            // for undownloaded book
            holder.downloadStateImage.setVisibility(View.INVISIBLE);
            Glide.with(context)
                    .load(Url.domainUrl + "/" + one.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }

        if (one.bookStatus != null && one.bookStatus.isRead.equals("1")) {
            holder.readStateImage.setVisibility(View.VISIBLE);
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
                bookItemInterface.onClickBookItem(one, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                bookItemEditInterface.onLongClickBookItem(one);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void reloadBookList(List<Book> updatedBooklist){
        bookList = updatedBooklist;
        notifyDataSetChanged();
    }

    public void reloadBookListWithSelection(ArrayList<Book> updatedBookList){
        selectedbookList = updatedBookList;
        notifyDataSetChanged();
    }
}
