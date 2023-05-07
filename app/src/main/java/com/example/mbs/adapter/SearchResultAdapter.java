package com.example.mbs.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbs.R;
import com.example.mbs.database.Database;
import com.example.mbs.database.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<User> dataset;
    Database db = new Database();
    private Context context;
    int total_data;
    public SearchResultAdapter(ArrayList<User> users, Context context) {
        dataset = users;
        this.context = context;
        total_data = dataset.size();
    }
    public static class SearchResultTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView fullname;
        TextView year;
        TextView address;

        public SearchResultTypeViewHolder(View itemView) {
            super(itemView);
            fullname = (TextView) itemView.findViewById(R.id.tv_full_name);
            img = (ImageView) itemView.findViewById(R.id.iv_icon);
            year = (TextView) itemView.findViewById(R.id.tv_year);
            address = (TextView) itemView.findViewById(R.id.tv_adr);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_type, parent, false);
        return new SearchResultAdapter.SearchResultTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User object = dataset.get(position);
        if (object != null) {
            db.getImageUri(object.getProfilePhotoUrl(), new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(uri != null)
                            Picasso.get()
                                    .load(uri)
                                    .into(((SearchResultAdapter.SearchResultTypeViewHolder) holder).img);
                    }
                });
            ((SearchResultAdapter.SearchResultTypeViewHolder) holder).fullname
                    .setText(object.getFullName());

            ((SearchResultTypeViewHolder) holder).year
                    .setText("Mezun Yılı: "+ object.getGraduate().substring(6));

            if(!Objects.equals(object.getCountry(), "") && !Objects.equals(object.getCity(), ""))
                ((SearchResultTypeViewHolder) holder).address
                        .setText(object.getCountry() + ", " + object.getCity());
            else if(object.getCountry() != "")
                ((SearchResultTypeViewHolder) holder).address
                        .setText(object.getCountry());
            else
                ((SearchResultTypeViewHolder) holder).address
                        .setText(object.getCity());
        }
    }

    @Override
    public int getItemCount() {
        return total_data;
    }
}
