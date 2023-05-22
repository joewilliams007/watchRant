package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.isImage;
import static com.dev.watchrant.RantActivity.toast;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.NotifAdapter;
import com.dev.watchrant.adapters.NotifItem;
import com.dev.watchrant.adapters.ReactionAdapter;
import com.dev.watchrant.adapters.ReactionItem;
import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.classes.NotifItems;
import com.dev.watchrant.classes.NotifUnread;
import com.dev.watchrant.classes.sky.Reactions;
import com.dev.watchrant.databinding.ActivityNotifBinding;
import com.dev.watchrant.databinding.ActivityReactionBinding;
import com.dev.watchrant.methods.MethodsNotif;
import com.dev.watchrant.methods.ModelNotif;
import com.dev.watchrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReactionActivity extends Activity {

    private ActivityReactionBinding binding;
    public static List<Reactions> reactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityReactionBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        createFeedList(reactions);
    }

    public void createFeedList(List<Reactions> items){
        ArrayList<ReactionItem> menuItems = new ArrayList<>();

        for (Reactions item : items){

            menuItems.add(new ReactionItem(item));
        }
        build(menuItems);
    }

    private void build(ArrayList<ReactionItem> menuItems) {
        WearableRecyclerView wearableRecyclerView = binding.reactionsMenuView;

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        wearableRecyclerView.setAdapter(new ReactionAdapter(this, menuItems, new ReactionAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                ReactionItem menuItem = menuItems.get(menuPosition);
                isImage = false;
                Intent intent = new Intent(ReactionActivity.this, ProfileActivity.class);
                intent.putExtra("id", String.valueOf(menuItem.getReaction().getUser_id()));
                startActivity(intent);
            }
        }));

        wearableRecyclerView.requestFocus();
    }
}