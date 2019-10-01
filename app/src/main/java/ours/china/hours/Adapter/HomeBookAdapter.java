package ours.china.hours.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Activity.Global;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.sys.ImageExtractor;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.nostra13.universalimageloader.core.ImageLoader;
import ours.china.hours.Common.Interfaces.BookItemEditInterface;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Interfaces.PageLoadInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;
import ours.china.hours.Services.BookFile;

public class HomeBookAdapter extends RecyclerView.Adapter<BookViewAdapterHolder> {
    private static String TAG = "HomeBookAdapter";

    public List<Book> bookList;
    public List<Book> selectedbookList;
    public Map<String, BookFile> mBookFiles;
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
        this.mBookFiles = new HashMap<String, BookFile>();
    }

    @NonNull
    @Override
    public BookViewAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new BookViewAdapterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewAdapterHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewAdapterHolder holder, int position) {
        Book one = bookList.get(position);

        holder.bookName.setText(one.bookName);

        if (mBookFiles.containsKey(one.bookId)){
            BookFile bookFile = mBookFiles.get(one.bookId);
            holder.progressBar.setVisibility(View.VISIBLE);
            if (bookFile.getIsDownloaded()){
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.downloadStateImage.setVisibility(View.VISIBLE);
            }else{
                holder.progressBar.setProgress(bookFile.getProgress());
            }
        }else{
            holder.progressBar.setVisibility(View.INVISIBLE);
        }

        if (!one.bookLocalUrl.equals("")) {
            // for downloaded book
            holder.downloadStateImage.setVisibility(View.VISIBLE);
            holder.downloadStateImage.setImageResource(R.drawable.download);

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
                if (!mBookFiles.containsKey(one.bookId)){
                    bookItemInterface.onClickBookItem(one, position);
                }else{
                    Toast.makeText(context, "下载...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!mBookFiles.containsKey(one.bookId)){
                    bookItemEditInterface.onLongClickBookItem(one);
                }else{
                    Toast.makeText(context, "下载...", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        if (getItemCount() >= Global.perPage && getItemCount() == position + 1) {
            if (pageLoadInterface != null) {
                pageLoadInterface.scrollToLoad(position + 1);
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

    public void reloadbookwithDownloadStatus(Map<String, BookFile> updatedBookFiles){
        mBookFiles = updatedBookFiles;
        for (int i = 0 ; i < bookList.size(); i ++){
            Book book = bookList.get(i);
            if (mBookFiles.containsKey(book.bookId)){
                notifyItemChanged(i);
            }
        }
    }

}
