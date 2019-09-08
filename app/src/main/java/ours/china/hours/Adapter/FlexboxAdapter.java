package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import ours.china.hours.R;

public class FlexboxAdapter extends RecyclerView.Adapter<FlexboxAdapter.ViewHolder> {

    public String[] listStr;
    public Context context;

    public FlexboxAdapter(String[] listStr, Context context) {
        this.listStr = listStr;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flexbox_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String one = listStr[position];
        holder.txtItem.setText(one);
    }

    @Override
    public int getItemCount() {
        return listStr.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItem = itemView.findViewById(R.id.flex_box_recycler_view_text_item);
        }
    }
}
