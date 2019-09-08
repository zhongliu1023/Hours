package ours.china.hours.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import ours.china.hours.Activity.MainActivity;
import ours.china.hours.Activity.ProfileModificationActivity;
import ours.china.hours.Model.Profile;
import ours.china.hours.R;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    public List<Profile> profileList;
    public Context context;

    public ProfileAdapter(List<Profile> profileList, Context context) {
        this.profileList = profileList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, final int position) {
        Profile one = profileList.get(position);
        holder.startText.setText(one.getStartText());
        holder.endText.setText(one.getEndText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (position) {
                    case 0:
                        Intent zeroIntent = new Intent (context, MainActivity.class);
                        zeroIntent.putExtra("from", "ProfileActivity");
                        context.startActivity(zeroIntent);
                        break;
                    case 1:
                        Intent firstIntent = new Intent(context, ProfileModificationActivity.class);
                        context.startActivity(firstIntent);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder {

        private TextView startText;
        private TextView endText;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            startText = itemView.findViewById(R.id.startText);
            endText = itemView.findViewById(R.id.endText);
        }
    }
}
