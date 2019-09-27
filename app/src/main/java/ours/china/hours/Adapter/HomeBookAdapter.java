package ours.china.hours.Adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Activity.Global;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Interfaces.BookItemEditInterface;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Interfaces.PageLoadInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;

public class HomeBookAdapter extends RecyclerView.Adapter<HomeBookAdapter.HomeBookViewHolder> {
    private static String TAG = "HomeBookAdapter";

    public List<Book> bookList;
    public List<Book> selectedbookList;
    BookItemInterface bookItemInterface;
    BookItemEditInterface bookItemEditInterface;
    PageLoadInterface pageLoadInterface;

    public Context context;
    public Activity activity;

    public HomeBookAdapter(List<Book> bookList, Context context, BookItemInterface bookItemInterface,
                           BookItemEditInterface bookItemEditInterface, PageLoadInterface pageLoadInterface) {
        this.bookList = bookList;
        this.context = context;
        this.activity = (Activity)context;
        this.bookItemInterface = bookItemInterface;
        this.bookItemEditInterface = bookItemEditInterface;
        this.pageLoadInterface = pageLoadInterface;

        selectedbookList = new ArrayList<>();
    }

    @NonNull
    @Override
    public HomeBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new HomeBookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeBookViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeBookViewHolder holder, int position) {
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

        if (getItemCount() == position + 1) {
            if (pageLoadInterface != null) {
                pageLoadInterface.scrollToLoad(getItemCount() / Global.perPage);
            }
        }



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
