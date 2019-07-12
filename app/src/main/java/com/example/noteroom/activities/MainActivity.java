package com.example.noteroom.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.noteroom.model.Note;
import com.example.noteroom.adapter.NoteAdapter;
import com.example.noteroom.viewmodel.NoteViewModel;
import com.example.noteroom.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_NOTE_REQUEST = 1;
    public static final int UPDATE_NOTE_REQUEST = 2;
    private NoteViewModel noteViewModel;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    final NoteAdapter noteAdapter = new NoteAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsByIds();
        configureFloatingActionButtonListener();
        configureRecyclerView();
        setNoteAdapterClickListener();
        configureNoteViewModel();
        connectItemTouchHelperToRecyclerView();
    }

    private void findViewsByIds() {
        floatingActionButton = findViewById(R.id.button_add_note);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void configureFloatingActionButtonListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(noteAdapter);
    }

    private void setNoteAdapterClickListener() {
        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, UpdateNoteActivity.class);
                intent.putExtra(AddNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddNoteActivity.EXTRA_PRIORITY, note.getPriority());
                intent.putExtra(AddNoteActivity.EXTRA_ID, note.getId());
                startActivityForResult(intent, UPDATE_NOTE_REQUEST);
            }
        });
    }

    private void configureNoteViewModel() {
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.setNotes(notes);
            }
        });
    }

    private void connectItemTouchHelperToRecyclerView() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isRequestFromAddActivity(requestCode, resultCode)) {
            insertNoteIntoViewModel(data);
        }
        else if (isRequestFromUpdateActivity(requestCode, resultCode)) {
            updateNoteIntoViewModel(data);
        }
    }

    private boolean isRequestFromAddActivity(int requestCode, int resultCode) {
        return requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK;
    }

    private boolean isRequestFromUpdateActivity(int requestCode, int resultCode) {
        return requestCode == UPDATE_NOTE_REQUEST && resultCode == RESULT_OK;
    }

    private void insertNoteIntoViewModel(@Nullable Intent data) {
        String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
        String description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
        int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1);
        Note note = new Note(title, description, priority);
        noteViewModel.insert(note);
    }

    private void updateNoteIntoViewModel(@Nullable Intent data) {
        int id = data.getIntExtra(UpdateNoteActivity.EXTRA_ID, -1);
        String title = data.getStringExtra(UpdateNoteActivity.EXTRA_TITLE);
        String description = data.getStringExtra(UpdateNoteActivity.EXTRA_DESCRIPTION);
        int priority = data.getIntExtra(UpdateNoteActivity.EXTRA_PRIORITY, 1);
        Note note = new Note(title, description, priority);
        note.setId(id);
        noteViewModel.update(note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_allnotes:
                noteViewModel.deleteAllNotes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
