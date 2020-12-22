package com.example.kinemictestgeste;

public class Movement {
    private String message;
    private String movement;

    public Movement(String message, String movement) {
        this.message = message;
        this.movement = movement;
    }

    public String getMessage() {
        return message;
    }

    public String getMovement() {
        return movement;
    }
}
