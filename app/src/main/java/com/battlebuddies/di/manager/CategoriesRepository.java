package com.battlebuddies.di.manager;

import androidx.lifecycle.LiveData;

import com.battlebuddies.di.dao.CategoryDao;
import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.di.model.CategoryEntry;

import java.util.List;

public class CategoriesRepository {
    private static final String LOG_TAG = CategoriesRepository.class.getSimpleName();
    private LiveData<List<CategoryEntry>> categories;
    private CategoryDao categoryDao;
    AppDatabase database;
    public CategoriesRepository(AppDatabase database) {
        this.database = database;
    }


    public LiveData<List<CategoryEntry>> getloadAllTasks() {
        categories = database.categoryDao().loadAllTasks();
        return categories;
    }


    public LiveData<CategoryEntry> getloadTaskById(int taskId) {
        return database.categoryDao().loadTaskById(taskId);
    }

    public void deleteTasks(CategoryEntry taskEntry) {
        database.categoryDao().deleteTask(taskEntry);
    }

    public void updateTaskById(CategoryEntry task) {
        database.categoryDao().updateTask(task);
    }
}
