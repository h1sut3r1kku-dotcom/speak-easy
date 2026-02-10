package com.example.speak_easy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AddWordActivity extends AppCompatActivity {
    private EditText etWord, etTranslation, etExample;
    private AutoCompleteTextView actvCategory;
    private Button btnSave, btnCancel;
    private AppDatabase db;
    private List<Category> categories;
    private int selectedCategoryId = 1;
    private String customCategoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        db = AppDatabase.getDatabase(this);
        etWord = findViewById(R.id.et_word);
        etTranslation = findViewById(R.id.et_translation);
        etExample = findViewById(R.id.et_example);
        actvCategory = findViewById(R.id.actv_category);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        loadCategories();

        btnSave.setOnClickListener(v -> saveWord());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadCategories() {
        new Thread(() -> {
            categories = db.categoryDao().getAllCategories();
            runOnUiThread(() -> {
                if (!categories.isEmpty()) {
                    List<String> names = new ArrayList<>();
                    for (Category cat : categories) {
                        names.add(cat.name);
                    }
                    names.add("Другое…");

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_dropdown_item_1line, names);
                    actvCategory.setAdapter(adapter);
                    actvCategory.setText(names.get(0), false);
                    selectedCategoryId = categories.get(0).id;

                    actvCategory.setOnClickListener(v -> {
                        actvCategory.showDropDown();
                    });

                    actvCategory.setOnItemClickListener((parent, view, position, id) -> {
                        if (position == names.size() - 1) {
                            showCustomCategoryDialog();
                        } else {
                            selectedCategoryId = categories.get(position).id;
                            customCategoryName = "";
                        }
                    });
                }
            });
        }).start();
    }

    private void showCustomCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Новая категория");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Введите название");
        input.setTextColor(getResources().getColor(android.R.color.white));
        builder.setView(input);

        builder.setPositiveButton("Создать", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                new Thread(() -> {
                    Category newCat = new Category(name);
                    db.categoryDao().insert(newCat);
                    runOnUiThread(() -> {
                        actvCategory.setText(name, false);
                        customCategoryName = name;
                        selectedCategoryId = -1; // флаг кастомной категории
                    });
                }).start();
            } else {
                Toast.makeText(this, "Название не может быть пустым", Toast.LENGTH_SHORT).show();
                showCustomCategoryDialog();
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> {
            dialog.cancel();
            if (!categories.isEmpty()) {
                actvCategory.setText(categories.get(0).name, false);
                selectedCategoryId = categories.get(0).id;
                customCategoryName = "";
            }
        });

        builder.show();
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
            int categoryIdToSave;
            if (selectedCategoryId == -1 && !customCategoryName.isEmpty()) {
                // поиск ID только что созданной категории
                List<Category> all = db.categoryDao().getAllCategories();
                for (Category c : all) {
                    if (c.name.equals(customCategoryName)) {
                        categoryIdToSave = c.id;
                        db.wordDao().insert(new Word(w, t, e, categoryIdToSave));
                        break;
                    }
                }
            } else {
                categoryIdToSave = selectedCategoryId;
                db.wordDao().insert(new Word(w, t, e, categoryIdToSave));
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Слово сохранено!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}