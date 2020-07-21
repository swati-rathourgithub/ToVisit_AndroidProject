package com.example.tovisit_swatirathour_c0772098_android.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlaceDao {
    @Insert
    void insertPlace(Place place);

    @Delete
    void deletePlace(Place place);

    @Update
    void updatePlace(Place place);

    @Query("SELECT * FROM Place")
    List<Place> getAllPlaces();
}


//
//    @Query("UPDATE employee SET name = :name, department = :department, salary = :salary WHERE id = :id")
//    int updateEmployee(int id, String name, String department, double salary);
//
//    @Query("SELECT * FROM employee ORDER BY name")
//    List<Employee> getAllEmployees();