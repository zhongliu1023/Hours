package ours.china.hours.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ours.china.hours.Activity.NewsDetailActivity;
import ours.china.hours.Model.NewsItem;
import ours.china.hours.R;

public class NewsListAdatper extends BaseAdapter {

    private ArrayList<NewsItem> listData;
    private Context context;

    private LayoutInflater inflater;

    public NewsListAdatper(Context context, ArrayList<NewsItem> listData) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.news_item, null);
            holder = new ViewHolder();
            holder.newsType = view.findViewById(R.id.newsType);
            holder.newsTime = view.findViewById(R.id.newsTime);
            holder.newsContent = view.findViewById(R.id.newsContent);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.newsType.setText(listData.get(position).title);
        holder.newsTime.setText(listData.get(position).releaseTime);
        holder.newsContent.setText(listData.get(position).content);

        holder.newsType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("time", listData.get(position).releaseTime);
                intent.putExtra("content", listData.get(position).content);
                context.startActivity(intent);
            }
        });

        return view;
    }

    public void reloadNews(ArrayList<NewsItem> updatedNews){
        listData = updatedNews;
        notifyDataSetChanged();
    }
    static class ViewHolder{
        TextView newsType;
        TextView newsTime;
        TextView newsContent;
    }

}
