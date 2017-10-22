package com.ink_steel.inksteel.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.adapters.GalleryRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class GalleryActivity extends AppCompatActivity {

    public static final ArrayList<Integer> images = new ArrayList<>(Arrays.asList(R.drawable.tatto1,
            R.drawable.tatto2, R.drawable.pierce1, R.drawable.pierce2));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        GalleryRecyclerViewAdapter customAdapter = new GalleryRecyclerViewAdapter(GalleryActivity.this, images);
        recyclerView.setAdapter(customAdapter);
    }
}
