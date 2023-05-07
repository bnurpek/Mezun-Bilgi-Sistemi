package com.example.mbs.adapter;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.mbs.R;
import com.example.mbs.database.Announcements;
import com.example.mbs.database.Database;
import com.example.mbs.database.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnnouncementsAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Announcements> announcements;
    Database db = new Database();
    private Context context;
    int total_data;
    public AnnouncementsAdapter(ArrayList<Announcements> announcements, Context context) {
        this.announcements = announcements;
        this.context = context;
        total_data = announcements.size();
    }

    public static class AnnImageTypeViewHolder extends ViewHolder{
        ImageView img;
        TextView header, author, text;
        public AnnImageTypeViewHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.tv_header);
            author = (TextView) itemView.findViewById(R.id.tv_user);
            text = (TextView) itemView.findViewById(R.id.tv_ann);
            img = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }

    public static class AnnouncementTypeViewHolder extends ViewHolder{
        TextView header, author, text;
        public AnnouncementTypeViewHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.tv_header);
            author = (TextView) itemView.findViewById(R.id.tv_user);
            text = (TextView) itemView.findViewById(R.id.tv_ann);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Announcements.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noimg_ann_type,
                        parent, false);
                return new AnnouncementTypeViewHolder(view);
            case Announcements.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_ann_type,
                        parent, false);
                return new AnnImageTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (announcements.get(position).type) {
            case 0:
                return Announcements.TEXT_TYPE;
            case 1:
                return Announcements.IMAGE_TYPE;
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {
        Announcements object = announcements.get(listPosition);
        if (object != null) {
            switch (object.type) {
                case Announcements.TEXT_TYPE:
                    ((AnnouncementTypeViewHolder) holder).header.setText(object.header);
                    ((AnnouncementTypeViewHolder) holder).author.setText(object.author);
                    ((AnnouncementTypeViewHolder) holder).text.setText(object.text);
                    break;

                case Announcements.IMAGE_TYPE:
                    db.getImageUri(object.path, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .into(((AnnImageTypeViewHolder) holder).img);
                        }
                    });
                    ((AnnImageTypeViewHolder) holder).header.setText(object.header);
                    ((AnnImageTypeViewHolder) holder).author.setText(object.author);
                    ((AnnImageTypeViewHolder) holder).text.setText(object.text);
            }
        }
    }

    @Override
    public int getItemCount() {
        return total_data;
    }
}
