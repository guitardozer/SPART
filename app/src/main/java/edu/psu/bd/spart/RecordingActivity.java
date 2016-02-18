package edu.psu.bd.spart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.Tango.OnTangoUpdateListener;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoCameraPreview;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;

import java.util.ArrayList;


public class RecordingActivity extends AppCompatActivity {


    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String sTranslationFormat = "Translation: %f, %f, %f";
    private static final String sRotationFormat = "Rotation: %f, %f, %f, %f";

    private static final int SECS_TO_MILLISECS = 1000;
    private static final double UPDATE_INTERVAL_MS = 100.0;

    private double mPreviousTimeStamp;
    private double mTimeToNextUpdate = UPDATE_INTERVAL_MS;

    private TextView mTranslationTextView;
    private TextView mRotationTextView;

    private Tango mTango;
    private TangoConfig mConfig;
    private boolean mIsConnected;

    private TangoCameraPreview tangoCameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recording);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitModel(view);
            }
        });

        tangoCameraPreview = (TangoCameraPreview)findViewById(R.id.videoOverLay);
        // Instantiate Tango client
        mTango = new Tango(this);
        //setContentView(tangoCameraPreview);


        // Set up Tango configuration for motion tracking
        // If you want to use other APIs, add more appropriate to the config
        // like: mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH, true)
        mConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_MOTIONTRACKING, true);

    }

    // Camera Preview
    private void startCameraPreview() {
        // Connect to color camera
        tangoCameraPreview.connectToTangoCamera(mTango,
                TangoCameraIntrinsics.TANGO_CAMERA_COLOR);
        // Use default configuration for Tango Service.
        TangoConfig config = mTango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);
        mTango.connect(config);
        mIsConnected = true;

        // No need to add any coordinate frame pairs since we are not using
        // pose data. So just initialize.
        ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();
        mTango.connectListener(framePairs, new OnTangoUpdateListener() {
            @Override
            public void onPoseAvailable(TangoPoseData pose) {
                // We are not using OnPoseAvailable for this app
            }

            @Override
            public void onFrameAvailable(int cameraId) {

                // Check if the frame available is for the camera we want and
                // update its frame on the camera preview.
                if (cameraId == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
                    tangoCameraPreview.onFrameAvailable();
                }
            }

            @Override
            public void onXyzIjAvailable(TangoXyzIjData xyzIj) {
                // We are not using OnPoseAvailable for this app
            }

            @Override
            public void onTangoEvent(TangoEvent event) {
                // We are not using OnPoseAvailable for this app
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Lock the Tango configuration and reconnect to the service each time
        // the app
        // is brought to the foreground.

        try {
            if (!mIsConnected) {
                startCameraPreview();
            }
        } catch (TangoOutOfDateException e) {
            //Toast.makeText(getApplicationContext(), R.string.TangoOutOfDateException,
              //      Toast.LENGTH_SHORT).show();
        } catch (TangoErrorException e) {
            //Toast.makeText(getApplicationContext(), R.string.TangoError, Toast.LENGTH_SHORT).show();
        }
        if (!mIsConnected) {
            try {
                setTangoListeners();
            } catch (TangoErrorException e) {
                Toast.makeText(this, "Tango Error! Restart the app!",
                        Toast.LENGTH_SHORT).show();
            }
            try {
                mTango.connect(mConfig);
                mIsConnected = true;
            } catch (TangoOutOfDateException e) {
                Toast.makeText(getApplicationContext(),
                        "Tango Service out of date!", Toast.LENGTH_SHORT)
                        .show();
            } catch (TangoErrorException e) {
                Toast.makeText(getApplicationContext(),
                        "Tango Error! Restart the app!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsConnected) {
            tangoCameraPreview.disconnectFromTangoCamera();
            mIsConnected = false;
        }
        // When the app is pushed to the background, unlock the Tango
        // configuration and disconnect
        // from the service so that other apps will behave properly.
        try {
            mTango.disconnect();
            mIsConnected = false;
        } catch (TangoErrorException e) {
            Toast.makeText(getApplicationContext(), "Tango Error!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setTangoListeners() {
        // Select coordinate frame pairs
        ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<TangoCoordinateFramePair>();
        framePairs.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                TangoPoseData.COORDINATE_FRAME_DEVICE));

        // Add a listener for Tango pose data
        mTango.connectListener(framePairs, new Tango.OnTangoUpdateListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onPoseAvailable(TangoPoseData pose) {
                // Format Translation and Rotation data
                final String translationMsg = String.format(sTranslationFormat,
                        pose.translation[0], pose.translation[1],
                        pose.translation[2]);
                final String rotationMsg = String.format(sRotationFormat,
                        pose.rotation[0], pose.rotation[1], pose.rotation[2],
                        pose.rotation[3]);

                // Output to LogCat
                String logMsg = translationMsg + " | " + rotationMsg;
                Log.i(TAG, logMsg);

                final double deltaTime = (pose.timestamp - mPreviousTimeStamp)
                        * SECS_TO_MILLISECS;
                mPreviousTimeStamp = pose.timestamp;
                mTimeToNextUpdate -= deltaTime;

                // Throttle updates to the UI based on UPDATE_INTERVAL_MS.
                if (mTimeToNextUpdate < 0.0) {
                    mTimeToNextUpdate = UPDATE_INTERVAL_MS;

                    // Display data in TextViews. This must be done inside a
                    // runOnUiThread call because
                    // it affects the UI, which will cause an error if performed
                    // from the Tango
                    // service thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRotationTextView.setText(rotationMsg);
                            mTranslationTextView.setText(translationMsg);
                        }
                    });
                }
            }

            @Override
            public void onXyzIjAvailable(TangoXyzIjData arg0) {
                // Ignoring XyzIj data
            }

            @Override
            public void onTangoEvent(TangoEvent arg0) {
                // Ignoring TangoEvents
            }

            @Override
            public void onFrameAvailable(int arg0) {
                // Ignoring onFrameAvailable Events

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording, menu);
        return true;
    }

    public void transitModel(View view){
        Intent intent = new Intent(this, ModelActivity.class);
        startActivity(intent);
    }
}
