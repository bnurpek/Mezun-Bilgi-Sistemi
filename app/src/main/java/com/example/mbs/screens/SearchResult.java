package com.example.mbs.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbs.R;
import com.example.mbs.adapter.SearchResultAdapter;
import com.example.mbs.database.Database;
import com.example.mbs.database.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class SearchResult extends AppCompatActivity {
    Database db = new Database();
    ArrayList<User> allUsers;
    ArrayList<User> queryResult;
    TextView title;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        title = findViewById(R.id.tv_title);

        title.setText("Mezunlar");

        searchView = findViewById(R.id.searchView);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        db.getAllUsers(new OnSuccessListener<ArrayList<User>>() {
            @Override
            public void onSuccess(ArrayList<User> users) {
                allUsers = users;
                SearchResultAdapter adapter = new SearchResultAdapter(users, SearchResult.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchResult.this,
                        RecyclerView.VERTICAL, false);

                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);

                mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                        View item = rv.findChildViewUnder(e.getX(), e.getY());
                        String uID;
                        if(item != null) {
                            if (queryResult != null)
                                uID = queryResult.get(rv.getChildAdapterPosition(item)).getuID();
                            else
                                uID = allUsers.get(rv.getChildAdapterPosition(item)).getuID();
                            Intent intent = new Intent(getApplicationContext(), Profile.class);
                            intent.putExtra("userID", uID);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), "There is an error happened.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryResult = db.getUsersViaFilter(allUsers, newText);
                SearchResultAdapter adapter = new SearchResultAdapter(queryResult, SearchResult.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchResult.this,
                        RecyclerView.VERTICAL, false);

                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
                return false;
            }
        });
    }

    private void setUsers(ArrayList<User> users) {
        SearchResultAdapter adapter = new SearchResultAdapter(users, SearchResult.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchResult.this,
                RecyclerView.VERTICAL, false);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
    }
}
