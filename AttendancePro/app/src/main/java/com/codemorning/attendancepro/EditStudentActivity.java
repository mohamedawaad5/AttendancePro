package com.codemorning.attendancepro;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by alina on 2/23/2016.
 */
public class EditStudentActivity extends AppCompatActivity {

    private Student studentObj;
    private EditText titleEditText;
    private String studentTitle;
    private String classTitle;
    private Button saveButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = this.getIntent();

        titleEditText = (EditText) findViewById(R.id.titleEt);

        if (intent.getStringExtra("classTitle").equals("")) {
            studentObj = new Student(intent.getStringExtra("studentId"), intent.getStringExtra("studentTitle"), intent.getStringExtra("classTitle"));
            titleEditText.setText(studentObj.getTitle());
        }

        saveButton = (Button)findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStudent();
                finish();
            }
        });

    }

    private void saveStudent() {

        studentTitle = titleEditText.getText().toString();
        studentTitle = studentTitle.trim();
        classTitle = getIntent().getStringExtra("classTitle");
        classTitle = classTitle.trim();
        // If user doesn't enter a title or content, do nothing
        // If user enters title, but no content, save
        // If user enters content with no title, give warning
        // If user enters both title and content, save

        if (!studentTitle.isEmpty()) {

            // Check if student is being created or edited

            if (studentObj == null) {
                // create new student

                final ParseObject studentObj0 = new ParseObject("Student");
                studentObj0.put("classTitle", classTitle);
                studentObj0.put("studentTitle", studentTitle);
                studentObj0.put("teacher", ParseUser.getCurrentUser());
                studentObj0.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Saved successfully.
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                            studentObj = new Student(studentObj0.getObjectId(),studentTitle,classTitle);
                        } else {
                            // The save failed.
                            Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getSimpleName(), "User update error: " + e);
                        }
                    }
                });

            }
            else {
                // update student

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");

                // Retrieve the object by id
                query.getInBackground(studentObj.getId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject studentObj0, ParseException e) {
                        if (e == null) {
                            // Now let's update it with some new data.
                            studentObj0.put("studentTitle", studentTitle);
                            studentObj0.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Saved successfully.
                                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // The save failed.
                                        Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                                        Log.d(getClass().getSimpleName(), "User update error: " + e);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
        else if (studentTitle.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditStudentActivity.this);
            builder.setMessage(R.string.edit_error_message)
                    .setTitle(R.string.edit_error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}
