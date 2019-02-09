package net.icraftsystems.app.auth.android.comicfirebase;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.icraftsystems.app.auth.android.comicfirebase.Adapter.MyComicAdapter;
import net.icraftsystems.app.auth.android.comicfirebase.Adapter.MySliderAdapter;
import net.icraftsystems.app.auth.android.comicfirebase.Common.Common;
import net.icraftsystems.app.auth.android.comicfirebase.Interface.IBannerLoadDone;
import net.icraftsystems.app.auth.android.comicfirebase.Interface.IComicLoadDone;
import net.icraftsystems.app.auth.android.comicfirebase.Model.Comic;
import net.icraftsystems.app.auth.android.comicfirebase.Service.PicassoLoadingService;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity implements IBannerLoadDone, IComicLoadDone {

    Slider slider;
    SwipeRefreshLayout swipeRefreshLayout;
    android.app.AlertDialog alertDialog;

    RecyclerView recycler_comic;
    TextView txt_comic;

    //Database
    DatabaseReference banners, comics;
    //Listener
    IBannerLoadDone bannerListener;
    IComicLoadDone comicListListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init Database
        banners = FirebaseDatabase.getInstance().getReference("Banners"); //from Firebase
        comics = FirebaseDatabase.getInstance().getReference("Comic");

        //init Listener
        bannerListener = this;
        comicListListner = this;

        slider= (Slider)findViewById(R.id.slder);
        Slider.init(new PicassoLoadingService());


        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBarner();
                loadComic();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadBarner();
                loadComic();
            }
        });

        recycler_comic = (RecyclerView) findViewById(R.id.recycler_comic);
        recycler_comic.setHasFixedSize(true);
        recycler_comic.setLayoutManager(new GridLayoutManager(this,2));

        txt_comic = (TextView)findViewById(R.id.txt_comic);

    }

    private void loadComic() {

        //show dialog
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false).setMessage("Please Wait...").build();

        if (!swipeRefreshLayout.isRefreshing())
        alertDialog.show();

                comics.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Comic> comic_load =new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot comicSnapShot:dataSnapshot.getChildren()){

                    Comic comic = comicSnapShot.getValue(Comic.class);
                    comic_load.add(comic);
                }
                comicListListner.onComicLoadDoneListner(comic_load);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MainActivity.this,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void loadBarner() {

        banners.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> bannerList = new ArrayList<>();
                for(DataSnapshot bannerSnapShort:dataSnapshot.getChildren()){

                    String image = bannerSnapShort.getValue(String.class);
                    bannerList.add(image);
                }
                //call the Listener
                bannerListener.onBannerLoadDoneListner(bannerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MainActivity.this,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBannerLoadDoneListner(List<String> banners) {

        slider.setAdapter(new MySliderAdapter(banners));
    }

    @Override
    public void onComicLoadDoneListner(List<Comic> comicList) {

        Common.comicList = comicList;
        recycler_comic.setAdapter(new MyComicAdapter(getBaseContext(),comicList));
        txt_comic.setText(new StringBuilder("New Comic (").append(comicList.size()).append(")"));

        if (!swipeRefreshLayout.isRefreshing())
            alertDialog.dismiss();
    }
}
