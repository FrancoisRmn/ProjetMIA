package com.example.kinemictestgeste;

import androidx.annotation.NonNull;

import de.kinemic.gesture.AirmousePalmDirection;

public class Movement {
    private String messsage;
    private String movement;

    public Movement(String messsage, String movement) {
        this.messsage = messsage;
        this.movement = movement;
    }

    public String getMesssage() {
        return messsage;
    }

    public String getMovement() {
        return movement;
    }
}
