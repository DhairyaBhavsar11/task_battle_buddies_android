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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.battlebuddies.R;
import com.battlebuddies.ui.adapter.TaskAdapter;
import com.battlebuddies.di.database.AppDatabase;
import com.battlebuddies.data.AppExecutors;
import com.battlebuddies.di.model.TaskEntry;
import com.battlebuddies.ui.viewmodel.MainViewModel;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private AppDatabase mDb;
    MainViewModel viewModel;
    private CompositeDisposable disposable = new CompositeDisposable();
    private EditText edSearch;
    private ImageView ivFilter;
    private boolean isSearchClear = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // mDb = AppDatabase.getInstance(getApplicationContext());

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);
        edSearch = findViewById(R.id.edSearch);
        ivFilter = findViewById(R.id.ivFilter);

        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,v);
                popupMenu.inflate(R.menu.filter_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.filter_date:
                                isSearchClear = true;
                                edSearch.setText("");
                                viewModel.filterByDate().observe(MainActivity.this, new Observer<List<TaskEntry>>() {
                                    @Override
                                    public void onChanged(List<TaskEntry> taskEntries) {
                                        mAdapter.setTasks(taskEntries);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                break;
                            case R.id.filter_name:
                                isSearchClear = true;
                                edSearch.setText("");
                                viewModel.filterByName().observe(MainActivity.this, new Observer<List<TaskEntry>>() {
                                    @Override
                                    public void onChanged(List<TaskEntry> taskEntries) {
                                        mAdapter.setTasks(taskEntries);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, this);
        setupViewModel();
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        /*new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
                        List<TaskEntry> tasks = mAdapter.getTasks();
//                      mDb.taskDao().deleteTask(tasks.get(position));
                        viewModel.deleteTask(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);*/

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        disposable.add(RxTextView.textChangeEvents(edSearch)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchContacts()));
    }

    private DisposableObserver<TextViewTextChangeEvent> searchContacts() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                if (!isSearchClear) {
                    Log.d(TAG, "Search query: " + textViewTextChangeEvent.text());
                    viewModel.searchTask(textViewTextChangeEvent.text().toString()).observe(MainActivity.this, new Observer<List<TaskEntry>>() {
                        @Override
                        public void onChanged(List<TaskEntry> taskEntries) {
                            mAdapter.setTasks(taskEntries);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }else {
                    isSearchClear = false;
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
                mAdapter.notifyDataSetChanged(); //optional statement. will work the same without also
            }
        });
    }
    private boolean isClickItem = false;
    @Override
    public void onItemClickListener(final int itemId, final int parentId, final String title) {
        isClickItem = true;
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        // COMPLETED (2) Launch AddTaskActivity with itemId as extra for the key AddTaskActivity.EXTRA_TASK_ID
        viewModel.getChildTasks(itemId).observe(MainActivity.this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                if (isClickItem) {
                    isClickItem = false;
                    viewModel.getChildTasks(itemId).removeObserver(this);
                    if (taskEntries != null) {
                        if (taskEntries.size() > 0) {
                            Intent intent = new Intent(MainActivity.this, SubTaskListActivity.class);
                            intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
                            intent.putExtra("name", title);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                            intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
                            startActivity(intent);
                        }
                    }
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_setting:
                Intent intent = new Intent(MainActivity.this,CategoriesActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}