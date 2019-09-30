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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Activity.BookDetailActivity;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.sys.ImageExtractor;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.nostra13.universalimageloader.core.ImageLoader;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class LibraryBookAdapter extends RecyclerView.Adapter<LibraryBookAdapter.ViewHolder> {

    private Context context;
    private List<Book> bookList;
    private BookItemInterface bookItemInterface;

    public LibraryBookAdapter(Context context, List<Book> mLibraryBooks, BookItemInterface bookItemInterface) {
        this.context = context;
        this.bookList = mLibraryBooks;
        this.bookItemInterface = bookItemInterface;
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

        holder.bookName.setText(one.bookName);
        if (!one.bookImageLocalUrl.equals("") && !one.bookLocalUrl.equals("")) {

            holder.downloadStateImage.setVisibility(View.VISIBLE);
            holder.downloadStateImage.setImageResource(R.drawable.download);
//            Glide.with(context)
//                    .load(one.bookImageLocalUrl)
//                    .placeholder(R.drawable.book_image)
//                    .into(holder.bookImage);

            // in case of dump image.
            int tempLibraryPosition = Integer.parseInt(one.libraryPosition);
            FileMeta meta = AppDB.get().getAll().get(tempLibraryPosition);
            String url = IMG.toUrl(meta.getPath(), ImageExtractor.COVER_PAGE, ViewGroup.LayoutParams.MATCH_PARENT);
            ImageLoader.getInstance().displayImage(url, holder.bookImage, IMG.displayCacheMemoryDisc, null);
        } else {
            holder.downloadStateImage.setVisibility(View.INVISIBLE);
            Glide.with(context)
                    .load(Url.domainUrl + "/" + one.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }

        if (one.bookStatus.isRead.equals("1")) {
            holder.readStateImage.setVisibility(View.VISIBLE);
            holder.readStateImage.setImageResource(R.drawable.read);
        } else {
            holder.readStateImage.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookItemInterface.onClickBookItem(one, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void reloadBookList(ArrayList<Book> updatedBooks){
        bookList = updatedBooks;
        notifyDataSetChanged();
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
