package com.example.kinemictestgeste;

import androidx.annotation.NonNull;

import de.kinemic.gesture.AirmousePalmDirection;

public class Movement {
    private float x;
    private float y;
    private float wrist_angle;
    private @NonNull AirmousePalmDirection facing;

    public Movement(float x, float y, float wrist_angle, @NonNull AirmousePalmDirection facing) {
        this.x = x;
        this.y = y;
        this.wrist_angle = wrist_angle;
        this.facing = facing;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWrist_angle() {
        return wrist_angle;
    }

    @NonNull
    public AirmousePalmDirection getFacing() {
        return facing;
    }
}
