package com.battlebuddies.di.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "category")
public class CategoryEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    // COMPLETED (1) Make updatedAt match a column named updated_at. Tip: Use the ColumnInfo annotation
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public CategoryEntry(String title, Date updatedAt) {
        this.title=title;
        this.updatedAt = updatedAt;
    }

    public CategoryEntry(int id, String title  , Date updatedAt) {
        this.id = id;
        this.title = title;
        this.updatedAt = updatedAt;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
