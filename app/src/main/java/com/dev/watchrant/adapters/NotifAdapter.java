package com.dev.watchrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.ProfileActivity;
import com.dev.watchrant.R;
import com.dev.watchrant.RantActivity;
import com.dev.watchrant.auth.MyApplication;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @SuppressLint("SetTextI18n")
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
                holder.menuItem.setText(data_provider.getUsername()+" ++'d your comment!\n"+getRelativeTimeSpanString(data_provider.getCreated_time()));
                break;
            case "comment_content":
                holder.menuItem.setText(data_provider.getUsername()+" commented on your rant!\n"+getRelativeTimeSpanString(data_provider.getCreated_time()));
                break;
            case "comment_mention":
                holder.menuItem.setText(data_provider.getUsername()+" mentioned you in a comment!\n"+getRelativeTimeSpanString(data_provider.getCreated_time()));
                break;
            case "comment_discuss":
                holder.menuItem.setText(data_provider.getUsername()+" (or more) new comments on a rant you commented on!\n"+getRelativeTimeSpanString(data_provider.getCreated_time()));
                break;
            case "content_vote":
                holder.menuItem.setText(data_provider.getUsername()+" ++'d your rant\n"+getRelativeTimeSpanString(data_provider.getCreated_time()));
                break;
            case "rant_sub":
                holder.menuItem.setText(data_provider.getUsername()+" posted a new rant!\n"+getRelativeTimeSpanString(data_provider.getCreated_time()));
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

