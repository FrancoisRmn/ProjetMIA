package com.example.kinemictestgeste;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.kinemictestgeste.utils.Constant;
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
        OnConnectionStateChangeListener, OnGestureListener, OnAirmouseEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Engine mEngine;
    private FloatingActionButton mFabButton;
    private Button startButton;
    private int i = 0;

    private com.github.cluelab.dollar.Gesture[] trainingSet;
    private Point[] points = new Point[50];
    private int iPoint = 0;

    private ArrayList<Movement> mouvements;
    private int currentMovement = 0;

    private MessageAdapter messageAdapter;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMovement();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEngine = getEngine();
        assert mEngine != null;
        mEngine.setRequiredGesturePrecision(RequiredGesturePrecision.LOOSE);

        mFabButton = findViewById(R.id.fabSensor);

        ListView listView = (ListView) findViewById(R.id.list);
        messageAdapter = new MessageAdapter(this);
        listView.setAdapter(messageAdapter);
        messageAdapter.add("Bienvenue dans QuidditchGO");

        startButton = findViewById(R.id.startButton);
        startButton.setVisibility(View.INVISIBLE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

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
                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(0, 0, 1), new Point(50, 0, 1)}, Constant.DROITE),
                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(0, 0, 1), new Point(-30, 0, 1)}, Constant.GAUCHE),
                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(0, 0, 1), new Point(0, -50, 1)}, Constant.BAS),
                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(0, 0, 1), new Point(0, 50, 1)}, Constant.HAUT),

                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(0, 0, 1), new Point(25, 75, 1)}, Constant.ATTRAPER),
                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(0, 0, 1), new Point(-75, -100, 1)}, Constant.LANCER),
                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(0, 0, 1), new Point(50, 0, 1), new Point(-50, 0, 1)}, Constant.COUPBATTE),
                new com.github.cluelab.dollar.Gesture(new Point[]{new Point(0, 0, 1), new Point(-50, 50, 1), new Point(50, 50, 1), new Point(-50, -50, 1), new Point(50, -50, 1),}, Constant.VIFDOR),
        };
    }

    private void initMovement() {
        mouvements = new ArrayList<>();
        mouvements.add(new Movement("Cognard à droite !! Allez à gauche !", Constant.GAUCHE));
        mouvements.add(new Movement("Cognard en haut !! Baissez-vous", Constant.BAS));
        mouvements.add(new Movement("Passe de votre poursuiveur !! Attrappez le souaffle", Constant.ATTRAPER));
        mouvements.add(new Movement("Lancez la balle pour faire une passe", Constant.LANCER));
        mouvements.add(new Movement("Un cognard arrive !! Donnez un coup de batte", Constant.COUPBATTE));
        mouvements.add(new Movement("Le vif d'or zigzague devant vous ! Formez un Z pour l'attraper", Constant.VIFDOR));
    }

    @Override
    protected void onPause() {
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
        mEngine.registerOnActivationStateChangeListener(this);
        mEngine.registerOnConnectionStateChangeListener(this);
        mEngine.registerOnAirmouseEventListener(this);
        mEngine.registerOnGestureListener(this);
    }

    @Override
    public void onActivationStateChanged(@NonNull ActivationState state) {
        Log.i(TAG, "activation state changed: " + state);
    }

    @Override
    public void onConnectionStateChanged(@NonNull ConnectionState state, @NonNull ConnectionReason reason) {
        Log.i(TAG, "connection state changed: " + state.toString() + " (" + reason.toString() + ")");
        if (state == ConnectionState.CONNECTED)
            startButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGesture(@NonNull Gesture gesture) {
        Log.i(TAG, "got gesture: " + gesture);
    }

    @Override
    public void onMove(float x, float y, float wrist_angle, @NonNull AirmousePalmDirection facing) {
        if (i == 0) {
            //movementsList.add(new Movement(x, y, wrist_angle, facing));
            Log.i(TAG, "onMove: " + x + ", " + y + ", " + "Wrist angle: " + wrist_angle + "Facing: " + facing);
            //detectMovement(movementsList);
            // acquire gesture points from user and construct the candidate gesture
            if (iPoint < 50) {
                points[iPoint] = (new Point(x, y, 1));
                iPoint++;
            } else {
                Log.i("Points overflow", "Trop de points");
            }
            i = 5;
        }
        i--;
    }


    @Override
    public void onClick() {
        Log.i(TAG, "Click event");
    }

    public void startGame() {
        messageAdapter.add("Le match va commencer !!!!!");
        startButton.setVisibility(View.INVISIBLE);
        nextMove();
    }

    private void nextMove() {
        if (currentMovement > mouvements.size() - 1)
            messageAdapter.add("Le match est gagné ! Bien joué !!!");
        else
            findMove(mouvements.get(currentMovement).getMesssage(), mouvements.get(currentMovement).getMovement());
    }

    public void findMove(String message, String move) {
        messageAdapter.add(message);
        CountDownTimer timer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                messageAdapter.add(String.valueOf((millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                messageAdapter.add("Faites le bon geste");
                mEngine.startAirmouse();
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEngine.stopAirmouse();

                        Point[] tempPoints = new Point[iPoint];
                        for (int j = 0; j < iPoint; j++) {
                            tempPoints[j] = points[j];
                        }

                        com.github.cluelab.dollar.Gesture candidate = new com.github.cluelab.dollar.Gesture(tempPoints);
                        String mouvementRecognise = PointCloudRecognizerPlus.Classify(candidate, trainingSet);
                        Log.i(TAG, "Mouvement reconnu: " + mouvementRecognise);

                        points = new Point[50];
                        iPoint = 0;

                        if (mouvementRecognise.equals(move)) {
                            mEngine.vibrate(500);
                            messageAdapter.add("Mouvement réussi");
                            currentMovement++;
                            nextMove();
                        } else {
                            messageAdapter.add("Recommencez");
                            findMove(message, move);
                        }
                    }
                }, 5000);
            }
        };
        timer.start();
    }
}