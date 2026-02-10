package com.example.speak_easy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class WordCardActivity extends AppCompatActivity {
    private EditText etWord, etTranslation, etExample;
    private TextView tvCategory;
    private Button btnSave, btnDelete;
    private AppDatabase db;
    private int wordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        db = AppDatabase.getDatabase(this);
        wordId = getIntent().getIntExtra("WORD_ID", -1);

        etWord = findViewById(R.id.et_word);
        etTranslation = findViewById(R.id.et_translation);
        etExample = findViewById(R.id.et_example);
        tvCategory = findViewById(R.id.tv_category);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);

        loadWord();

        btnSave.setOnClickListener(v -> saveWord());
        btnDelete.setOnClickListener(v -> deleteWord());
    }

    private void loadWord() {
        final int id = wordId;
        new Thread(() -> {
            Word foundWord = null;
            for (Word word : db.wordDao().getAllWords()) {
                if (word.id == id) {
                    foundWord = word;
                    break;
                }
            }
            if (foundWord != null) {
                // получаем имя категории
                String categoryName = "Без категории";
                List<Category> categories = db.categoryDao().getAllCategories();
                for (Category cat : categories) {
                    if (cat.id == foundWord.categoryId) {
                        categoryName = cat.name;
                        break;
                    }
                }
                final String finalCategoryName = categoryName;
                final Word finalFoundWord = foundWord;
                runOnUiThread(() -> {
                    etWord.setText(finalFoundWord.word);
                    etTranslation.setText(finalFoundWord.translation);
                    etExample.setText(finalFoundWord.example);
                    tvCategory.setText("Категория: " + finalCategoryName);
                });
            }
        }).start();
    }

    private void saveWord() {
        String w = etWord.getText().toString().trim();
        String t = etTranslation.getText().toString().trim();
        String e = etExample.getText().toString().trim();

        if (w.isEmpty() || t.isEmpty()) {
            Toast.makeText(this, "Слово и перевод обязательны!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            Word updatedWord = new Word(w, t, e, 1); // категория пока не редактируется
            updatedWord.id = wordId;
            db.wordDao().update(updatedWord);
            runOnUiThread(() -> {
                Toast.makeText(this, "Слово обновлено!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private void deleteWord() {
        new Thread(() -> {
            Word wordToDelete = new Word();
            wordToDelete.id = wordId;
            db.wordDao().delete(wordToDelete);
            runOnUiThread(() -> {
                Toast.makeText(this, "Слово удалено!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}