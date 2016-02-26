package com.codemorning.attendancepro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ClassActivity extends AppCompatActivity {

    private List<Class> classes;
    ArrayAdapter<Class> adapter;
    ListView lv;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            loadLoginView();
        }

        pb = (ProgressBar)findViewById(R.id.progressbar);
        classes = new ArrayList<Class>();
        adapter = new ArrayAdapter<Class>(this, R.layout.class_item_layout, classes);
        lv = (ListView)findViewById(R.id.list);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class classObj = classes.get(position);
                Intent intent = new Intent(ClassActivity.this, StudentActivity.class);
                intent.putExtra("classTitle", classObj.getTitle());
                startActivity(intent);
            }
        });

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Class classObj = classes.get(position);
//                Intent intent = new Intent(ClassActivity.this, EditClassActivity.class);
//                intent.putExtra("classId", classObj.getId());
//                intent.putExtra("classTitle", classObj.getTitle());
//                startActivity(intent);
//            }
//        });

        refreshClassList();

    }


    private void refreshClassList() {
        pb.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Class");
        query.whereEqualTo("teacher", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> classList, ParseException e) {
                pb.setVisibility(View.GONE);
                if (e == null) {
                    // If there are results, update the list of classes
                    // and notify the adapter
                    classes.clear();
                    for (ParseObject classObj2 : classList) {
                        Class classObj = new Class(classObj2.getObjectId(), classObj2.getString("classTitle"));
                        classes.add(classObj);
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
        getMenuInflater().inflate(R.menu.class_menu, menu);
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
                refreshClassList();
                break;
            }

            case R.id.action_new_class: {
                Intent intent = new Intent(this, EditClassActivity.class);
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
