package edu.psu.bd.spart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class FileList extends AppCompatActivity {
    View rootView;
    String sendit;
    ArrayList<String> messageArray = new ArrayList<String>();

    ListView argumentList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        String[] myStringArray = {"File 1", "File 2", "File 3", "File 4", "File 5", "File 6"};
        ArrayAdapter<String> myAdapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.activity_file_list_item,
                        R.id.FileName,
                        myStringArray);
        ListView myList = (ListView) findViewById(R.id.FileList);

        AdapterView.OnItemClickListener
                mMessageClickedHandler =
                new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        Log.d("hello", sendit);
                    }
                };
        myList.setAdapter(myAdapter);
    }
}
