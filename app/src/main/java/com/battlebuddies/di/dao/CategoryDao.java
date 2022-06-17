package com.battlebuddies.di.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.battlebuddies.di.model.CategoryEntry;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category ORDER BY id")
    LiveData<List<CategoryEntry>> loadAllTasks();

    @Query("SELECT * FROM category WHERE id=:id")
    LiveData<CategoryEntry> loadTaskById(int id);

    @Insert
    void insertTask(CategoryEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(CategoryEntry taskEntry);

    @Delete
    void deleteTask(CategoryEntry taskEntry);


}
