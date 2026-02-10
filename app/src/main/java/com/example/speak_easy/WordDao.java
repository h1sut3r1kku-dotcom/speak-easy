// app/src/main/java/com/example/speak_easy/WordDao.java
package com.example.speak_easy;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface WordDao {
    @Query("SELECT * FROM words ORDER BY id DESC")
    List<Word> getAllWords();

    @Insert
    void insert(Word word);

    @Update
    void update(Word word);

    @Delete
    void delete(Word word);
}