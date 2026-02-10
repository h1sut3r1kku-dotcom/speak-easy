package com.example.speak_easy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "test")
public class TestRoom {
    @PrimaryKey(autoGenerate = true)
    public int id;
}