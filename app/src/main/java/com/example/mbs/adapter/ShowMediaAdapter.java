package com.example.mbs.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbs.R;
import com.example.mbs.screens.Profile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Uri> dataset;
    private Context context;
    int total_data;
    public ShowMediaAdapter(ArrayList<Uri> uris, Context context) {
        dataset = uris;
        this.context = context;
        total_data = dataset.size();
    }
    public static class ShowMediaTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public ShowMediaTypeViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.iv_media);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(context.getClass() == Profile.class)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.only_image_type, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_type, parent, false);
        return new ShowMediaAdapter.ShowMediaTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Uri object = dataset.get(position);
        if (object != null) {
            Picasso.get()
                    .load(object)
                    .into(((ShowMediaAdapter.ShowMediaTypeViewHolder) holder).img);
        }
    }

    @Override
    public int getItemCount() {
        return total_data;
    }
}
