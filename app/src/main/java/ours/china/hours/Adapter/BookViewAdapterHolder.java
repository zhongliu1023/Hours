package ours.china.hours.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.liulishuo.filedownloader.BaseDownloadTask;

import ours.china.hours.R;

public class BookViewAdapterHolder  extends RecyclerView.ViewHolder {
    ImageView bookImage;
    ImageView downloadStateImage;
    ImageView readStateImage;
    TextView bookName;
    ProgressBar progressBar;

    public BookViewAdapterHolder(View itemView) {
        super(itemView);

        bookImage = itemView.findViewById(R.id.item_book_image);
        downloadStateImage = itemView.findViewById(R.id.downState);
        readStateImage = itemView.findViewById(R.id.readState);
        bookName = itemView.findViewById(R.id.item_bookName);
        progressBar = itemView.findViewById(R.id.progressBar);
    }
    public void updateProgress(final int sofar, final int total, final int speed) {
        if (total == -1) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setMax(total);
            progressBar.setProgress(sofar);
        }
    }
    public void updateCompleted(final BaseDownloadTask task) {
        progressBar.setIndeterminate(false);
        progressBar.setMax(task.getSmallFileTotalBytes());
        progressBar.setProgress(task.getSmallFileSoFarBytes());
        progressBar.setVisibility(View.GONE);
    }
    public void updateConnected(String etag, String filename) {
        progressBar.setVisibility(View.VISIBLE);
    }
    public void updatePending(BaseDownloadTask task) {
        progressBar.setVisibility(View.VISIBLE);
    }
}