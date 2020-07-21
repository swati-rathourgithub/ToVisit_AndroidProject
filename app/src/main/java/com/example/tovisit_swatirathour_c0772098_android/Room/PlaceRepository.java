package com.example.tovisit_swatirathour_c0772098_android.Room;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlaceRepository {

    private PlaceDao placeDao;


    public PlaceRepository(Application application) {
        PlaceRoomDb placeRoomDb = PlaceRoomDb.getInstance(application);
        placeDao = placeRoomDb.placeDao();
    }

    public void updatePlace(Place place) {
        if(place != null) {
            new UpdatePlaceAsync(placeDao).execute(place);
        }
    }

    public void deletePlace(Place place) {
        if(place != null) {
            new DeletePlaceAsync(placeDao).execute(place);
        }
    }

    public void insertPlace(Place place) {
        if(place != null) {
            new InsertPlaceAsync(placeDao).execute(place);
        }
    }

    public List<Place> fetchAllPlaces() {
        List<Place> places = new ArrayList<>();
        try {
            places = new FetchAllPlacesAsync(placeDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return places;
    }

    private class InsertPlaceAsync extends AsyncTask<Place, Void, Void> {
        private PlaceDao placeDao;

        private InsertPlaceAsync(PlaceDao placeDao) {
            this.placeDao = placeDao;
        }


        @Override
        protected Void doInBackground(Place... places) {
            placeDao.insertPlace(places[0]);
            return null;
        }
    }
    private class UpdatePlaceAsync extends AsyncTask<Place, Void, Void> {
        private PlaceDao placeDao;

        private UpdatePlaceAsync(PlaceDao placeDao) {
            this.placeDao = placeDao;
        }


        @Override
        protected Void doInBackground(Place... places) {
            placeDao.updatePlace(places[0]);
            return null;
        }
    }

    private class DeletePlaceAsync extends AsyncTask<Place, Void, Void> {
        private PlaceDao placeDao;

        private DeletePlaceAsync(PlaceDao placeDao) {
            this.placeDao = placeDao;
        }


        @Override
        protected Void doInBackground(Place... places) {
            placeDao.deletePlace(places[0]);
            return null;
        }
    }

    private class FetchAllPlacesAsync extends AsyncTask<Void, Void, List<Place>> {
        private PlaceDao placeDao;

        private FetchAllPlacesAsync(PlaceDao placeDao) {
            this.placeDao = placeDao;
        }


        @Override
        protected List<Place> doInBackground(Void... voids) {
            return placeDao.getAllPlaces();
        }
    }

}
