package com.example.speak_easy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WordAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация базы
        db = AppDatabase.getDatabase(this);

        // Добавление категорий при первом запуске
        new Thread(() -> {
            if (db.categoryDao().getAllCategories().isEmpty()) {
                db.categoryDao().insert(new Category("Быт"));
                db.categoryDao().insert(new Category("Еда"));
                db.categoryDao().insert(new Category("Глаголы"));
                db.categoryDao().insert(new Category("Природа"));
                db.categoryDao().insert(new Category("Техника"));
            }
        }).start();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordAdapter();
        recyclerView.setAdapter(adapter);

        TextInputEditText searchView = findViewById(R.id.et_search);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadAllWords();

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddWordActivity.class)));
    }

    private void loadAllWords() {
        new Thread(() -> {
            List<Word> words = db.wordDao().getAllWords();
            runOnUiThread(() -> {
                adapter.setWords(words);
                findViewById(R.id.tv_no_words).setVisibility(words.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }).start();
    }

    private void filter(String query) {
        new Thread(() -> {
            List<Word> allWords = db.wordDao().getAllWords();
            List<Word> filtered = new ArrayList<>();
            for (Word word : allWords) {
                if (word.word.toLowerCase().contains(query.toLowerCase()) ||
                        word.translation.toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(word);
                }
            }
            runOnUiThread(() -> {
                adapter.setWords(filtered);
                findViewById(R.id.tv_no_words).setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllWords();
    }
}