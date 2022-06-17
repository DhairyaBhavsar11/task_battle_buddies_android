package com.battlebuddies.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.di.manager.CategoriesRepository;
import com.battlebuddies.di.model.CategoryEntry;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = CategoryViewModel.class.getSimpleName();

    private LiveData<List<CategoryEntry>> tasks;
    private final CategoriesRepository tasksRepository;
    public CategoryViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        //tasks = database.taskDao().loadAllTasks();
        tasksRepository = new CategoriesRepository(database);
        tasks = tasksRepository.getloadAllTasks();
    }

    public LiveData<List<CategoryEntry>> getTasks() {
        return tasks;
    }

    public void deleteTask(CategoryEntry taskEntry) {
        tasksRepository.deleteTasks(taskEntry);
    }
}