/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.battlebuddies.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.battlebuddies.R;
import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.data.AppExecutors;
import com.battlebuddies.di.model.CategoryEntry;
import com.battlebuddies.di.model.TaskEntry;
import com.battlebuddies.ui.viewmodel.AddTaskViewModel;
import com.battlebuddies.ui.viewmodel.AddTaskViewModelFactory;

import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


public class AddTaskActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();
    // Fields for views
    EditText editTextTitle,editTextDescription;
    Button mButton;

    private int mTaskId = DEFAULT_TASK_ID;

    // Member variable for the Database
    private AppDatabase mDb;
    AddTaskViewModel viewModel;
    private Spinner spinnerCategories;
    private String[] categories;
    List<CategoryEntry> mCategoryEntries;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }
        AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);
        viewModel = ViewModelProviders.of(this, factory).get(AddTaskViewModel.class);
        viewModel.getAllCategories().observe(this, new Observer<List<CategoryEntry>>() {
            @Override
            public void onChanged(List<CategoryEntry> categoryEntries) {
                mCategoryEntries = categoryEntries;
                viewModel.getAllCategories().removeObserver(this);
                if (categoryEntries.size() > 0) {
                    categories = new String[categoryEntries.size()];
                    for (int i = 0; i < categoryEntries.size(); i++) {
                        categories[i] = categoryEntries.get(i).getTitle();
                    }
                    ArrayAdapter aa = new ArrayAdapter(AddTaskActivity.this, android.R.layout.simple_spinner_item, categories);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    spinnerCategories.setAdapter(aa);
                }

                Intent intent = getIntent();
                if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
                    mButton.setText(R.string.update_button);
                    if (mTaskId == DEFAULT_TASK_ID) {
                        mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                        viewModel.getTask(mTaskId).observe(AddTaskActivity.this, new Observer<TaskEntry>() {
                            @Override
                            public void onChanged(TaskEntry taskEntry) {
                                viewModel.getTask(mTaskId).removeObserver(this);
                                populateUI(taskEntry);
                            }
                        });
                    }
                }
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerCategories = findViewById(R.id.spinnerCategories);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        // COMPLETED (7) return if the task is null
        if (task == null) {
            return;
        }

        // COMPLETED (8) use the variable task to populate the UI
        editTextTitle.setText(task.getTitle());
        editTextDescription.setText(task.getDescription());
        if (task.getCategoryId() != -1 && mCategoryEntries != null){
            for (int i = 0; i < mCategoryEntries.size(); i++) {
                if (mCategoryEntries.get(i).getId() == task.getCategoryId()){
                    spinnerCategories.setSelection(i);
                }
            }
        }
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int categoryId = -1;
        if (categories != null && categories.length > 0){
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(spinnerCategories.getSelectedItem().toString())){
                    categoryId = i+1;
                }
            }

        }
        Date date = new Date();

        final TaskEntry task = new TaskEntry(title,description,categoryId, date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // COMPLETED (9) insert the task only if mTaskId matches DEFAULT_TASK_ID
                // Otherwise update it
                // call finish in any case
                if (mTaskId == DEFAULT_TASK_ID) {
                    // insert new task
                    mDb.taskDao().insertTask(task);
                } else {
                    //update task
                    task.setId(mTaskId);
                    viewModel.updateTask(task);
//                    mDb.taskDao().updateTask(task);
                }
                finish();
            }
        });
    }
}