package com.example.android_notepad;

import androidx.cardview.widget.CardView;

import com.example.android_notepad.Models.Notes;

public interface NotesClickListener {

    void onClick (Notes notes);
    void onLongClick (Notes notes, CardView cardView);

}
