package ours.china.hours.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ours.china.hours.Activity.BookDetailActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.BookLib.foobnix.dao2.FileMeta;
import ours.china.hours.BookLib.foobnix.pdf.info.IMG;
import ours.china.hours.BookLib.foobnix.sys.ImageExtractor;
import ours.china.hours.BookLib.foobnix.ui2.AppDB;
import ours.china.hours.BookLib.nostra13.universalimageloader.core.ImageLoader;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Common.Interfaces.PageLoadInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.MoreBook;
import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;
import ours.china.hours.Services.BookFile;

public class MoreBookAdapter extends RecyclerView.Adapter<MoreBookAdapter.ViewHolder> {

    private Context context;
    private List<Book> moreBooks;
    public Map<String, BookFile> mBookFiles;
    private BookItemInterface bookItemInterface;
    PageLoadInterface pageLoadInterface;

    public MoreBookAdapter(Context context, List<Book> moreBooks, BookItemInterface bookItemInterface, PageLoadInterface pageLoadInterface) {
        this.context = context;
        this.moreBooks = moreBooks;
        this.bookItemInterface = bookItemInterface;
        this.pageLoadInterface = pageLoadInterface;

        this.mBookFiles = new HashMap<String, BookFile>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_more_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Book one = moreBooks.get(position);

        holder.bookName.setText(one.bookName);
        if (!one.bookLocalUrl.equals("")) {

            // for downloaded book
            holder.downloadStateImage.setVisibility(View.VISIBLE);
            holder.downloadStateImage.setImageResource(R.drawable.download);
            holder.txtDownState.setText("已下载");
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

            // for undownloaded book
            holder.downloadStateImage.setVisibility(View.INVISIBLE);
            holder.txtDownState.setText("未下载");
            Glide.with(context)
                    .load(Url.domainUrl + "/" + one.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }

        if (mBookFiles.containsKey(one.bookId)){
            BookFile bookFile = mBookFiles.get(one.bookId);
            holder.progressBar.setVisibility(View.VISIBLE);
            if (bookFile.getIsDownloaded()){
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.downloadStateImage.setVisibility(View.VISIBLE);
            }else{
                holder.progressBar.setProgress(bookFile.getProgress());
                holder.txtDownState.setText("下载中");
            }
        }else{
            holder.progressBar.setVisibility(View.INVISIBLE);
        }


        if (one.bookStatus != null && one.bookStatus.isRead.equals("1")) {
            holder.readStateImage.setVisibility(View.VISIBLE);
            holder.readStateImage.setImageResource(R.drawable.read);
        } else {
            holder.readStateImage.setVisibility(View.INVISIBLE);
        }

        holder.bookAuthor.setText(one.author);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookItemInterface.onClickBookItem(one, position);
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
        return moreBooks.size();
    }

    public void reloadBookList(ArrayList<Book> updatedBooks){
        moreBooks = updatedBooks;
        notifyDataSetChanged();
    }

    public void reloadbookwithDownloadStatus(Map<String, BookFile> updatedBookFiles){
        mBookFiles = updatedBookFiles;
        for (int i = 0 ; i < moreBooks.size(); i ++){
            Book book = moreBooks.get(i);
            if (mBookFiles.containsKey(book.bookId)){
                notifyItemChanged(i);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImage;
        ImageView downloadStateImage;
        ImageView readStateImage;
        TextView bookName;
        TextView bookAuthor;
        TextView txtDownState;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.item_book_image);
            downloadStateImage = itemView.findViewById(R.id.imgDownState);
            readStateImage = itemView.findViewById(R.id.imgReadState);
            bookName = itemView.findViewById(R.id.item_bookName);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            txtDownState = itemView.findViewById(R.id.txtDownState);
            progressBar = itemView.findViewById(R.id.progressbar);

        }
    }
}
