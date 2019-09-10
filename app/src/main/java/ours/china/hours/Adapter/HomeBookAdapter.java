package ours.china.hours.Adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Environment;
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
import java.util.List;

import ours.china.hours.Activity.Global;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.ExtUtils;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class HomeBookAdapter extends RecyclerView.Adapter<HomeBookAdapter.HomeBookViewHolder> {

    public List<Book> bookList;
    BookItemInterface bookItemInterface;

    public Context context;
    public Activity activity;

    public HomeBookAdapter(List<Book> bookList, Context context, BookItemInterface bookItemInterface) {
        this.bookList = bookList;
        this.context = context;
        this.activity = (Activity)context;
        this.bookItemInterface = bookItemInterface;
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
        if (!one.getBookLocalUrl().equals("") && !one.getBookImageLocalUrl().equals("")) {

            // for downloaded book
            holder.downloadStateImage.setImageResource(R.drawable.download);
            Glide.with(context)
                    .load(one.getBookImageLocalUrl())
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        } else {

            // for undownloaded book
            holder.downloadStateImage.setVisibility(View.INVISIBLE);
            Glide.with(context)
                    .load(one.getBookImageUrl())
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }

        if (one.getReadState().equals(context.getString(R.string.state_read_complete))) {
            holder.readStateImage.setImageResource(R.drawable.read);
        } else {
            holder.readStateImage.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //example, change later
//                bookItemInterface.onClickBookItem("https://carlosicaza.com/swiftbooks/SwiftLanguage.pdf");
//                bookItemInterface.onClickBookItem("http://192.168.6.222/Hour/assets/upload/book/austen.epub");

                if (!one.getBookLocalUrl().equals("") && !one.getBookImageLocalUrl().equals("")) {
//                    displayLocalBook(position);

                    List<FileMeta> localBooks = AppDB.get().getAll();
                    ExtUtils.openFile(activity, localBooks.get(0));

                } else {
                    Global.bookID = String.valueOf(position);
                    bookItemInterface.onClickBookItem(one.getBookUrl());

                    Toast.makeText(context, one.getBookUrl(), Toast.LENGTH_SHORT).show();
                }

            }
        });
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

    public void displayLocalBook(int position) {
        Book one = bookList.get(position);

        FileMeta fileMeta = new FileMeta();
        fileMeta.setAuthor("zhong liu");
        fileMeta.setChild("epub");
        fileMeta.setDateTxt("9/7/19");
        fileMeta.setExt("epub");
        fileMeta.setGenre("test...");
        fileMeta.setIsRecent(true);
        fileMeta.setIsRecentProgress((float) 0.253);
        fileMeta.setIsRecentTime(System.currentTimeMillis());
        fileMeta.setIsSearchBook(true);
        fileMeta.setIsStar(false);
        fileMeta.setIsStarTime(System.currentTimeMillis());
        fileMeta.setIsbn(one.getBookLocalUrl());
        fileMeta.setLang("en");
        fileMeta.setPages(20);
        fileMeta.setPath(one.getBookLocalUrl());
        fileMeta.setPathTxt("test");
        fileMeta.setPublisher("");
        fileMeta.setSequence("");
        fileMeta.setSizeTxt("1770KB");
        fileMeta.setSize((long) (1770 * 1000));
        fileMeta.setState(2);
        fileMeta.setTitle("");
        fileMeta.setYear(2016);
        ExtUtils.openFile(activity, fileMeta);
    }

}
