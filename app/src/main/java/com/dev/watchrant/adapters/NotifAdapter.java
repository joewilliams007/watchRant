package com.dev.watchrant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.R;

import java.util.ArrayList;

public class NotifAdapter extends WearableRecyclerView.Adapter<NotifAdapter.RecyclerViewHolder> {

    private ArrayList<NotifItem> dataSource = new ArrayList<NotifItem>();
    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private AdapterCallback callback;

    private String drawableIcon;
    private Context context;


    public NotifAdapter(Context context, ArrayList<NotifItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_menu_item,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout menuContainer;
        TextView menuItem;
        ImageView menuIcon;
        TextView detailsItem;

        public RecyclerViewHolder(View view) {
            super(view);
            menuContainer = view.findViewById(R.id.menu_container);
            menuItem = view.findViewById(R.id.menu_item);
            menuIcon = view.findViewById(R.id.menu_icon);
            detailsItem = view.findViewById(R.id.details_item);
        };
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        NotifItem data_provider = dataSource.get(position);

        holder.setIsRecyclable(false);

        switch (data_provider.getType()) {
            case "all":
                holder.menuItem.setText(data_provider.getUsername());
                holder.menuItem.setBackground(null);
                break;
            case "comment_vote":
                holder.menuItem.setText("++ on your comment");
                break;
            case "comment_content":
                holder.menuItem.setText("comment on your rant");
                break;
            case "comment_mention":
                holder.menuItem.setText("you have a mention");
                break;
            case "comment_discuss":
                holder.menuItem.setText("theres a new comment");
                break;
            case "content_vote":
                holder.menuItem.setText("++ on your rant");
                break;
            case "rant_sub":
                holder.menuItem.setText("a sub has made something");
                break;

        }
        if (data_provider.getRead() == 0 && !data_provider.getType().equals("all")) {
            holder.menuItem.setTextColor(Color.parseColor("#f4945c"));
        }

        holder.menuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                holder.menuItem.setTextColor(Color.parseColor("#FFFFFF"));
                if(callback != null) {
                    callback.onItemClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

