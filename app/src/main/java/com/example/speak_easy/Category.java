package com.example.speak_easy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }
}