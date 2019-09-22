package ours.china.hours.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorSpace;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jsoup.select.Evaluator;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ours.china.hours.Activity.Global;
import ours.china.hours.BookLib.foobnix.android.utils.LOG;
import ours.china.hours.BookLib.nostra13.universalimageloader.utils.L;
import ours.china.hours.CustomView.CustomRectView;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Book;
import ours.china.hours.R;

public class ReadingStatusBookAdapter extends RecyclerView.Adapter<ReadingStatusBookAdapter.ReadingStatusBookViewHolder> {

    private static final String TAG = "ReadingNowBookActivity";
    private List<Book> bookList;
    public Context context;
    ReadingStatusBookViewHolder holder;

    // the cycle continues between the LinkedList => textViewPol and Arraylist => textViews.
    // if it's added as needed, it won't be created and the cycle will continue.
    private final List<TextView> textViewPool = new LinkedList<>();

    public ReadingStatusBookAdapter(Context context, ArrayList<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ReadingStatusBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_book_reading_now_item, parent, false);
        holder = new ReadingStatusBookViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReadingStatusBookViewHolder holder, int position) {

        Book one = bookList.get(position);

        holder.bookName.setText(one.bookName);
        holder.txtReadTime.setText(one.bookStatus.time);

        Log.i("ReadingStatusBook", "reagTime = >>>>>" + one.bookStatus.time);
        if (one.bookStatus.time.equals("")) {
            holder.txtReadTime.setText("0.0");
        } else {
            float hours = ((float) (Integer.parseInt(one.bookStatus.time) / 1000)) / 3600;
            Log.i(TAG, "hours => " + String.format("%.2f", hours));

            holder.txtReadTime.setText(String.format("%.2f", hours));
        }
        holder.txtSpecifiedTime.setText(one.demandTime);
        holder.txtLastDate.setText(one.bookStatus.lastRead);

        if (one.bookImageLocalUrl.isEmpty()){
            Glide.with(context)
                    .load(Url.domainUrl + "/" + one.coverUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }else{
            Glide.with(context)
                    .load(one.bookImageLocalUrl)
                    .placeholder(R.drawable.book_image)
                    .into(holder.bookImage);
        }

        String[] pages = one.bookStatus.pages.split(",");
        int percent = (one.bookStatus.pages.split(",")).length / Integer.parseInt(one.pageCount);
        holder.txtReadPercent.setText(String.valueOf((pages.length * 100 / Integer.parseInt(one.pageCount))));

        Log.i(TAG, "book read percent => " + percent);
        Log.i(TAG, "pages => " + one.bookStatus.pages);
        Log.i(TAG, "total page => " + one.pageCount);
        // we can use getWidth(), getHeight(), getX(), getY() after the layout was created really.
        // so we must use getViewTreeObeserver().addOnGlobalLayoutListener().
        holder.container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                float paramMarginLeft = holder.txtStateBar.getX();
                float paramMarginTop = holder.txtStateBar.getY();
                int paramWidth = (int) holder.txtStateBar.getWidth();
                int paramHeight = (int) holder.txtStateBar.getHeight();

                float paramWidthPerPage = (int) paramWidth / Integer.parseInt(one.pageCount);
                holder.recycleTextViews();

                if (pages.length !=0 && !pages[0].equals("")) {

                    for (int i = 0; i < pages.length; i++) {

                        TextView tv = holder.getRecycledTextViewOrCreate();
                        holder.textViews.add(tv);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) paramWidthPerPage, paramHeight);
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                        params.leftMargin = (int) (paramWidthPerPage * Integer.parseInt(pages[i]) + paramMarginLeft + holder.container.getWidth() * 1.2 / 5.6);
                        params.topMargin = (int) (convertDpToPixel(8) + holder.containerStateBar.getHeight() * 2 / 3 + paramMarginTop);
                        tv.setBackgroundColor(context.getResources().getColor(R.color.pink));

                        holder.container.addView(tv, params);
                    }
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        if (!Global.readingNowOrHistory.equals("history")) {
            return this.bookList.size();
        }
        if (this.bookList.size() > 2) {
            return 2;
        }
        return this.bookList.size();

    }

    public class ReadingStatusBookViewHolder extends RecyclerView.ViewHolder {

        ImageView bookImage;
        TextView bookName;
        TextView txtReadPercent;
        TextView txtReadTime;
        TextView txtSpecifiedTime;
        TextView txtLastDate;

        TextView txtStateBar;
        LinearLayout containerStateBar;

        ViewGroup container;
        RelativeLayout rootNowItem;
        private final List<TextView> textViews = new ArrayList<>();

        public ReadingStatusBookViewHolder(View view) {
            super(view);

            bookImage = view.findViewById(R.id.bookImage);
            bookName = view.findViewById(R.id.bookName);
            txtReadPercent = view.findViewById(R.id.txtReadPercent);
            txtReadTime = view.findViewById(R.id.txtReadingTime);
            txtSpecifiedTime = view.findViewById(R.id.txtSpecifiedTime);
            txtLastDate = view.findViewById(R.id.txtLastDate);

            txtStateBar = view.findViewById(R.id.stateBar);
            containerStateBar = view.findViewById(R.id.containerStateBar);

            container = (ViewGroup) itemView;
            rootNowItem = view.findViewById(R.id.rootNowItem);
        }

        private TextView getRecycledTextViewOrCreate() {
            if (textViewPool.isEmpty()) {
                View view = (View) LayoutInflater.from(container.getContext()).inflate(R.layout.block, container, false);
                return (TextView) view;
            }
            return textViewPool.remove(0);
        }

        public void recycleTextViews() {
            textViewPool.addAll(textViews);

            // remove the addition.
            for (int i = 0; i < textViews.size(); i++) {
                container.removeView(textViews.get(i));
            }

            // remove all the instances.
            textViews.clear();
        }
    }


    @Override
    public void onViewRecycled(@NonNull ReadingStatusBookViewHolder holder) {
        super.onViewRecycled(holder);
        holder.recycleTextViews();
    }

    public static int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

}
