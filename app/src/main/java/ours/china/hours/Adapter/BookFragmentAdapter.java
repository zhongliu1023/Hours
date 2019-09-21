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

import java.util.List;

import ours.china.hours.Activity.Global;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.FragmentBookModel;
import ours.china.hours.R;

public class BookFragmentAdapter extends RecyclerView.Adapter<BookFragmentAdapter.BookFragmentViewHolder> {

    public List<FragmentBookModel> bookList;
    public Context context;

    public BookFragmentAdapter(List<FragmentBookModel> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_fragment_item, parent, false);
        return new BookFragmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookFragmentViewHolder holder, int position) {
        final FragmentBookModel parent = bookList.get(position);
        final Book one = bookList.get(position).getBook();

        holder.bookName.setText(one.bookName);
        if (!one.bookLocalUrl.equals("") && !one.bookImageLocalUrl.equals("")) {

            // for downloaded book
            holder.downloadStateImage.setImageResource(R.drawable.download);
            Glide.with(context)
                    .load(one.bookImageLocalUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        } else {

            // for undownloaded book
            holder.downloadStateImage.setVisibility(View.INVISIBLE);
            Glide.with(context)
                    .load(one.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }

        if (one.bookStatus.isRead.equals(context.getString(R.string.state_read_complete))) {
            holder.readStateImage.setImageResource(R.drawable.read);
        } else {
            holder.readStateImage.setVisibility(View.INVISIBLE);
        }

        // select event
        if (parent.getSelectedState().equals("noClicked")) {
            holder.bookImage.setBackground(null);
        } else if (parent.getSelectedState().equals("clicked")) {
            holder.bookImage.setBackground(context.getResources().getDrawable(R.drawable.rect_book_yellow_stroke));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.editStateOfFavoritesDetails.equals("yes")) {

                    if (parent.getSelectedState().equals("noClicked")) {
                        holder.bookImage.setBackground(context.getResources().getDrawable(R.drawable.rect_book_yellow_stroke));
                        parent.setSelectedState("clicked");

                    } else if (parent.getSelectedState().equals("clicked")) {
                        holder.bookImage.setBackground(null);
                        parent.setSelectedState("noClicked");
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookFragmentViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        ImageView downloadStateImage;
        ImageView readStateImage;
        TextView bookName;

        public BookFragmentViewHolder(View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.item_book_image);
            downloadStateImage = itemView.findViewById(R.id.downState);
            readStateImage = itemView.findViewById(R.id.readState);
            bookName = itemView.findViewById(R.id.item_bookName);
        }
    }

}
