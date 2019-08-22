package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Model.ReadingStatusBook;
import ours.china.hours.R;

public class ReadingStatusBookAdapter extends RecyclerView.Adapter<ReadingStatusBookAdapter.ReadingStatusBookViewHolder> {

    private List<ReadingStatusBook> bookList;
    public Context context;

    public ReadingStatusBookAdapter(Context context, ArrayList<ReadingStatusBook> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ReadingStatusBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_book_item, parent, false);
        return new ReadingStatusBookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadingStatusBookViewHolder holder, int position) {
        ReadingStatusBook one = bookList.get(position);

        holder.bookName.setText(one.getBookName());
        holder.scheduleNumber.setText(one.getScheduleNumber());
        holder.duration.setText(one.getDuration());
        holder.averageHours.setText(one.getAverageHours());

        Glide.with(context)
                .load(one.getImageUrl())
                .placeholder(R.drawable.book_image)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ReadingStatusBookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView bookName;
        TextView scheduleNumber;
        TextView duration;
        TextView averageHours;

        public ReadingStatusBookViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.book_image);
            bookName = view.findViewById(R.id.bookName);
            scheduleNumber = view.findViewById(R.id.schedule_number);
            duration = view.findViewById(R.id.duration);
            averageHours = view.findViewById(R.id.averageHours);
        }
    }
}
