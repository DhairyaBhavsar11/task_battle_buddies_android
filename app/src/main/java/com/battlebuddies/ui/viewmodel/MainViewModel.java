package com.battlebuddies.ui.viewmodel;

import android.app.Application;
import android.util.Log;
import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.di.model.TaskEntry;
import com.battlebuddies.di.manager.TasksRepository;

import java.util.List;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private final TasksRepository tasksRepository;
    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        //tasks = database.taskDao().loadAllTasks();
        tasksRepository = new TasksRepository(database);
    }

    public LiveData<List<TaskEntry>> getTasks() {
        return tasksRepository.getloadAllTasks();
    }

    public void deleteTask(TaskEntry taskEntry) {
        tasksRepository.deleteTasks(taskEntry);
    }

    public LiveData<List<TaskEntry>> searchTask(String str){
        return str.isEmpty() ? tasksRepository.getloadAllTasks() : tasksRepository.searchTask(str);
    }

    public LiveData<List<TaskEntry>> filterByDate(){
        return tasksRepository.filterByDate();
    }

    public LiveData<List<TaskEntry>> filterByName(){
        return tasksRepository.filterByName();
    }
    public LiveData<List<TaskEntry>> getChildTasks(int id) {
        return tasksRepository.loadAllChildTasks(id);
    }
}