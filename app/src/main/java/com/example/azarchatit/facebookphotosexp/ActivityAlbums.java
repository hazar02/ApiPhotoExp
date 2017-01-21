package com.example.azarchatit.facebookphotosexp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.azarchatit.facebookphotosexp.MainActivity.response2;

public class ActivityAlbums extends AppCompatActivity implements RecycleClickListener.OnRecyclerClickListener {
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;


    private ArrayList<Photo> albumList = new ArrayList<>();
    private static final String TAG = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate: response "+ response2.toString());
        setContentView(R.layout.activity_albums2);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        adapter = new AlbumsAdapter(this, albumList);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecycleClickListener(this, recyclerView, this));
        recyclerView.setAdapter(adapter);

        getData(response2.getJSONObject());

    }

    /**
     * The Jason Data
     */
    public ArrayList<Photo> getData(JSONObject data) {

        try {
            Log.d(TAG, "getData: this is the data");
            JSONArray albumsArray = data.getJSONArray("data");
            for (int i = 0; i < albumsArray.length(); i++) {
                JSONObject jsonAlbums = albumsArray.getJSONObject(i);

                String name = jsonAlbums.getString("name");
                int count = jsonAlbums.getInt("photo_count");
                JSONObject photos = jsonAlbums.getJSONObject("photos");
                JSONArray jsonpicture = photos.getJSONArray("data");

                JSONObject object2 = jsonpicture.getJSONObject(0);
                JSONArray albums = object2.getJSONArray("webp_images");
                JSONObject images = albums.getJSONObject(0);
                String image = images.getString("source");

                Photo photoObject = new Photo(name, image, count);
                Log.d(TAG, "getData:photos " + photoObject.toString());
                albumList.add(photoObject);

            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getData: " + e.getMessage());
        }

        return albumList;
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, CustomGallery_Activity.class);
        intent.putExtra("intVariableName", position);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}


