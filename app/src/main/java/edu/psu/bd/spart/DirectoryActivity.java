package edu.psu.bd.spart;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;


import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DirectoryActivity extends AppCompatActivity {

    private static String DNAME = "testfiles";
    private static String FILENAME = "data";

    EditText mEditDirectory, mEditFilename;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFile();
            }
        });

        //mButton = (Button)findViewById(R.id.button);
        mEditDirectory   = (EditText)findViewById(R.id.edit_directory);
        mEditFilename   = (EditText)findViewById(R.id.edit_filename);

       /* mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Log.d("EditDirectory", mEditDirectory.getText().toString());
                        DNAME=mEditDirectory.getText().toString();
                        Log.d("EditFilename", mEditFilename.getText().toString());
                        FILENAME=mEditFilename.getText().toString();
                    }
                });
*/
        mEditFilename.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        mEditDirectory.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveFile()
    {
        String timestamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        if(mEditFilename.getText().toString().matches(""))
        {
            FILENAME ="data"+"_"+timestamp+".txt";
        }
        else
        {
            FILENAME = mEditFilename.getText().toString()+"_"+timestamp+".txt";
        }
        if(mEditDirectory.getText().toString().matches(""))
        {
            DNAME ="testfiles";
        }
        else {
            DNAME = mEditDirectory.getText().toString() + "_" + timestamp + ".txt";
        }

        File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), DNAME);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
            Log.d("mylog", "Created directory "+Environment.DIRECTORY_DOCUMENTS+"/"+DNAME);
            Toast.makeText(this, "Created directory " + Environment.DIRECTORY_DOCUMENTS+"/"+DNAME, Toast.LENGTH_SHORT).show();
        }
        File dataFile = new File(rootPath, FILENAME);
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Cannot use storage.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        try {
            FileOutputStream mOutput = new FileOutputStream(dataFile, false);
            String data = "File was created at " + timestamp +".";
            mOutput.write(data.getBytes());
            mOutput.close();

            Log.d("mylog", "Created file " + Environment.DIRECTORY_DOCUMENTS + "/" + DNAME + "/" + FILENAME);
            Toast.makeText(this, "Created file " + FILENAME, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileInputStream mInput = new FileInputStream(dataFile);
            byte[] data = new byte[128];
            mInput.read(data);
            mInput.close();

            //String display = new String(data);
            //R.layout.activity_test.setText(display.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditDirectory.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mEditFilename.getWindowToken(), 0);
    }
}


