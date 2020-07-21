package com.example.tovisit_swatirathour_c0772098_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tovisit_swatirathour_c0772098_android.Adapters.RecyclerViewAdapter;
import com.example.tovisit_swatirathour_c0772098_android.Room.Place;
import com.example.tovisit_swatirathour_c0772098_android.Room.PlaceRepository;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    RecyclerView mRecyclerView;
    private List<Place> mPlaceList = new ArrayList<>();
    private PlaceRepository mPlaceRepository;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPlaceRepository = new PlaceRepository(this.getApplication());
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, mPlaceList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    public void addPlaceClicked(View view) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlaceList = mPlaceRepository.fetchAllPlaces();
        mRecyclerViewAdapter.setPlaces(mPlaceList);
    }

    public void nearbyClicked(View view) {
        Intent intent = new Intent(MainActivity.this, NearbyActivity.class);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback mSimpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                final Place place = mRecyclerViewAdapter.removePlace(position);
                mPlaceRepository.deletePlace(place);
                Snackbar.make(mRecyclerView, place.getTitle(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlaceRepository.insertPlace(place);
                        mRecyclerViewAdapter.insertPlace(place, position);
                    }
                });
            } else if (direction == ItemTouchHelper.RIGHT) {
                System.out.println("right");
                Place place = mRecyclerViewAdapter.togglePlaceVisited(position);
                mPlaceRepository.updatePlace(place);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            String label = "Visited";
            try {
                int position = viewHolder.getAdapterPosition();
                if (mRecyclerViewAdapter.isVisited(position)) {
                    label = "To Visit";
                } else {
                    label = "Visited";
                }
            } catch (Exception e) {

            }
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.RED)
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_outline_24)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .addSwipeRightLabel(label)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark))
                    .setSwipeRightLabelColor(Color.WHITE)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}