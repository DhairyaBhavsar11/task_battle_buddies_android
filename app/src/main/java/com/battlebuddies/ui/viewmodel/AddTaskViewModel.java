package com.battlebuddies.ui.viewmodel;

import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.di.manager.CategoriesRepository;
import com.battlebuddies.di.model.CategoryEntry;
import com.battlebuddies.di.model.TaskEntry;
import com.battlebuddies.di.manager.TasksRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class AddTaskViewModel extends ViewModel {

    // COMPLETED (6) Add a task member variable for the TaskEntry object wrapped in a LiveData
    private LiveData<TaskEntry> task;
    private final TasksRepository tasksRepository;
    private final CategoriesRepository categoriesRepository;

    // COMPLETED (8) Create a constructor where you call loadTaskById of the taskDao to initialize the tasks variable
    // Note: The constructor should receive the database and the taskId
    public AddTaskViewModel(AppDatabase database, int taskId) {
       // task = database.taskDao().loadTaskById(taskId);
        tasksRepository = new TasksRepository(database);
        categoriesRepository= new CategoriesRepository(database);
    }

    // COMPLETED (7) Create a getter for the task variable
    public LiveData<TaskEntry> getTask(int mTaskId) {
        task = tasksRepository.getloadTaskById(mTaskId);
        return task;
    }

    public void updateTask(TaskEntry task) {
        tasksRepository.updateTaskById(task);
    }

    public LiveData<List<CategoryEntry>> getAllCategories(){
        return categoriesRepository.getloadAllTasks();
    }


}
