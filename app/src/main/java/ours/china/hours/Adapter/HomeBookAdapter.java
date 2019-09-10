package ours.china.hours.Adapter;

import android.content.Context;
import android.media.Image;
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

import java.util.List;

import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class HomeBookAdapter extends RecyclerView.Adapter<HomeBookAdapter.HomeBookViewHolder> {

    public List<Book> bookList;
    public Context context;
    BookItemInterface bookItemInterface;

    public HomeBookAdapter(List<Book> bookList, Context context, BookItemInterface bookItemInterface) {
        this.bookList = bookList;
        this.context = context;
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

        Glide.with(context)
                .load(one.getBookImage())
                .placeholder(R.drawable.book_image)
                .into(holder.bookImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //example, change later
//                bookItemInterface.onClickBookItem("https://carlosicaza.com/swiftbooks/SwiftLanguage.pdf");
//                bookItemInterface.onClickBookItem("http://192.168.6.222/Hour/assets/upload/book/austen.epub");
                bookItemInterface.onClickBookItem("http://www.jedisaber.com/eBooks/books/sample.epub");


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

}
