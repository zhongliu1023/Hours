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

        if (one.getImageUrl1() != null) {
            if (!one.getImageUrl1().equals("")) {
                Glide.with(context)
                        .load(one.getImageUrl1())
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView1);
            }
        }
        if (one.getImageUrl2() != null) {
            if (!one.getImageUrl2().equals("")) {
                Glide.with(context)
                        .load(one.getImageUrl2())
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView2);
            }
        }
        if (one.getImageUrl3() != null) {
            if (!one.getImageUrl3().equals("")) {
                Glide.with(context)
                        .load(one.getImageUrl3())
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView3);
            }
        }
        if (one.getImageUrl4() != null) {
            if (!one.getImageUrl4().equals("")) {
                Glide.with(context)
                        .load(one.getImageUrl4())
                        .placeholder(R.drawable.book_image)
                        .into(holder.imageView4);
            }
        }

        holder.txtType.setText(one.getFavoriteType());

        if (one.getStateClick().equals("noClicked")) {
            holder.linBound.setBackground(context.getResources().getDrawable(R.drawable.rect_favorites_noclicked));
        } else if (one.getStateClick().equals("clicked")) {
            holder.linBound.setBackground(context.getResources().getDrawable(R.drawable.rect_favorites_clicked));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Global.editStateOfFavorite.equals("no")) {
                    Intent intent = new Intent(context, FavoritesDetailActivity.class);
                    intent.putExtra("favoriteType", one.getFavoriteType());
                    context.startActivity(intent);

                } else if (Global.editStateOfFavorite.equals("yes")) {

                    if (one.getStateClick().equals("noClicked")) {
                        one.setStateClick("clicked");
                        holder.linBound.setBackground(context.getResources().getDrawable(R.drawable.rect_favorites_clicked));

                    } else if (one.getStateClick().equals("clicked")) {
                        one.setStateClick("noClicked");
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
    }
}
