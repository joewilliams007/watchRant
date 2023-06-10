package com.dev.watchrant;

import static com.dev.watchrant.RantActivity.toast;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.FollowingAdapter;
import com.dev.watchrant.adapters.FollowingItem;
import com.dev.watchrant.adapters.NotifAdapter;
import com.dev.watchrant.adapters.NotifItem;
import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.classes.NotifItems;
import com.dev.watchrant.classes.NotifUnread;
import com.dev.watchrant.databinding.ActivityFollowingBinding;
import com.dev.watchrant.databinding.ActivityNotifBinding;
import com.dev.watchrant.methods.MethodsNotif;
import com.dev.watchrant.methods.ModelNotif;
import com.dev.watchrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingActivity extends Activity {

    private ActivityFollowingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityFollowingBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        createList();
    }

    public void createList(){
        ArrayList<FollowingItem> menuItems = new ArrayList<>();

        String[] following = Account.following().split("\n");
        try {
            for (String item : following){
                String[] args = item.split(" ");
                menuItems.add(new FollowingItem(
                        args[0],
                        args[1],
                        args[2],
                        args[3]
                ));
            }
        } catch (Exception e) {

        }
        build(menuItems);
    }

    private void build(ArrayList<FollowingItem> menuItems) {
        WearableRecyclerView wearableRecyclerView = binding.followingMenuView;

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        wearableRecyclerView.setAdapter(new FollowingAdapter(this, menuItems, new FollowingAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                FollowingItem menuItem = menuItems.get(menuPosition);
                Intent intent = new Intent(FollowingActivity.this, ProfileActivity.class);
                intent.putExtra("id", String.valueOf(menuItem.getId()));
                startActivity(intent);
            }
        }) {
        });

        wearableRecyclerView.setAlpha(0);
        wearableRecyclerView.setTranslationY(Tools.dpToPx(40));
        wearableRecyclerView.animate().alpha(1).translationY(0).setDuration(300).withLayer();
        wearableRecyclerView.requestFocus();
    }
}