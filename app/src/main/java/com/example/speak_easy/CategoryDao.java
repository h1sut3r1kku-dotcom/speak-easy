package com.example.speak_easy;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    List<Category> getAllCategories();

    @Insert
    void insert(Category category);
}