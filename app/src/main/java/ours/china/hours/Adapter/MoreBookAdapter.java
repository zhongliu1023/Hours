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

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Activity.BookDetailActivity;
import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.MoreBook;
import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;

public class MoreBookAdapter extends RecyclerView.Adapter<MoreBookAdapter.ViewHolder> {

    private Context context;
    private List<Book> moreBooks;
    private BookItemInterface bookItemInterface;

    public MoreBookAdapter(Context context, List<Book> moreBooks, BookItemInterface bookItemInterface) {
        this.context = context;
        this.moreBooks = moreBooks;
        this.bookItemInterface = bookItemInterface;
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

        if (one.bookStatus.isAttention.equals("已下载")) {
            holder.imgDownState.setVisibility(View.VISIBLE);
            holder.imgDownState.setImageResource(R.drawable.label2_icon);
        } else {
            holder.imgDownState.setVisibility(View.INVISIBLE);
        }

        if (one.bookStatus.isAttention.equals("已阅")) {
            holder.imgReadState.setVisibility(View.VISIBLE);
            holder.imgReadState.setImageResource(R.drawable.label1_icon);
        } else {
            holder.imgReadState.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(Url.domainUrl + "/" + one.coverUrl)
                .placeholder(R.drawable.book_image)
                .into(holder.bookImage);

        holder.bookName.setText(one.bookName);
        holder.bookAuthor.setText(one.author);
        holder.txtDownState.setText(one.bookStatus.isAttention);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookItemInterface.onClickBookItem(one, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moreBooks.size();
    }

    public void reloadBookList(ArrayList<Book> updatedBooks){
        moreBooks = updatedBooks;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImage;
        ImageView imgDownState;
        ImageView imgReadState;
        TextView bookName;
        TextView bookAuthor;
        TextView txtDownState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.item_book_image);
            imgDownState = itemView.findViewById(R.id.imgDownState);
            imgReadState = itemView.findViewById(R.id.imgReadState);
            bookName = itemView.findViewById(R.id.item_bookName);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            txtDownState = itemView.findViewById(R.id.txtDownState);

        }
    }
}
