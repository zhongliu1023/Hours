package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ours.china.hours.Model.Profile;
import ours.china.hours.R;

public class ProfileModificationAdapter extends RecyclerView.Adapter<ProfileModificationAdapter.ProfileModificationViewHolder> {

    private ArrayList<Profile> profileList;
    private Context context;

    public ProfileModificationAdapter(ArrayList<Profile> profileList, Context context) {
        this.profileList = profileList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileModificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        return new ProfileModificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileModificationViewHolder holder, int position) {
        Profile one = profileList.get(position);
        holder.startText.setText(one.getStartText());
        holder.endText.setText(one.getEndText());
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public class ProfileModificationViewHolder extends RecyclerView.ViewHolder {

        private TextView startText;
        private TextView endText;

        public ProfileModificationViewHolder(@NonNull View itemView) {
            super(itemView);

            startText = itemView.findViewById(R.id.startText);
            endText = itemView.findViewById(R.id.endText);
        }
    }
}
