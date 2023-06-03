package com.example.android_notepad;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.android_notepad.Adapter.NotesListAdapter;
import com.example.android_notepad.DataBase.RoomDataBase;
import com.example.android_notepad.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab_add;
    NotesListAdapter notesListAdapter;
    RoomDataBase dataBase;
    List<Notes> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Сховати статус бар
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle_home);
        fab_add = findViewById(R.id.fab_add);

        dataBase = RoomDataBase.getInstance(this);
        notes = dataBase.mainDAO().getAll();

        updateRecyclre(notes);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent, 101);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Код який я написав нище обробляє результат активності з кодом 101, якщо результат є RESULT_OK.
//        Він отримує об'єкт Notes з інтента і вставляє його в базуданних,
//        оновлює список нотаток і оновлює адаптер для відображення змін

//        Перевіргмо, чи отриманий код requestCode співпадає з кодом 101:
        if (requestCode == 101) {
//            Отримуємо об'єкт Notes з інтента, використовуючи ключ "note":
            if (resultCode == Activity.RESULT_OK) {
//                Отримуємо об'єкт Notes з інтента, використовуючи ключ "note":
                Notes new_notes = (Notes) data.getSerializableExtra("note");
//                Вставляємо отриманий об'єкт new_notes в базу даних:
                dataBase.mainDAO().insert(new_notes);
//                Очищаємо список notes, оновлюємо його з бази даних і сповіщаємо адаптер про зміни:
                notes.clear();
                notes.addAll(dataBase.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }

    }

    private void updateRecyclre(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this,notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {

        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {

        }
    };
}

























