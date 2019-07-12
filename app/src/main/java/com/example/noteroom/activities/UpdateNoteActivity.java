package com.example.noteroom.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.noteroom.R;

public class UpdateNoteActivity extends AppCompatActivity {

    private EditText editTextUpdateTitle;
    private EditText editTextUpdateDescription;
    private NumberPicker numberPickerUpdatePriority;
    private int noteId;
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "EXTRA_PRIORITY";
    public static final String EXTRA_ID = "EXTRA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        findViewsByIds();
        setNumberPickerLimits();
        configureActionBar();
        setValues();
    }

    private void findViewsByIds() {
        editTextUpdateTitle = findViewById(R.id.edit_text_update_title);
        editTextUpdateDescription = findViewById(R.id.edit_text_update_description);
        numberPickerUpdatePriority = findViewById(R.id.number_picker_update_priority);
    }

    private void setValues() {
        Intent data = getIntent();
        editTextUpdateTitle.setText(data.getStringExtra(AddNoteActivity.EXTRA_TITLE));
        editTextUpdateDescription.setText(data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION));
        numberPickerUpdatePriority.setValue(data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1));
        noteId = getIntent().getIntExtra(EXTRA_ID, -1);
    }

    private void setNumberPickerLimits() {
        numberPickerUpdatePriority.setMinValue(1);
        numberPickerUpdatePriority.setMaxValue(10);
    }

    private void configureActionBar() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Update Note");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_note:
                updateNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateNote() {
        String title = editTextUpdateTitle.getText().toString();
        String description = editTextUpdateDescription.getText().toString();
        int priority = numberPickerUpdatePriority.getValue();
        if (isEmptyField(title, description)) {
            Toast.makeText(this, "No empty space!", Toast.LENGTH_SHORT).show();
        }
        else {
            createIntent(title, description, priority);
        }
    }

    public boolean isEmptyField(String title, String description) {
        return title.trim().isEmpty() || description.trim().isEmpty();
    }

    private void createIntent(String title, String description, int priority) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);
        data.putExtra(EXTRA_ID, noteId);
        setResult(RESULT_OK, data);
        finish();
    }
}
