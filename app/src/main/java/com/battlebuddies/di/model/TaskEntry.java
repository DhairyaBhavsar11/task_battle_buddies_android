package com.battlebuddies.di.model;


import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task")
public class TaskEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private int categoryId;
    private int patentTaskId;
    // COMPLETED (1) Make updatedAt match a column named updated_at. Tip: Use the ColumnInfo annotation
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public TaskEntry(String title,String description,int categoryId,int patentTaskId, Date updatedAt) {
        this.title=title;
        this.description = description;
        this.categoryId = categoryId;
        this.patentTaskId = patentTaskId;
        this.updatedAt = updatedAt;
    }

    public TaskEntry(int id, String title, String description,int categoryId,int patentTaskId, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.patentTaskId = patentTaskId;
        this.updatedAt = updatedAt;
    }

    public int getPatentTaskId() {
        return patentTaskId;
    }

    public void setPatentTaskId(int patentTaskId) {
        this.patentTaskId = patentTaskId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
