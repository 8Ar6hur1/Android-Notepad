package com.example.android_notepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.android_notepad.Adapter.NotesListAdapter;
import com.example.android_notepad.DataBase.RoomDataBase;
import com.example.android_notepad.Models.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    FloatingActionButton fab_add;
    NotesListAdapter notesListAdapter;
    RoomDataBase dataBase;
    List<Notes> notes = new ArrayList<>();
    SearchView searchView_home;
    Notes selectNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Сховати статус бар
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle_home);
        fab_add = findViewById(R.id.fab_add);

        searchView_home = findViewById(R.id.searchView_home);

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

        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Notes newNote = (Notes) data.getSerializableExtra("note");
                dataBase.mainDAO().insert(newNote);
                notes.clear();
                notes.addAll(dataBase.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Notes updatedNote = (Notes) data.getSerializableExtra("note");
                dataBase.mainDAO().update(updatedNote.getID(), updatedNote.getTitle(), updatedNote.getNotes());
                notes.clear();
                notes.addAll(dataBase.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }


    private void updateRecyclre(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectNote = new Notes();
            selectNote = notes;
            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {

        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.pin) {
            if (selectNote.isPinned()) {
                dataBase.mainDAO().pin(selectNote.getID(), false);
                Toast.makeText(MainActivity.this, "Відкріплено", Toast.LENGTH_SHORT).show();
            } else {
                dataBase.mainDAO().pin(selectNote.getID(), true);
                Toast.makeText(MainActivity.this, "Прикріплено", Toast.LENGTH_SHORT).show();
            }
            notes.clear();
            notes.addAll(dataBase.mainDAO().getAll());
            notesListAdapter.notifyDataSetChanged();
            return true;
        } else if (itemId == R.id.delete) {
            dataBase.mainDAO().delete(selectNote);
            notes.remove(selectNote);
            notesListAdapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Note removed", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}
