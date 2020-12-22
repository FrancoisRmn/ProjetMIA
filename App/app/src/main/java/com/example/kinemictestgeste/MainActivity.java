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

import de.kinemic.gesture.AirmousePalmDirection;
import de.kinemic.gesture.ConnectionReason;
import de.kinemic.gesture.ConnectionState;
import de.kinemic.gesture.Engine;
import de.kinemic.gesture.OnAirmouseEventListener;
import de.kinemic.gesture.OnConnectionStateChangeListener;
import de.kinemic.gesture.common.EngineActivity;
import de.kinemic.gesture.common.fragments.BandFloatingActionButtonFragment;

public class MainActivity extends EngineActivity implements OnConnectionStateChangeListener, OnAirmouseEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Engine bandEngine;
    private Button startButton;
    private com.github.cluelab.dollar.Gesture[] trainingSet;
    private ArrayList<Movement> movements;
    private MessageAdapter messageAdapter;
    private Point[] points = new Point[50];
    private int iPoint = 0; //Indice du tableau points
    private int iterator = 0; //Permet de récupérer un point sur 5 parmi tous les points renvoyés par le bracelet.
    private int currentMovement = 0; //Retiens la position du geste dans le scénario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton mFabButton = findViewById(R.id.fabSensor);
        ListView listView = (ListView) findViewById(R.id.list);
        startButton = findViewById(R.id.startButton);

        initMovements(); //Initialise les mouvements du scénario

        setSupportActionBar(toolbar);

        bandEngine = getEngine();
        assert bandEngine != null;

        messageAdapter = new MessageAdapter(this);//Création d'un adapter pour la liste de message du scénario
        listView.setAdapter(messageAdapter);
        messageAdapter.add("Bienvenue dans QuidditchGO");

        startButton.setVisibility(View.INVISIBLE);
        startButton.setOnClickListener(event -> startGame());

        if (savedInstanceState == null) { //Ajoute le fragment fourni par la librairie pour connecter le bracelet
            BandFloatingActionButtonFragment bandFabFragment = BandFloatingActionButtonFragment.newInstance(BandFloatingActionButtonFragment.ConnectMode.CHOOSE);
            getSupportFragmentManager().beginTransaction().add(bandFabFragment, "band_fab").commit();
            bandFabFragment.setFloatingActionButton(mFabButton);
        } else {
            BandFloatingActionButtonFragment bandFabFragment = (BandFloatingActionButtonFragment) getSupportFragmentManager().findFragmentByTag("band_fab");
            if (bandFabFragment != null) bandFabFragment.setFloatingActionButton(mFabButton);
        }

        trainingSet = new com.github.cluelab.dollar.Gesture[]{ //Définition des mouvements pour la librairie p$+
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

    private void initMovements() {
        movements = new ArrayList<>();
        movements.add(new Movement("Cognard à droite !! Allez à gauche !", Constant.GAUCHE));
        movements.add(new Movement("Cognard en haut !! Baissez-vous", Constant.BAS));
        movements.add(new Movement("Passe de votre poursuiveur !! Attrappez le souaffle", Constant.ATTRAPER));
        movements.add(new Movement("Lancez la balle pour faire une passe", Constant.LANCER));
        movements.add(new Movement("Un cognard arrive !! Donnez un coup de batte", Constant.COUPBATTE));
        movements.add(new Movement("Le vif d'or zigzague devant vous ! Formez un Z pour l'attraper", Constant.VIFDOR));
    }

    @Override
    protected void onPause() {
        bandEngine.unregisterOnConnectionStateChangeListener(this);
        bandEngine.unregisterOnAirmouseEventListener(this);
        bandEngine = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bandEngine = getEngine();
        checkPermissions();
        bandEngine.registerOnConnectionStateChangeListener(this);
        bandEngine.registerOnAirmouseEventListener(this);
    }

    @Override
    public void onConnectionStateChanged(@NonNull ConnectionState state, @NonNull ConnectionReason reason) {
        if (state == ConnectionState.CONNECTED)
            startButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMove(float x, float y, float wrist_angle, @NonNull AirmousePalmDirection facing) { //Méthode appelé par le bracelet chaque fois qu'un point est renvoyé
        if (iterator == 0) {
            Log.i(TAG, "onMove: " + x + ", " + y + ", " + "Wrist angle: " + wrist_angle + "Facing: " + facing);
            // acquire gesture points from user and construct the candidate gesture
            if (iPoint < 50) {
                points[iPoint] = (new Point(x, y, 1));
                iPoint++;
            } else {
                Log.i("Points overflow", "Trop de points");
            }
            iterator = 5;
        }
        iterator--;
    }

    @Override
    public void onClick() {

    }

    public void startGame() {
        currentMovement = 0;
        messageAdapter.add("Le match va commencer !!!!!");
        startButton.setVisibility(View.INVISIBLE);
        nextMove();
    }

    private void nextMove() {
        if (currentMovement > movements.size() - 1) {
            messageAdapter.add("Le match est gagné ! Bien joué !!!");
            //le bouton pour recommencer reapparait.
            startButton.setVisibility(View.VISIBLE);
        } else
            findMove(movements.get(currentMovement).getMessage(), movements.get(currentMovement).getMovement());
    }

    public void findMove(String message, String move) {
        messageAdapter.add(message);
        CountDownTimer timer = new CountDownTimer(4000, 1000) { //Permet d'afficher un décompte
            @Override
            public void onTick(long millisUntilFinished) {
                messageAdapter.add(String.valueOf((millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                messageAdapter.add("Faites le bon geste");
                bandEngine.startAirmouse();
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    bandEngine.stopAirmouse();

                    Point[] tempPoints = new Point[iPoint]; //Permet de créer un tableau de la taille du nombre de points reçus sans cellule vide pour la librairie
                    for (int j = 0; j < iPoint; j++) {
                        tempPoints[j] = points[j];
                    }

                    com.github.cluelab.dollar.Gesture candidate = new com.github.cluelab.dollar.Gesture(tempPoints);
                    String mouvementRecognise = PointCloudRecognizerPlus.Classify(candidate, trainingSet); //Appelle de la librairie pour reconnaitre le mouvement
                    Log.i(TAG, "Mouvement reconnu: " + mouvementRecognise);

                    points = new Point[50];
                    iPoint = 0;

                    if (mouvementRecognise.equals(move)) {
                        bandEngine.vibrate(500);
                        messageAdapter.add("Mouvement réussi");
                        currentMovement++;
                        nextMove();
                    } else {
                        messageAdapter.add("Recommencez");
                        findMove(message, move);
                    }
                }, 5000);
            }
        };
        timer.start();
    }
}