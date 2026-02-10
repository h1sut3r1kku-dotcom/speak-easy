// app/src/main/java/com/example/speak_easy/WordAdapter.java
package com.example.speak_easy;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {
    private List<Word> words = new ArrayList<>();

    public void setWords(List<Word> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word current = words.get(position);
        holder.textViewWord.setText(current.word);
        holder.textViewTranslation.setText(current.translation);

        // Клик по карточке
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), WordCardActivity.class);
            intent.putExtra("WORD_ID", current.id);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewWord;
        private TextView textViewTranslation;

        public WordViewHolder(View itemView) {
            super(itemView);
            textViewWord = itemView.findViewById(R.id.text_view_word);
            textViewTranslation = itemView.findViewById(R.id.text_view_translation);
        }
    }
}