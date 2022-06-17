package com.battlebuddies.di.dao;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.battlebuddies.di.model.TaskEntry;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task ORDER BY id")
    LiveData<List<TaskEntry>> loadAllTasks();

    @Query("SELECT * FROM task WHERE id=:id")
    LiveData<TaskEntry> loadTaskById(int id);

    @Query("SELECT * FROM task WHERE title LIKE '%' || :str || '%'")
    LiveData<List<TaskEntry>> searchTask(String str);

    @Query("SELECT * FROM task ORDER BY updated_at DESC")
    LiveData<List<TaskEntry>> filterByDate();

    @Query("SELECT * FROM task ORDER BY title")
    LiveData<List<TaskEntry>> filterByName();

    @Insert
    void insertTask(TaskEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntry taskEntry);

    @Delete
    void deleteTask(TaskEntry taskEntry);


}
