package com.battlebuddies.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.di.manager.CategoriesRepository;
import com.battlebuddies.di.model.CategoryEntry;

public class AddCategoryViewModel extends ViewModel {

    // COMPLETED (6) Add a task member variable for the TaskEntry object wrapped in a LiveData
    private LiveData<CategoryEntry> task;
    private final CategoriesRepository tasksRepository;

    // COMPLETED (8) Create a constructor where you call loadTaskById of the taskDao to initialize the tasks variable
    // Note: The constructor should receive the database and the taskId
    public AddCategoryViewModel(AppDatabase database, int taskId) {
       // task = database.taskDao().loadTaskById(taskId);
        tasksRepository = new CategoriesRepository(database);
        task = tasksRepository.getloadTaskById(taskId);
    }

    // COMPLETED (7) Create a getter for the task variable
    public LiveData<CategoryEntry> getTask() {
        return task;
    }

    public void updateTask(CategoryEntry task) {
        tasksRepository.updateTaskById(task);
    }
}
