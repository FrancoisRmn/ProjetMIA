package com.example.kinemictestgeste;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.github.cluelab.dollar.Point;
import com.github.cluelab.dollar.PointCloudRecognizerPlus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import de.kinemic.gesture.ActivationState;
import de.kinemic.gesture.AirmousePalmDirection;
import de.kinemic.gesture.ConnectionReason;
import de.kinemic.gesture.ConnectionState;
import de.kinemic.gesture.Engine;
import de.kinemic.gesture.Gesture;
import de.kinemic.gesture.OnActivationStateChangeListener;
import de.kinemic.gesture.OnAirmouseEventListener;
import de.kinemic.gesture.OnBatteryChangeListener;
import de.kinemic.gesture.OnConnectionStateChangeListener;
import de.kinemic.gesture.OnGestureListener;
import de.kinemic.gesture.OnStreamQualityChangeListener;
import de.kinemic.gesture.RequiredGesturePrecision;
import de.kinemic.gesture.common.EngineActivity;
import de.kinemic.gesture.common.fragments.BandFloatingActionButtonFragment;
import de.kinemic.gesture.common.fragments.GestureFloatingActionButtonFragment;

public class MainActivity extends EngineActivity implements OnActivationStateChangeListener,
        OnBatteryChangeListener, OnConnectionStateChangeListener, OnGestureListener, OnStreamQualityChangeListener, OnAirmouseEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Engine mEngine;

    private FloatingActionButton mFabButton;

    private boolean airmouseActive = false;
    private final boolean airmouseValid = false;
    private float airmouseX, airmouseY;
    private final AirmousePalmDirection airmouseMode = AirmousePalmDirection.INCONCLUSIVE;

    private final ArrayList<Movement> movementsList = new ArrayList<>();
    private int i = 0;
    private int iMove = 0;

    private com.github.cluelab.dollar.Gesture[] trainingSet;
    private final Point[] points = new Point[50];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEngine = getEngine();
        assert mEngine != null;
        mEngine.setRequiredGesturePrecision(RequiredGesturePrecision.LOOSE);

        mFabButton = findViewById(R.id.fabSensor);

        ListView listView = (ListView)findViewById(R.id.list);

        MessageAdapter messageAdapter = new MessageAdapter(this);

        listView.setAdapter(messageAdapter);

        messageAdapter.add("Bienvenue dans QuidditchGO");

        if (savedInstanceState == null) {
            BandFloatingActionButtonFragment bandFabFragment = BandFloatingActionButtonFragment.newInstance(BandFloatingActionButtonFragment.ConnectMode.CHOOSE);
            getSupportFragmentManager().beginTransaction().add(bandFabFragment, "band_fab").commit();
            bandFabFragment.setFloatingActionButton(mFabButton);

            GestureFloatingActionButtonFragment gestureFabFragmentDark = GestureFloatingActionButtonFragment.newInstance(true);
            getSupportFragmentManager().beginTransaction().add(gestureFabFragmentDark, "gesture_fab_dark").commit();

        } else {
            BandFloatingActionButtonFragment bandFabFragment = (BandFloatingActionButtonFragment) getSupportFragmentManager().findFragmentByTag("band_fab");
            if (bandFabFragment != null) bandFabFragment.setFloatingActionButton(mFabButton);
        }

        trainingSet = new com.github.cluelab.dollar.Gesture[]{
                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(30, 7, 1), new Point(103, 7, 1), new Point(66, 7, 2), new Point(66, 87, 2)}, "T"),
                /* todo ajouter les mouvements ici*/
        };
    }

    @Override
    protected void onPause() {
        mEngine.unregisterOnBatteryChangeListener(this);
        mEngine.unregisterOnStreamQualityChangeListener(this);
        mEngine.unregisterOnActivationStateChangeListener(this);
        mEngine.unregisterOnConnectionStateChangeListener(this);
        mEngine.unregisterOnGestureListener(this);
        mEngine.unregisterOnAirmouseEventListener(this);
        mEngine = null;

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEngine = getEngine();
        checkPermissions();

        mEngine.registerOnBatteryChangeListener(this);
        mEngine.registerOnStreamQualityChangeListener(this);
        mEngine.registerOnActivationStateChangeListener(this);
        mEngine.registerOnConnectionStateChangeListener(this);
        mEngine.registerOnAirmouseEventListener(this);
        mEngine.registerOnGestureListener(this);
    }

    @Override
    public void onBatteryChanged(int batteryPercent, boolean charging, boolean powered) {
        Log.i(TAG, "battery changed: " + batteryPercent + " (charging: " + charging + ")");
    }

    @Override
    public void onStreamQualityChanged(int quality) {
        Log.i(TAG, "Stream Quality changed: " + quality);
    }

    @Override
    public void onActivationStateChanged(@NonNull ActivationState state) {
        Log.i(TAG, "activation state changed: " + state);
    }

    @Override
    public void onConnectionStateChanged(@NonNull ConnectionState state, @NonNull ConnectionReason reason) {
        Log.i(TAG, "connection state changed: " + state.toString() + " (" + reason.toString() + ")");
    }

    @Override
    public void onGesture(@NonNull Gesture gesture) {
        mEngine.vibrate(150);
        Log.i(TAG, "got gesture: " + gesture);

        if (gesture == Gesture.ROTATE_RL) {
            if (airmouseActive) return;
            airmouseActive = true;
            mEngine.startAirmouse();
            Log.d(TAG, "airmouse started");
        } else if (gesture == Gesture.ROTATE_LR) {
            if (!airmouseActive) return;
            airmouseActive = false;
            mEngine.stopAirmouse();
            Log.d(TAG, "airmouse stopped");
        }
    }

    @Override
    public void onMove(float x, float y, float wrist_angle, @NonNull AirmousePalmDirection facing) {
        if (i == 0) {
            //movementsList.add(new Movement(x, y, wrist_angle, facing));
            Log.i(TAG, "onMove: " + x + ", " + y + ", " + "Wrist angle: " + wrist_angle + "Facing: " + facing);
            //detectMovement(movementsList);
            // acquire gesture points from user and construct the candidate gesture
            points[points.length] = new Point(x, y, iMove);
            iMove++;
            i = 5;
        }
        i--;
        if (points.length == 50) {
            mEngine.stopAirmouse();
           //movementsList.clear();
            com.github.cluelab.dollar.Gesture candidate = new com.github.cluelab.dollar.Gesture(points);

// classify the candidate gesture with the preferred recognizer
// $P+
            String gestureClass = PointCloudRecognizerPlus.Classify(candidate, trainingSet);

            Log.i(TAG, "Gesture :" + gestureClass);
        }

    }

//    public void detectMovement(ArrayList<Movement> movementsList) {
//        if (movementsList.size() == 1) {
//            Log.i(TAG, "Start position");
//        } else {
//            float distanceX;
//            float distanceY;
//            final int DISTANCE_BETWEEN_MVMT = 5;
//            Movement movementA = movementsList.get(iMove);
//            iMove++;
//            Movement movementB = movementsList.get(iMove);
//
//            distanceX = Math.abs(movementA.getX()) - Math.abs(movementB.getX());
//            distanceY = Math.abs(movementA.getY()) - Math.abs(movementB.getY());
//
//            if (distanceX < DISTANCE_BETWEEN_MVMT && distanceX > -DISTANCE_BETWEEN_MVMT) {
//                Log.i(TAG, "Not moving X axes");
//            } else {
//                if (movementA.getX() > movementB.getX()) {
//                    Log.i(TAG, "Going left");
//                } else
//                    Log.i(TAG, "Going right");
//            }
//
//            if (distanceY < DISTANCE_BETWEEN_MVMT && distanceY > -DISTANCE_BETWEEN_MVMT) {
//                Log.i(TAG, "Not moving Y axes");
//            } else {
//                if (movementA.getY() > movementB.getY()) {
//                    Log.i(TAG, "Going down");
//                } else
//                    Log.i(TAG, "Going up");
//            }
//        }
//    }

    @Override
    public void onClick() {
        Log.i(TAG, "Click event");
    }
}