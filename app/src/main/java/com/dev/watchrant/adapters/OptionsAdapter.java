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

public class OptionsAdapter extends WearableRecyclerView.Adapter<OptionsAdapter.RecyclerViewHolder> {

    private ArrayList<OptionsItem> dataSource = new ArrayList<OptionsItem>();
    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private AdapterCallback callback;

    private String drawableIcon;
    private Context context;


    public OptionsAdapter(Context context, ArrayList<OptionsItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.options_menu_item,parent,false);

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
        OptionsItem data_provider = dataSource.get(position);

        holder.setIsRecyclable(false);
        holder.menuItem.setText(data_provider.getText());
        switch (data_provider.getId()) {
            case 0:
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                break;
            case 1:
                holder.menuItem.setGravity(Gravity.CENTER);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setBackground(null);
                holder.menuItem.setTypeface(null, Typeface.ITALIC);
                break;
            case 3:
                holder.menuItem.setGravity(Gravity.CENTER);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setBackground(null);
                holder.menuItem.setTextColor(Color.parseColor("#f4945c"));
                break;
        }

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

