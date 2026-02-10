package com.example.speak_easy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String word;
    public String translation;
    public String example;
    public int categoryId;

    public Word() {}

    public Word(String word, String translation, String example) {
        this.word = word;
        this.translation = translation;
        this.example = example;
        this.categoryId = 1;
    }

    public Word(String word, String translation, String example, int categoryId) {
        this.word = word;
        this.translation = translation;
        this.example = example;
        this.categoryId = categoryId;
    }
}