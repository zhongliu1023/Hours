package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.QueryBook;
import ours.china.hours.R;
import ours.china.hours.Services.BookFile;

public class AttentionBookAdapter extends RecyclerView.Adapter<BookViewAdapterHolder> {
    private final String TAG = "AttentionBookAdapter";

    public List<Book> bookList;
    public List<Book> selectedbookList;
    public Map<String, BookFile> mBookFiles;
    public Context context;
    BookItemInterface bookItemInterface;
    BookItemEditInterface bookItemEditInterface;

    public AttentionBookAdapter(List<Book> bookList, Context context, BookItemInterface bookItemInterface,  BookItemEditInterface bookItemEditInterface) {
        this.bookList = bookList;
        this.context = context;
        this.bookItemInterface = bookItemInterface;
        this.bookItemEditInterface = bookItemEditInterface;

        this.selectedbookList = new ArrayList<Book>();
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
