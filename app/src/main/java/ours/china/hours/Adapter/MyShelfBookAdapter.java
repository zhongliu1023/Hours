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

import java.util.List;

import ours.china.hours.Model.MyShelfBook;
import ours.china.hours.R;

public class MyShelfBookAdapter extends RecyclerView.Adapter<MyShelfBookAdapter.ViewHolder> {

    private Context context;
    private List<MyShelfBook> myShelfBooks;

    public MyShelfBookAdapter(Context context, List<MyShelfBook> myShelfBooks) {
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
        MyShelfBook one = myShelfBooks.get(position);

        if (one.getDownState().equals("downloaded")) {
            holder.downStateMark.setImageResource(R.drawable.download);
        } else {
            holder.downStateMark.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(one.getBookImageUrl())
                .placeholder(R.drawable.book_image)
                .into(holder.bookImage);

        holder.bookName.setText(one.getBookName());
        holder.downState.setText(one.getDownState());
        holder.bookAuthor.setText(one.getBookAuthor());
    }

    @Override
    public int getItemCount() {
        return myShelfBooks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView bookImage;
        public ImageView downStateMark;
        public TextView bookName;
        public TextView downState;
        public TextView bookAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bookImage = itemView.findViewById(R.id.item_book_image);
            bookName = itemView.findViewById(R.id.bookName);
            downState = itemView.findViewById(R.id.downState);
            downStateMark = itemView.findViewById(R.id.downStateMark);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
        }
    }
}
