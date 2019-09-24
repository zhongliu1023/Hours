package ours.china.hours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

import ours.china.hours.Model.SelectFavorite;
import ours.china.hours.R;

public class SelectFavoriteAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> mFavoritesCollection;

    public SelectFavoriteAdapter(Context context, ArrayList<String> mFavoritesCollection) {
        this.context = context;
        this.mFavoritesCollection = mFavoritesCollection;
    }

    @Override
    public int getCount() {
        return mFavoritesCollection.size();
    }

    @Override
    public Object getItem(int i) {
        return mFavoritesCollection.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dialog_select_item, null);
        } else {
            view = convertView;
        }

        CheckedTextView checkedTextView = view.findViewById(R.id.checkTextView);
//        if (position == 0) {
//            checkedTextView.setChecked(true);
//        }

        checkedTextView.setText(mFavoritesCollection.get(position));

        return view;
    }
}
