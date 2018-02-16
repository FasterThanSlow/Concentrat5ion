package com.example.concentration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.vecmath.Vector2d;

/**
 * Created by Валим on 31.01.2018.
 *
 * View Алгоритма Тренажёра "Концентрация"
 */

@SuppressLint("ViewConstructor")
public class MovementView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public static final int CIRCLES_RUN_TIME = 4000;
    public static final int CIRCLES_GRAY_RUN_TIME = 2000;
    public static final int SHOW_ANSWER_DELAY = 150;
    public static final int SHOW_ANSWER = 1500;
    public static final int ONE_TICK = 10;
    public static final int FPS = 60;

    private int width;
    private int height;

    private int wrongAnswers = 0;
    private int correctAnswers = 0;
    private int currentGameCount = 0;
    private int inCorrectGameCount = 0;
    private int correctGameCount = 0;
    private int answersCount = 0;
    private long time;

    private Paint circlePaintGrey = new Paint();
    private Paint circlePaintBlue = new Paint();
    private Paint circlePaintRed = new Paint();

    private CountDownTimer timerAll;
    private CountDownTimer timerGray;

    private boolean isRun = false;
    private boolean isLocked = false;
    private boolean isAllGrey = false;
    private boolean isAnswered = false;

    private Thread thread = new Thread(this);
    private Handler handler = new Handler();

    private Level currentLevel;
    private List<MovingCircle> circles;

    public MovementView(Context context, int startCount, int speed, int radius) {
        super(context);

        getHolder().addCallback(this);

        circlePaintGrey.setColor(Color.GRAY);
        circlePaintBlue.setColor(Color.BLUE);
        circlePaintRed.setColor(Color.RED);

        currentLevel = new Level(startCount, startCount / 2, radius, speed);

        if(currentLevel.getCircleCount() >= 16 && correction){
            currentLevel.setCircleRadius(currentLevel.getCircleRadius() - currentLevel.getCircleRadius() / 5);
            correction = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.WHITE);
        for (MovingCircle circle : circles) {
            if (isAllGrey)
                circle.draw(canvas, circlePaintGrey);
            else {
                if(isAnswered)
                    circle.drawAnswered(canvas);
                else
                    circle.draw(canvas);
            }
        }
    }

    public void updatePhysics() {
        final int size = circles.size();

        for (MovingCircle circle : circles) {
            circle.setNextPos();
        }

        for (int i = 0; i <  size; i++) {
            MovingCircle firstCircle = circles.get(i);

            for (int j = i + 1; j < size; j++) {
                MovingCircle secondCircle = circles.get(j);

                if(firstCircle.isCollisionWithCircle(secondCircle)){
                    firstCircle.collisionWithCircle(secondCircle);
                }
            }

            firstCircle.collisionWithWalls(width,height);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Rect surfaceFrame = holder.getSurfaceFrame();
        width = surfaceFrame.width();
        height = surfaceFrame.height();

        circles = initCircles(currentLevel);

        drawToScreen();
        startThread();
    }

    private void startThread(){
        isRun = true;

        startTimerToAllGrey(CIRCLES_RUN_TIME);

        thread = new Thread(this);
        thread.start();
    }

    private void startTimerToAllGrey(long time){
        timerAll = new CountDownTimer(time, ONE_TICK){
            public void onTick(long millisUntilDone){

            }

            public void onFinish() {
                showAllGrey();
            }
        }.start();
    }

    private void showAllGrey(){
        isAllGrey = true;
        startTimerStop(CIRCLES_GRAY_RUN_TIME);
    }

    private void startTimerStop(long time){
        timerGray = new CountDownTimer(time, ONE_TICK){
            public void onTick(long millisUntilDone){

            }

            public void onFinish() {
                stopGame();
            }
        }.start();
    }

    private void stopGame(){
        isAllGrey = true;
        isRun = false;

        boolean retry = true;

        while(retry){
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        drawToScreen();
    }

    private void drawToScreen(){
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas(null);
        }
        finally {
            if(canvas != null){
                draw(canvas);
                getHolder().unlockCanvasAndPost(canvas);
            }
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    private void cancelGame(){
        isRun = false;

        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        handler.removeCallbacks(showCorrectAnswer);
        handler.removeCallbacks(showInCorrectAnswer);
        handler.removeCallbacks(restartLevel);
        handler.removeCallbacks(startNextLevel);

        if(timerAll != null)
            timerAll.cancel();
        if(timerGray != null)
            timerGray.cancel();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        cancelGame();
    }

    private List<MovingCircle> initCircles(Level level){
        List<MovingCircle> result = new ArrayList<>();
        final Random random = new Random();

        int bufferBlue = level.getCircleCountTrue();
        for (int i = 0; i < level.getCircleCount(); i++) {
            if (bufferBlue > 0 ) {
                result.add(createCircle(level.getCircleRadius(), level.getCircleSpeed(), circlePaintBlue, random));
                bufferBlue--;
            }
            else{
                result.add(createCircle(level.getCircleRadius(), level.getCircleSpeed(), circlePaintGrey, random));
            }
        }

        final int size = result.size();

        for (int i = 0; i <  size; i++) {
            MovingCircle firstCircle = result.get(i);

            for (int j = 0; j < size; j++) {

                if(i == j)
                    continue;

                MovingCircle secondCircle = result.get(j);

                if(firstCircle.isCollisionWithCircle(secondCircle)){
                    firstCircle.setPosOutCircle(secondCircle, width , height);
                }
            }

            firstCircle.collisionWithWalls(width,height);
        }

        return result;
    }

    private MovingCircle createCircle(int radius, int speed, Paint paint, Random random){
        MovingCircle result = new MovingCircle();

        result.setRadius(radius);
        result.setPosition(new Vector2d(random.nextInt(width) + radius, random.nextInt(height) + radius));
        result.setSpeed(new Vector2d(speed,speed));
        result.setPaint(paint);

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isRun && !isLocked) {
                performClick();
                double x = event.getX();
                double y = event.getY();

                checkTouchCircles(x, y);
                drawToScreen();

                if(answersCount == currentLevel.getCircleCountTrue()) {
                    isLocked = true;
                    if (correctAnswers == currentLevel.getCircleCountTrue()) {
                        handler.postDelayed(showCorrectAnswer, SHOW_ANSWER_DELAY);
                        correctGameCount++;
                        inCorrectGameCount = 0;
                    }

                    if (wrongAnswers > 0) {
                        handler.postDelayed(showInCorrectAnswer, SHOW_ANSWER_DELAY);
                        correctGameCount = 0;
                        inCorrectGameCount++;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private boolean correction = true;

    private void getNextLevel(boolean isCorrectAnswered){
        currentGameCount++;

        if(isCorrectAnswered && correctGameCount == 3){
            currentLevel.setCircleCount(currentLevel.getCircleCount() + 2);
            currentLevel.setCircleCountTrue(currentLevel.getCircleCountTrue() + 1);
            correctGameCount = 0;
            inCorrectGameCount = 0;
        }
        else if(inCorrectGameCount == 3 && currentLevel.getCircleCount() > 4){
            currentLevel.setCircleCount(currentLevel.getCircleCount() - 2);
            currentLevel.setCircleCountTrue(currentLevel.getCircleCountTrue() - 1);
            correctGameCount = 0;
            inCorrectGameCount = 0;
        }

        if(currentLevel.getCircleCount() >= 16 && correction){
            currentLevel.setCircleRadius(currentLevel.getCircleRadius() - currentLevel.getCircleRadius() / 5);
            correction = false;
        }
    }

    private void startLevel(){
        circles = initCircles(currentLevel);

        isRun = true;
        wrongAnswers = 0;
        correctAnswers = 0;
        answersCount = 0;
        isAnswered = false;
        isAllGrey = false;

        startThread();
    }

    private void checkTouchCircles(double x, double y) {
        for (MovingCircle circle : circles) {
            if (circle.isPointIn(x, y)) {
                answersCount++;
                if (circle.getPaint().getColor() == Color.BLUE) {
                    circle.setAnswerPaint(circle.getPaint());
                    correctAnswers++;

                } else {
                    circle.setAnswerPaint(circlePaintRed);
                    wrongAnswers++;
                }
            } else {
                if (circle.getAnswerPaint() == null) {
                    circle.setAnswerPaint(circlePaintGrey);
                }
            }
        }

        isAllGrey = false;
        isAnswered = true;
    }

    private Runnable showInCorrectAnswer = new Runnable(){
        @Override
        public void run() {

            isAllGrey = false;
            isAnswered = false;
            drawToScreen();

            handler.postDelayed(restartLevel, SHOW_ANSWER);
        }
    };

    private Runnable restartLevel = new Runnable() {
        @Override
        public void run() {
            isLocked = false;
            getNextLevel(false);
            startLevel();
        }
    };

    private Runnable startNextLevel = new Runnable() {
        @Override
        public void run() {
            isLocked = false;
            getNextLevel(true);
            startLevel();
        }
    };

    private Runnable showCorrectAnswer = new Runnable() {
        @Override
        public void run() {
            Paint correctPaint = new Paint();
            correctPaint.setColor(Color.GREEN);

            for (MovingCircle circle : circles) {
                if(circle.getPaint().getColor() == Color.BLUE){
                    circle.setPaint(correctPaint);
                }
            }

            isAnswered = false;
            drawToScreen();

            handler.postDelayed(startNextLevel, SHOW_ANSWER);
        }
    };

    @Override
    public void run() {
        while (isRun) {
            long cTime = System.currentTimeMillis();

            if ((cTime - time) <= (1000 / FPS)) {
                updatePhysics();
                drawToScreen();
            }

            time = cTime;
        }
    }
}
