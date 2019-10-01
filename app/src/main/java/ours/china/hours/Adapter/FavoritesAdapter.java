package ours.china.hours.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Activity.FavoritesDetailActivity;
import ours.china.hours.Activity.Global;
import ours.china.hours.Management.Url;
import ours.china.hours.Model.Favorites;
import ours.china.hours.R;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private List<Favorites> favoritesList;
    public Context context;

    public FavoritesAdapter.addActionListener listener;

    public FavoritesAdapter(ArrayList<Favorites> favoritesList, Context context) {
        this.favoritesList = favoritesList;
        this.context = context;
        this.listener = (FavoritesAdapter.addActionListener) context;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorties_item, parent, false);
        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoritesViewHolder holder, int position) {
        final Favorites one = favoritesList.get(position);

        holder.imageView1.setVisibility(View.INVISIBLE);
        holder.imageView2.setVisibility(View.INVISIBLE);
        holder.imageView3.setVisibility(View.INVISIBLE);
        holder.imageView4.setVisibility(View.INVISIBLE);
        if (one.bookList.size() > 0 && one.bookList.get(0) != null) {
            holder.imageView1.setVisibility(View.VISIBLE);
            if (!one.bookList.get(0).bookImageLocalUrl.equals("")) {
                Glide.with(context)
                        .load(one.bookList.get(0).bookImageLocalUrl)
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView1);
            }else{
                Glide.with(context)
                        .load(Url.domainUrl + "/" + one.bookList.get(0).coverUrl)
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView1);
            }
        }
        if (one.bookList.size() > 1 && one.bookList.get(1) != null) {
            holder.imageView2.setVisibility(View.VISIBLE);
            if (!one.bookList.get(1).bookImageLocalUrl.equals("")) {
                Glide.with(context)
                        .load(one.bookList.get(1).bookImageLocalUrl)
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView2);
            }else{
                Glide.with(context)
                        .load(Url.domainUrl + "/" + one.bookList.get(1).coverUrl)
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView2);
            }
        }
        if (one.bookList.size() > 2 && one.bookList.get(2) != null) {
            holder.imageView3.setVisibility(View.VISIBLE);
            if (!one.bookList.get(2).bookImageLocalUrl.equals("")) {
                Glide.with(context)
                        .load(one.bookList.get(2).bookImageLocalUrl)
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView3);
            }else{
                Glide.with(context)
                        .load(Url.domainUrl + "/" + one.bookList.get(2).coverUrl)
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView3);
            }
        }
        if (one.bookList.size() > 3 && one.bookList.get(3) != null) {
            holder.imageView4.setVisibility(View.VISIBLE);
            if (!one.bookList.get(3).bookImageLocalUrl.equals("")) {
                Glide.with(context)
                        .load(one.bookList.get(3).bookImageLocalUrl)
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView4);
            }else{
                Glide.with(context)
                        .load(Url.domainUrl + "/" + one.bookList.get(3).coverUrl)
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView4);
            }
        }

        holder.txtType.setText(one.favorite);

        if (!one.isChecked) {
            holder.linBound.setBackground(context.getResources().getDrawable(R.drawable.rect_favorites_noclicked));
        } else {
            holder.linBound.setBackground(context.getResources().getDrawable(R.drawable.rect_favorites_clicked));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Global.editStateOfFavorite.equals("no")) {

                    listener.onItemClickAction(one);

                } else if (Global.editStateOfFavorite.equals("yes")) {

                    if (!one.isChecked) {
                        one.isChecked = true;
                        holder.linBound.setBackground(context.getResources().getDrawable(R.drawable.rect_favorites_clicked));

                    } else if (one.isChecked) {
                        one.isChecked = false;
                        holder.linBound.setBackground(context.getResources().getDrawable(R.drawable.rect_favorites_noclicked));
                    }

                    listener.enableComponentAction();

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void reloadFavorites(ArrayList<Favorites> updatedFavorites){
        favoritesList = updatedFavorites;
        notifyDataSetChanged();
    }


    public class FavoritesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        ImageView imageView4;
        TextView txtType;

        LinearLayout linBound;

        public FavoritesViewHolder(View view) {
            super(view);

            imageView1 = view.findViewById(R.id.image1);
            imageView2 = view.findViewById(R.id.image2);
            imageView3 = view.findViewById(R.id.image3);
            imageView4 = view.findViewById(R.id.image4);
            txtType = view.findViewById(R.id.txtFavoritesType);

            linBound = view.findViewById(R.id.linBound);
        }
    }

    public interface addActionListener {
        public void enableComponentAction();
        public void onItemClickAction(Favorites one);
    }
}
