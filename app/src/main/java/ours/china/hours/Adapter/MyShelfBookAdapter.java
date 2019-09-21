package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;

public class MyShelfBookAdapter extends RecyclerView.Adapter<MyShelfBookAdapter.ViewHolder> {

    private Context context;
    private List<Book> myShelfBooks;

    public MyShelfBookAdapter(Context context, List<Book> myShelfBooks) {
        this.context = context;
        this.myShelfBooks = myShelfBooks;
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

        Book one = myShelfBooks.get(position);

        holder.bookName.setText(one.bookName);
        if (!one.bookImageLocalUrl.equals("") && !one.bookLocalUrl.equals("")) {

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

        if (one.bookStatus.isRead.equals(context.getString(R.string.state_read_complete))) {
            holder.imgReadState.setImageResource(R.drawable.read);
        } else {
            holder.imgReadState.setVisibility(View.INVISIBLE);
        }

        holder.bookAuthor.setText(one.author);

    }

    @Override
    public int getItemCount() {
        return myShelfBooks.size();
    }

    public void reloadBookList(ArrayList<Book> updatedBooks){
        myShelfBooks = updatedBooks;
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
