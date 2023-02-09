package com.dev.watchrant;

import static com.dev.watchrant.RantActivity.toast;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.MainMenuAdapter;
import com.dev.watchrant.adapters.NotifAdapter;
import com.dev.watchrant.adapters.NotifItem;
import com.dev.watchrant.adapters.RantItem;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.classes.NotifData;
import com.dev.watchrant.classes.NotifItems;
import com.dev.watchrant.classes.NotifUnread;
import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.classes.Unread;
import com.dev.watchrant.databinding.ActivityNotifBinding;
import com.dev.watchrant.methods.MethodsFeed;
import com.dev.watchrant.methods.MethodsNotif;
import com.dev.watchrant.methods.ModelNotif;
import com.dev.watchrant.models.ModelFeed;
import com.dev.watchrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifActivity extends Activity {

    private ActivityNotifBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Account.theme().equals("dark")) {
            setTheme(R.style.Theme_Dark);
        } else {
            setTheme(R.style.Theme_Amoled);
        }
        super.onCreate(savedInstanceState);

        binding = ActivityNotifBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        startReq();
    }

    private void startReq() {
        MethodsNotif methods = RetrofitClient.getRetrofitInstance().create(MethodsNotif.class);
        String total_url = BASE_URL
                + "users/me/notif-feed?"+"token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id()+"&app=3"+"/";
       // + "devrant/rants?"+"token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id()+"&app=3&limit="+Account.limit()+"&sort="+sort+"&range=all&skip=0"

        Call<ModelNotif> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelNotif>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelNotif> call, @NonNull Response<ModelNotif> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    List<NotifItems> items = response.body().getData().getItems();
                    NotifUnread unread = response.body().getData().getUnread();

                    createFeedList(items, unread);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast(response.message());
                } else {
                    toast(response+" ");

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelNotif> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");

            }
        });
    }


    public void createFeedList(List<NotifItems> items, NotifUnread unread){
        ArrayList<NotifItem> menuItems = new ArrayList<>();

        int all = unread.getAll();
        int upvotes = unread.getUpvotes();
        int mentions = unread.getMentions();
        int comments = unread.getComments();
        int subs = unread.getSubs();
        int total = unread.getTotal();


        menuItems.add(new NotifItem(0,"all",all,0,0,"all "+all
                +"\nupvotes "+upvotes
                +"\nmentions "+mentions
                +"\ncomments "+comments
                +"\nsubs "+subs));

        for (NotifItems item : items){
            menuItems.add(new NotifItem(item.getCreated_time(),item.getType(),item.getRead(),item.getRant_id(),item.getUid(),null));
        }
        build(menuItems);
    }

    private void build(ArrayList<NotifItem> menuItems) {
        WearableRecyclerView wearableRecyclerView = binding.notifMenuView;

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        wearableRecyclerView.setAdapter(new NotifAdapter(this, menuItems, new NotifAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                NotifItem menuItem = menuItems.get(menuPosition);
                Intent intent = new Intent(NotifActivity.this, RantActivity.class);
                intent.putExtra("id",String.valueOf(menuItem.getRant_id()));
                startActivity(intent);
            }
        }));
    }
}