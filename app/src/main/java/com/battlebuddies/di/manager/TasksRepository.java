package com.battlebuddies.di.manager;

import java.util.List;

import androidx.lifecycle.LiveData;

import com.battlebuddies.di.dao.TaskDao;
import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.di.model.TaskEntry;

public class TasksRepository  {
    private static final String LOG_TAG = TasksRepository.class.getSimpleName();
    private LiveData<List<TaskEntry>> tasks;
    private TaskDao taskDao;
    AppDatabase database;
    public TasksRepository(AppDatabase database) {
        this.database = database;
    }


    public LiveData<List<TaskEntry>> getloadAllTasks() {
        tasks = database.taskDao().loadAllTasks();
        return tasks;
    }


    public LiveData<TaskEntry> getloadTaskById(int taskId) {
        return database.taskDao().loadTaskById(taskId);
    }

    public void deleteTasks(TaskEntry taskEntry) {
        database.taskDao().deleteTask(taskEntry);
    }

    public void deleteTasks(int id) {
        database.taskDao().deleteTask(id);
    }

    public void updateTaskById(TaskEntry task) {
        database.taskDao().updateTask(task);
    }

    public LiveData<List<TaskEntry>> searchTask(String str) {
        return database.taskDao().searchTask(str);
    }

    public LiveData<List<TaskEntry>> filterByDate() {
        return database.taskDao().filterByDate();
    }

    public LiveData<List<TaskEntry>> filterByName() {
        return database.taskDao().filterByName();
    }
    public LiveData<List<TaskEntry>> loadAllChildTasks(int id) {
        return database.taskDao().loadAllChildTasks(id);
    }
}
