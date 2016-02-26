package com.codemorning.attendancepro;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alina on 2/23/2016.
 */
public class StudentActivity extends AppCompatActivity {

    private List<Student> students;
    private List<Student> attStudents;
    ArrayAdapter<Student> adapter;
    ListView lv;
    ProgressBar pb;
    Intent intent;
    String classTitle;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            loadLoginView();
        }

        pb = (ProgressBar)findViewById(R.id.progressbar);
        students = new ArrayList<Student>();
        attStudents = new ArrayList<Student>();
        adapter = new ArrayAdapter<Student>(this, R.layout.student_item_layout, students);
        lv = (ListView)findViewById(R.id.list);
        intent = this.getIntent();
        classTitle = intent.getStringExtra("classTitle");
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)view.findViewById(R.id.tv);
                tv.setText(tv.getText().toString() + " selected");
                tv.setCompoundDrawablesRelative(null,null,getDrawable(R.drawable.ic_action_refresh),null);
                Student studentObj = students.get(position);
                attStudents.add(studentObj);
            }
        });

        submitButton = (Button)findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAttend();
            }
        });

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Student studentObj = students.get(position);
//                Intent intent2 = new Intent(StudentActivity.this, EditStudentActivity.class);
//                intent2.putExtra("studentId", studentObj.getId());
//                intent2.putExtra("studentTitle", studentObj.getTitle());
//                intent2.putExtra("classTitle", "");
//                startActivity(intent2);
//            }
//        });

        refreshStudentList();

    }

    public void sendAttend(){
        for (int i=0; i<attStudents.size();i++) {
            Student studentObj = attStudents.get(i);
            ParseObject studentObj0 = new ParseObject("Attendance");
            studentObj0.put("classTitle", classTitle);
            studentObj0.put("studentTitle", studentObj.getTitle());
            studentObj0.put("teacher", ParseUser.getCurrentUser());

            studentObj0.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Saved successfully.
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
//                            classObj = new Class(classObj0.getObjectId(), classTitle, postContent);
                    } else {
                        // The save failed.
                        Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                        Log.d(getClass().getSimpleName(), "User update error: " + e);
                    }
                }
            });
        }

    }


    private void refreshStudentList() {
        pb.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
        query.whereEqualTo("teacher", ParseUser.getCurrentUser()).whereEqualTo("classTitle",classTitle);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> studentList, ParseException e) {
                pb.setVisibility(View.GONE);
                if (e == null) {
                    // If there are results, update the list of students
                    // and notify the adapter
                    students.clear();
                    for (ParseObject studentObj2 : studentList) {
                        Student studentObj = new Student(studentObj2.getObjectId(), studentObj2.getString("studentTitle"),classTitle);
                        students.add(studentObj);
                    }
                    adapter.notifyDataSetChanged();
                } else {

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_refresh: {
                refreshStudentList();
                break;
            }

            case R.id.action_new_student: {
                Intent intent = new Intent(this, EditStudentActivity.class);
                intent.putExtra("classTitle",classTitle);
                startActivity(intent);
                break;
            }
            case R.id.action_logout: {
                ParseUser.logOut();
                loadLoginView();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    private void loadLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
