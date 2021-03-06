package com.example.azarchatit.facebookphotosexp;

/**
 * Created by azar on 12/01/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Photo> albumList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        }
    }


    public AlbumsAdapter(Context mContext, ArrayList<Photo> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Photo album = albumList.get(position);
        holder.title.setText(album.getName());
        if (album.getCount() == 1) {
            holder.count.setText(album.getCount() + " " + "Picture");
        } else {
            holder.count.setText(album.getCount() + " " + "Pictures");
        }

        // loading album photo using Glide library
        Glide.with(mContext).load(album.getUrlphoto()).into(holder.thumbnail);


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
