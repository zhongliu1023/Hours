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

import java.util.List;

import ours.china.hours.Activity.BookDetailActivity;
import ours.china.hours.Model.MoreBook;
import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;

public class MoreBookAdapter extends RecyclerView.Adapter<MoreBookAdapter.ViewHolder> {

    private Context context;
    private List<MoreBook> moreBooks;

    public MoreBookAdapter(Context context, List<MoreBook> moreBooks) {
        this.context = context;
        this.moreBooks = moreBooks;
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
        final MoreBook one = moreBooks.get(position);

        if (one.getDownState().equals("已下载")) {
            holder.imgDownState.setImageResource(R.drawable.label2_icon);
        } else {
            holder.imgDownState.setVisibility(View.INVISIBLE);
        }

        if (one.getReadState().equals("已阅")) {
            holder.imgReadState.setImageResource(R.drawable.label1_icon);
        } else {
            holder.imgReadState.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(one.getBookImageUrl())
                .placeholder(R.drawable.book_image)
                .into(holder.bookImage);

        holder.bookName.setText(one.getBookName());
        holder.bookAuthor.setText(one.getBookAuthor());
        holder.txtDownState.setText(one.getBookAuthor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!one.getDownState().equals("已下载")) {
                    Intent intent = new Intent(context, BookDetailActivity.class);

                    // here include book information.
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return moreBooks.size();
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
