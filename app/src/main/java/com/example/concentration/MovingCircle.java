package com.example.concentration;

import android.graphics.Canvas;
import android.graphics.Paint;
import javax.vecmath.Vector2d;

/**
 * Created by Валим on 31.01.2018.
 *
 * Двигающийся круг
 */

public class MovingCircle {
    private static final int BORDER_COEFFICENT = 7;

    private Vector2d position;
    private Vector2d speed;

    private final int mass = 10;
    private int radius;
    private int border;

    private Paint paint;
    private Paint answerPaint;

    public MovingCircle(Vector2d position, Vector2d speed, int radius){
        this.position = position;
        this.radius = radius;
        this.speed = speed;
        this.border = radius / BORDER_COEFFICENT;
    }

    public void setNextPos(){
        this.position.x += this.speed.x;
        this.position.y += this.speed.y;
    }

    public boolean isOnWallX(int width){
        if (this.position.x - this.radius < 0 || this.position.x + this.radius > width) {
            return true;
        }
        return false;
    }

    public boolean isOnWallY(int height){
        if (this.position.y - this.radius < 0 || this.position.y + this.radius > height) {
            return true;
        }
        return false;
    }

    public void setWallPosX(int width){
        if (this.position.x - this.radius < 0) {
           this.position.x = this.radius;
        } else {
            this.position.x = width - this.radius;
        }
        this.setOppositeSpeedX();
    }

    public void setOppositeSpeedX(){
        this.speed.x *= -1;
    }

    public void setWallPosY(int height){
        if (this.position.y - this.radius < 0) {
           this.position.y = this.radius;
        }else{
            this.position.y = height - this.radius;
        }
        this.setOppositeSpeedY();
    }

    public void setOppositeSpeedY(){
        this.speed.y *= -1;
    }

    public boolean isCollisionWithCircle(MovingCircle circle){
        double radiusSum = getSumRadius(circle);
        double distance = getDistance(circle.getPosition().x, circle.getPosition().y);

        if(distance <= radiusSum){
            return true;
        }

        return false;
    }

    public void collisionWithCircle(MovingCircle circle){
        double xDist = this.position.x - circle.getPosition().x;
        double yDist = this.position.y - circle.getPosition().y;
        double distSquared = xDist*xDist + yDist*yDist;

        double xVelocity = circle.getSpeed().x - this.speed.x;
        double yVelocity = circle.getSpeed().y - this.speed.y;

        double dotProduct = xDist*xVelocity + yDist*yVelocity;

        if(dotProduct > 0){
            double collisionScale = dotProduct / distSquared;
            double xCollision = xDist * collisionScale;
            double yCollision = yDist * collisionScale;

            double combinedMass = this.mass + circle.getMass();
            double collisionWeightA = 2 * circle.getMass() / combinedMass;
            double collisionWeightB = 2 * this.mass  / combinedMass;

            this.speed.x += collisionWeightA * xCollision;
            this.speed.y += collisionWeightA * yCollision;

            circle.speed.x -= collisionWeightB * xCollision;
            circle.speed.y -= collisionWeightB * yCollision;
        }
    }

    public boolean collisionWithWalls(int width, int height){
        if(this.isOnWallY(height)){
            this.setWallPosY(height);
            return true;
        }

        if(this.isOnWallX(width)){
            this.setWallPosX(width);
            return true;
        }

        return false;
    }

    private double getDistance(double x, double y){
        return Math.hypot(this.position.x - x, this.position.y - y);
    }

    private double getSumRadius(MovingCircle circle){
        return this.radius + circle.getRadius();
    }

    public int getMass() {
        return mass;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.border = radius / BORDER_COEFFICENT;
    }

    public MovingCircle(){

    }

    public Vector2d getPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public Vector2d getSpeed() {
        return speed;
    }

    public void setSpeed(Vector2d speed) {
        this.speed = speed;
    }

    public Paint getAnswerPaint() {
        return answerPaint;
    }

    public void setAnswerPaint(Paint answerPaint) {
        this.answerPaint = answerPaint;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public boolean isPointIn(double x, double y){
        double distance = getDistance(x, y);

        if(distance <= this.radius){
            return true;
        }

        return false;
    }

    public void draw(Canvas canvas){
        canvas.drawCircle((float) this.position.x,(float) this.position.y, this.radius - this.border, this.paint);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawCircle((float) this.position.x,(float) this.position.y, this.radius - this.border, paint);
    }

    public void drawAnswered(Canvas canvas){
        canvas.drawCircle((float) this.position.x,(float) this.position.y, this.radius - this.border, this.answerPaint);
    }

    public void setPosOutCircle(MovingCircle circle, int width, int height){
        double x, y;

        if (circle.getPosition().x >= this.position.x && getPosForCircleRight(circle) >= width) {
            x = getPosForCircleLeft(circle);
        }
        else if(circle.getPosition().x >= this.position.x && getPosForCircleRight(circle) < width) {
            x = getPosForCircleRight(circle);
        }
        else if(circle.getPosition().x < this.position.x && getPosForCircleLeft(circle) <= 0) {
            x = getPosForCircleRight(circle);
        }
        else {
            x = getPosForCircleLeft(circle);
        }

        if (circle.getPosition().y >= this.position.y && getPosForCircleBottom(circle) >= height) {
            y = getPosForCircleTop(circle);
        }
        else if(circle.getPosition().y >= this.position.y && getPosForCircleBottom(circle) < height) {
            y = getPosForCircleBottom(circle);
        }
        else if(circle.getPosition().y < this.position.y && getPosForCircleTop(circle) <= 0) {
            y = getPosForCircleBottom(circle);
        }
        else {
            y = getPosForCircleTop(circle);
        }

        circle.setPosition(new Vector2d(x, y));
    }

    private double getPosForCircleLeft(MovingCircle circle){
        return this.position.x - circle.getRadius() - this.radius - this.border;
    }

    private double getPosForCircleRight(MovingCircle circle){
        return this.position.x + circle.getRadius() + this.radius + this.border;
    }

    private double getPosForCircleTop(MovingCircle circle){
        return this.position.y - circle.getRadius() - this.radius - this.border;
    }

    private double getPosForCircleBottom(MovingCircle circle){
        return this.position.y + circle.getRadius() + this.radius + this.border;
    }


}
