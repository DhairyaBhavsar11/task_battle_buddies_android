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
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.battlebuddies.R;
import com.battlebuddies.data.AppExecutors;
import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.di.model.CategoryEntry;
import com.battlebuddies.ui.adapter.CatogoriesAdapter;
import com.battlebuddies.ui.viewmodel.CategoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class CategoriesActivity extends AppCompatActivity implements CatogoriesAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = CategoriesActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private CatogoriesAdapter mAdapter;
    private AppDatabase mDb;
    CategoryViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
       // mDb = AppDatabase.getInstance(getApplicationContext());

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CatogoriesAdapter(this, this);
        setupViewModel();
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
               // MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<CategoryEntry> tasks = mAdapter.getTasks();
//                      mDb.taskDao().deleteTask(tasks.get(position));
                        viewModel.deleteTask(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddCategoryActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddCategoryActivity
                Intent addTaskIntent = new Intent(CategoriesActivity.this, AddCategoryActivity.class);
                startActivity(addTaskIntent);
            }
        });
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<CategoryEntry>>() {
            @Override
            public void onChanged(@Nullable List<CategoryEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
                mAdapter.notifyDataSetChanged(); //optional statement. will work the same without also
            }
        });
    }
    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddCategoryActivity adding the itemId as an extra in the intent
        // COMPLETED (2) Launch AddCategoryActivity with itemId as extra for the key AddCategoryActivity.EXTRA_TASK_ID
        Intent intent = new Intent(CategoriesActivity.this, AddCategoryActivity.class);
        intent.putExtra(AddCategoryActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}