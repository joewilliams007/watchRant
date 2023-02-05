package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.profile_avatar;
import static com.dev.watchrant.ProfileActivity.rant_image;
import static com.dev.watchrant.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import java.util.ArrayList;

public class RantAdapter extends WearableRecyclerView.Adapter<RantAdapter.RecyclerViewHolder> {

    private ArrayList<RantItem> dataSource = new ArrayList<RantItem>();
    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private AdapterCallback callback;

    private String drawableIcon;
    private Context context;


    public RantAdapter(Context context, ArrayList<RantItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rant_menu_item,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout menuContainer;
        TextView menuItem;
        ImageView menuIcon;
        TextView detailsItem;
        ImageView image;

        public RecyclerViewHolder(View view) {
            super(view);
            menuContainer = view.findViewById(R.id.menu_container);
            menuItem = view.findViewById(R.id.menu_item);
            menuIcon = view.findViewById(R.id.menu_icon);
            detailsItem = view.findViewById(R.id.details_item);
            image = view.findViewById(R.id.image);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        RantItem data_provider = dataSource.get(position);

        switch (data_provider.getType()) {
            case "avatar":
                holder.menuItem.setVisibility(View.GONE);
                holder.menuIcon.setVisibility(View.INVISIBLE);
                holder.detailsItem.setVisibility(View.GONE);

                if (data_provider.getText()!=null) {
                    new DownloadImageTask(holder.menuIcon)
                            .execute("https://avatars.devrant.com/"+data_provider.getText());
                } else {
                    holder.menuIcon.setVisibility(View.VISIBLE);
                }
                profile_avatar = data_provider.getText();

                break;
            case "image":
                rant_image = data_provider.getText();

                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText("VIEW IMAGE");
                holder.menuItem.setGravity(Gravity.CENTER);
                holder.menuItem.setBackground(null);
                holder.detailsItem.setVisibility(View.GONE);
                break;
            case "details":
                holder.menuItem.setVisibility(View.VISIBLE);
                holder.menuIcon.setVisibility(View.GONE);
                holder.menuItem.setText(data_provider.getText());
                holder.menuItem.setGravity(Gravity.CENTER);
                holder.menuItem.setBackground(null);
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
                holder.menuItem.setVisibility(View.VISIBLE);
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
                    holder.detailsItem.setText(" "+data_provider.getScore());
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

class RantItem {
    private String text;
    private String image;
    private String type;
    private int score;
    private int numComments;

    private int id;
    public RantItem(String image_url, String text, int id, String type, int score, int numComments) {
        this.image = image_url;
        this.text = text;
        this.id = id;
        this.type = type;
        this.score = score;
        this.numComments = numComments;
    }
    public String getType() {
        return type;
    }
    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public int getNumComments() {
        return numComments;
    }

}
