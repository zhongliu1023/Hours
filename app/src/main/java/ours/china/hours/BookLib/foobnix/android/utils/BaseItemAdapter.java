package ours.china.hours.BookLib.foobnix.android.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseItemAdapter<T> extends BaseAdapter {
    private List<T> items = new ArrayList<T>();

    public BaseItemAdapter() {

    }

    public BaseItemAdapter(List<T> list) {
        addItems(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T item = getItem(position);
        return getView(position, convertView, parent, item);
    }

    public abstract View getView(int position, View convertView, ViewGroup parent, T item);

    @Override
    public T getItem(int position) {
        try {
            return items.get(position);
        } catch (Exception e) {
            return items.get(0);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getItems() {
        return items;
    }

    public void addItems(List<T> items) {
        for (T t : items) {
            this.items.add(t);
        }
        notifyDataSetChanged();
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void updateItems(List<T> items) {
        items.clear();
        addItems(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

}
