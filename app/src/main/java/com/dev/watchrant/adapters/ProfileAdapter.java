package com.dev.watchrant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.auth.Account;
import com.dev.watchrant.auth.MyApplication;
import com.dev.watchrant.network.DownloadImageTask;
import com.dev.watchrant.R;

import java.util.ArrayList;

public class ProfileAdapter extends WearableRecyclerView.Adapter<ProfileAdapter.RecyclerViewHolder> {

    private ArrayList<RantItem> dataSource = new ArrayList<RantItem>();
    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private AdapterCallback callback;

    private String drawableIcon;
    private Context context;


    public ProfileAdapter(Context context, ArrayList<RantItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_menu_item,parent,false);

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
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        RantItem data_provider = dataSource.get(position);
        holder.setIsRecyclable(false);
        switch (data_provider.getType()) {
            case "avatar":
                holder.menuItem.setVisibility(View.GONE);
                holder.menuIcon.setVisibility(View.INVISIBLE);
                holder.detailsItem.setVisibility(View.GONE);
                holder.menuIcon.setVisibility(View.VISIBLE);
                if (data_provider.getText()!=null) {
                    new DownloadImageTask(holder.menuIcon)
                            .execute("https://avatars.devrant.com/"+data_provider.getText());
                }

                break;
            case "details":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                holder.menuItem.setGravity(Gravity.CENTER);
                holder.menuItem.setBackground(null);
                holder.detailsItem.setVisibility(View.GONE);
                break;
            case "info":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                holder.menuItem.setGravity(Gravity.CENTER);
                holder.menuItem.setBackground(null);
                holder.detailsItem.setVisibility(View.GONE);
                break;
                case "info_small":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                holder.menuItem.setGravity(Gravity.CENTER);
                holder.detailsItem.setVisibility(View.GONE);
                break;
            case "rant_details":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                holder.menuItem.setGravity(Gravity.RIGHT);
                holder.detailsItem.setVisibility(View.GONE);
                holder.menuItem.setBackground(null);
                break;
            case "feed":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                holder.detailsItem.setVisibility(View.VISIBLE);
                if (data_provider.getScore()<0) {
                    holder.detailsItem.setText(data_provider.getScore() + " comnts: " + data_provider.getNumComments());
                } else {
                    holder.detailsItem.setText("+" + data_provider.getScore() + " comnts: " + data_provider.getNumComments());
                }
                break;
            case "comment":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                if (data_provider.getScore()<0) {
                    holder.detailsItem.setText(data_provider.getScore());
                } else {
                    holder.detailsItem.setText("+" + data_provider.getScore());
                }
                break;
            case "rant":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                if (data_provider.getScore()<0) {
                    holder.detailsItem.setText(data_provider.getScore() + " comnts: " + data_provider.getNumComments());
                } else {
                    holder.detailsItem.setText("+" + data_provider.getScore() + " comnts: " + data_provider.getNumComments());
                }

                break;
            case "phone":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                holder.detailsItem.setVisibility(View.GONE);
                holder.menuItem.setGravity(Gravity.CENTER);
                holder.menuItem.setBackground(null);
                holder.menuItem.setTextColor(Color.parseColor("#f4945c"));
                break;

        }
        if (Account.isLoggedIn()) {
            if (data_provider.getVote_state()==1) {
                holder.menuItem.setBackground(ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.rounded_corner_up));
            } else if (data_provider.getVote_state()<0) {
                holder.menuItem.setBackground(ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.rounded_corner_down));
            }
        }

        /*if (data_provider.getImage()!=null) {

            new DownloadImageTask(holder.menuIcon)
                    .execute(data_provider.getImage().replaceAll("\\\\",""));
        } else {
            holder.menuIcon.setVisibility(View.GONE);
        }*/

        holder.menuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

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

