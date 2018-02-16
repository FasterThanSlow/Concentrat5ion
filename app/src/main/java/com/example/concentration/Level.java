package com.example.concentration;

/**
 * Created by Валим on 05.02.2018.
 */

public class Level {
    private int circleCount;
    private int circleCountTrue;
    private int circleRadius;
    private int circleSpeed;

    public Level(int circleCount, int circleCountTrue, int circleRadius, int circleSpeed) {
        this.circleCount = circleCount;
        this.circleCountTrue = circleCountTrue;
        this.circleRadius = circleRadius;
        this.circleSpeed = circleSpeed;
    }

    public int getCircleCount() {
        return circleCount;
    }

    public void setCircleCount(int circleCount) {
        this.circleCount = circleCount;
    }

    public int getCircleCountTrue() {
        return circleCountTrue;
    }

    public void setCircleCountTrue(int circleCountTrue) {
        this.circleCountTrue = circleCountTrue;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public int getCircleSpeed() {
        return circleSpeed;
    }

    public void setCircleSpeed(int circleSpeed) {
        this.circleSpeed = circleSpeed;
    }
}
