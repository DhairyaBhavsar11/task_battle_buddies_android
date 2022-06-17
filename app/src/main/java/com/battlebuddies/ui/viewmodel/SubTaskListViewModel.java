package com.battlebuddies.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.di.manager.TasksRepository;
import com.battlebuddies.di.model.TaskEntry;

import java.util.List;

public class SubTaskListViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = SubTaskListViewModel.class.getSimpleName();

    private final TasksRepository tasksRepository;
    public SubTaskListViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        //tasks = database.taskDao().loadAllTasks();
        tasksRepository = new TasksRepository(database);
    }

    public LiveData<List<TaskEntry>> getChildTasks(int id) {
        return tasksRepository.loadAllChildTasks(id);
    }

    public void deleteTask(TaskEntry taskEntry) {
        tasksRepository.deleteTasks(taskEntry);
    }
}