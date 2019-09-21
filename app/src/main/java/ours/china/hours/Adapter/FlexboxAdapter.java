package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ours.china.hours.Common.Interfaces.SearchItemInterface;
import ours.china.hours.R;

public class FlexboxAdapter extends RecyclerView.Adapter<FlexboxAdapter.ViewHolder> {

    public List<String> listStr;
    public Context context;
    SearchItemInterface mInterface;

    public FlexboxAdapter(ArrayList<String> listStr, Context context, SearchItemInterface searchItemInterface) {
        this.listStr = listStr;
        this.context = context;
        this.mInterface = searchItemInterface;
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
        final String one = listStr.get(position);
        holder.txtItem.setText(one);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterface.onClickSearchItem(one);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listStr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItem = itemView.findViewById(R.id.flex_box_recycler_view_text_item);
        }
    }
}
