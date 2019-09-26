package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Common.Interfaces.BookItemInterface;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;

public class MyShelfBookAdapter extends RecyclerView.Adapter<MyShelfBookAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Book> myShelfBooks;
    private List<Book> myShelfBooksFiltered;
    private BookItemInterface bookListener;

    public MyShelfBookAdapter(Context context, List<Book> myShelfBooks, BookItemInterface bookListenenr) {
        this.context = context;
        this.myShelfBooks = myShelfBooks;
        this.myShelfBooksFiltered = myShelfBooks;
        this.bookListener = bookListenenr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.book_myshelf_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Book one = myShelfBooksFiltered.get(position);

        holder.bookName.setText(one.bookName);
        if (!one.bookImageLocalUrl.equals("") && !one.bookLocalUrl.equals("")) {

            holder.imgDownState.setVisibility(View.VISIBLE);
            holder.imgDownState.setImageResource(R.drawable.download);
            holder.txtDownState.setText("已下载");
            Glide.with(context)
                    .load(one.bookImageLocalUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        } else {
            holder.imgDownState.setVisibility(View.INVISIBLE);
            holder.txtDownState.setText("未下载");
            Glide.with(context)
                    .load(Url.domainUrl + "/" + one.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }

        if (one.bookStatus.isRead.equals("1")) {
            holder.imgReadState.setVisibility(View.VISIBLE);
            holder.imgReadState.setImageResource(R.drawable.read);
        } else {
            holder.imgReadState.setVisibility(View.INVISIBLE);
        }

        holder.bookAuthor.setText(one.author);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookListener.onClickBookItem(one, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myShelfBooksFiltered.size();
    }

    public void reloadBookList(ArrayList<Book> updatedBooks, String keyword){
        myShelfBooks = updatedBooks;
        MyShelfBookAdapter.this.getFilter().filter(keyword);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchString = charSequence.toString();
                if (searchString.isEmpty()) {
                    myShelfBooksFiltered = myShelfBooks;
                } else {
                    List<Book> filteredList = new ArrayList<>();
                    for (Book one : myShelfBooks) {
                        if (one.bookName.toLowerCase().contains(searchString.toLowerCase())
                                || one.author.toLowerCase().contains(searchString.toLowerCase())
                                || one.publishingHouse.toLowerCase().contains(searchString.toLowerCase())) {
                            filteredList.add(one);
                        }
                    }

                    myShelfBooksFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = myShelfBooksFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                myShelfBooksFiltered = (ArrayList<Book>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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
