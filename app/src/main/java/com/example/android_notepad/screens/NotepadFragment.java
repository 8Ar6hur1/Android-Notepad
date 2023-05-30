package com.example.android_notepad.screens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android_notepad.R;

public class NotepadFragment extends Fragment {

    private EditText editText;
    private Button btnSave, btnLoad;

    public NotepadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notepad, container, false);

        editText = view.findViewById(R.id.edit_text);
        btnSave = view.findViewById(R.id.btn_save);
        btnLoad = view.findViewById(R.id.btn_load);

        // обробники подій для кнопок Save і Load
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обробка натискання кнопки Save
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обробка натискання кнопки Load
            }
        });

        return view;
    }

}
