package com.example.tovisit_swatirathour_c0772098_android.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Place.class}, version = 1, exportSchema = false)
public abstract class PlaceRoomDb extends RoomDatabase {



        private static final String DB_NAME = "place_room_db";

        public abstract PlaceDao placeDao();

        private static volatile PlaceRoomDb INSTANCE;

        public static PlaceRoomDb getInstance(Context context) {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PlaceRoomDb.class, DB_NAME)
                        .allowMainThreadQueries()
                        .build();
            return INSTANCE;
        }
}
