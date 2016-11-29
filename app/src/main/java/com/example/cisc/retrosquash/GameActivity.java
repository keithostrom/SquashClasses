
package com.example.cisc.retrosquash;

        import android.app.Activity;
        import android.content.Context;
        import android.content.res.AssetFileDescriptor;
        import android.content.res.AssetManager;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Point;
        import android.media.AudioManager;
        import android.media.SoundPool;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Display;
        import android.view.KeyEvent;
        import android.view.MotionEvent;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.widget.Toast;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Random;

public class GameActivity extends Activity {

    Canvas canvas;
    SquashCourtView squashCourtView;

    private SoundPool soundPool;
    int soundNewBall    = -1;
    int soundMissedBall = -1;
    int soundBounceWall = -1;
    int soundPaddle     = -1;


    Display display;
    Point screenSize;
    int screenWidth;
    int screenHeight;

    Paddle racketBottom;
    Paddle racketTop;
    ArrayList theBallList = new ArrayList(100);
    Point paddleSize;
    Point ballSize;
    Point sittingStill;

    // stats
    long lastFrameTime = 0;
    long timeNow;
    long timeThisFrame;
    int fps;
    int fpsLastFrame = 0;
    int avgFps = 1;
    int score;
    int lives;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("info","GameActivity OnCreate starting......");


        squashCourtView = new SquashCourtView(this);
        setContentView(squashCourtView);

        paddleSize   = new Point(80,20);
        ballSize     = new Point(20,20);
        sittingStill = new Point(0,0);
        racketBottom = new Paddle(paddleSize,sittingStill);
        racketTop = new Paddle(paddleSize,sittingStill);
        Log.i("info","GameActivity OnCreate before display 1");

        display = getWindowManager().getDefaultDisplay();
        Log.i("info","GameActivity OnCreate before display 2");


        Point anotherPoint = new Point();
        display.getSize(anotherPoint); //One size for all (static)
        screenSize = anotherPoint;
        Log.i("info","GameActivity OnCreate before display 3");


        GamePart.setScreenSize(screenSize);
        Log.i("info","GameActivity OnCreate before display 4");
        racketBottom.setPosition( new Point(Paddle.screenSize.x/2,Paddle.screenSize.y-50));
        racketTop.setPosition( new Point(Paddle.screenSize.x/2,50));

        Log.i("info","GameActivity OnCreate before display 5");
        Random myRandom = new Random();
        Ball firstBall = new Ball(ballSize,sittingStill);
        Log.i("info","GameActivity OnCreate before display 5.1");
        float tempfloatX = ((myRandom.nextFloat()*40)-20);
        Log.i("info","tempfloat X "+tempfloatX);
        firstBall.velocityX = tempfloatX;
        Log.i("info","GameActivity OnCreate before display 5.2");

        float tempfloatY = ((myRandom.nextFloat()*40)-20);
        Log.i("info","tempfloat Y "+tempfloatY);
        firstBall.velocityY = tempfloatY;
        Log.i("info","GameActivity OnCreate before display 5.3");
        //firstBall.setPosition(new Point(25,46));
        firstBall.setPosition( new Point(
                myRandom.nextInt(Ball.screenSize.x-firstBall.mySize.x)+(firstBall.mySize.x/2),
                myRandom.nextInt(Ball.screenSize.y-firstBall.mySize.y)+(firstBall.mySize.y/2) ));
        Log.i("info","GameActivity OnCreate before display 5.4");
        //firstBall.position.y =  myRandom.nextInt(Ball.screenSize.y/4)+Ball.screenSize.y/2;
        Log.i("info","GameActivity OnCreate before display 5.5");
        Log.i("info","GameActivity OnCreate before display 6");

        theBallList.add(firstBall);
        Log.i("info","GameActivity OnCreate before display 7");
        //load sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("newball.wav");
            soundNewBall = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("missedball.wav");
            soundMissedBall = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("bouncewall.wav");
            soundBounceWall = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("paddle.wav");
            soundPaddle = soundPool.load(descriptor, 0);
        } catch (IOException e) {
            // Maybe put toast to let user know something is not right
            Toast.makeText(getApplicationContext(),"Trouble loading sounds.",Toast.LENGTH_LONG).show();
        }


        if(MainActivity.checkBoxObjectMute.isChecked() == false) {
            soundPool.play(soundNewBall, 1, 1, 0, 0, 1);
        }
        lives = 3;
        Log.i("info","end of GameActivity.onCreate");
    } // End onCreate

    class SquashCourtView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSquash;
        Paint paint;

        // Constructor
        public SquashCourtView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
        } // End public SquashCourtView

        @Override
        public void run() {
            while ( playingSquash) {
                updateCourt();
                drawCourt();
                controlFPS();
            }
        } // End public void run

        public void updateCourt() {
        for( int a=0;a<theBallList.size();a++) {
            Ball theBall = (Ball) theBallList.get(a);
            theBall.DoMove();
            theBallList.set(a,theBall);
        }

        } // End public void updateCourt

        public void drawCourt() {
            if( ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                //Paint paint = new Paint();
                paint.setColor(Color.argb(255, 0, 0, 0)); // the background
                canvas.drawRect(0,0,GamePart.screenSize.x,GamePart.screenSize.y,paint);

                // Outline screen
                paint.setColor(Color.argb(255, 255, 60, 180));
                canvas.drawLine(0, 0, Paddle.screenSize.x-1,0,paint);
                canvas.drawLine(Paddle.screenSize.x-1,0,Paddle.screenSize.x-1,Paddle.screenSize.y-1,paint);
                canvas.drawLine(Paddle.screenSize.x-1,Paddle.screenSize.y-1,0,Paddle.screenSize.y-1, paint);
                canvas.drawLine(0,Paddle.screenSize.y-1,0,0,paint);

                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + " Lives: " + lives + " fps: " + fps,
                        15, GamePart.screenSize.x/2, paint);

                //Draw the squash racket1
                paint.setColor(Color.argb(255, 25, 255, 255));
                canvas.drawRect(racketBottom.position.x - (racketBottom.mySize.x / 2),  // Left
                        racketBottom.position.y - (racketBottom.mySize.y / 2),          // Top
                        racketBottom.position.x + (racketBottom.mySize.x / 2),          // Right
                        racketBottom.position.y + (racketBottom.mySize.y / 2),          // Bottom
                        paint);                                  // Bitmap

                //Draw the squash racket2
                paint.setColor(Color.argb(255, 255, 25, 255));
                canvas.drawRect(racketTop.position.x - (racketTop.mySize.x / 2),  // Left
                        racketTop.position.y - (racketTop.mySize.y / 2),          // Top
                        racketTop.position.x + (racketTop.mySize.x / 2),          // Right
                        racketTop.position.y + (racketTop.mySize.y / 2),          // Bottom
                        paint);                                  // Bitmap

                // draw the ball
                paint.setColor(Color.argb(255, 255, 25, 25));

                for( int a=0;a<theBallList.size();a++) {
                    Ball theBall = (Ball) theBallList.get(a);
                    canvas.drawRect(theBall.position.x - (theBall.mySize.x / 2),  // Left
                            theBall.position.y - (theBall.mySize.y / 2),          // Top
                            theBall.position.x + (theBall.mySize.x / 2),          // Right
                            theBall.position.y + (theBall.mySize.y / 2),          // Bottom
                            paint);                                  // Bitmap
                }

                ourHolder.unlockCanvasAndPost(canvas);
            }
        } // End drawCourt()

        public void controlFPS() {
            int upperFps, lowerFps, tempFps;

            timeNow = System.currentTimeMillis();
            timeThisFrame = (timeNow - lastFrameTime) + 1; //+1 as zero time (ms) not allowed
            if(timeThisFrame > 400) {
                timeThisFrame = 400;  // Clip max time to 500 ms.
            }
            fps = (int) (1000/timeThisFrame); //divide by zero not allowed so no need to check
            //Log.i("info","timeThisFrame: "+timeThisFrame+" fps: "+fps);
            long timeToSleep = 15 - timeThisFrame; //15ms = 66fps
            if( MainActivity.checkBoxObjectLimit.isChecked()) {
                timeToSleep = 80 - timeThisFrame; // slow to 12.5 fps
            }

            if( timeToSleep > 0 ) {
                try {
                    ourThread.sleep(timeToSleep);
                }
                catch (InterruptedException e) {
                    // Nothing here
                }
            }
            lastFrameTime = timeNow;
            fpsLastFrame = fps;
        } // End controlFPS

        public void pause() {
            playingSquash = false;
            try {
                ourThread.join();
            }
            catch (InterruptedException e) {
                // Nothing here
            }
        } // End pause()

        public void resume() {
            playingSquash = true;
            ourThread = new Thread(this);
            ourThread.start();
        } // End resume()

        //Fixed racket1 control relative to the racket NOT the center of the screen
        @Override
        public boolean onTouchEvent( MotionEvent motionEvent) {
            switch( motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN: // Start touching the screen

//                    if( motionEvent.getY() >= screenHeight/2) { // Check for action of racket1
//                        if (motionEvent.getX() >= racket1Position.x + (racket1Width / 2)) {
//                            racket1IsMovingRight = true;
//                            racket1IsMovingLeft = false;
//                        } else {
//                            racket1IsMovingLeft = true;
//                            racket1IsMovingRight = false;
//                        }
//                    }
//                    else { // Check for action of racket2
//                        if (motionEvent.getX() >= racket2Position.x + (racket2Width / 2)) {
//                            racket2IsMovingRight = true;
//                            racket2IsMovingLeft = false;
//                        } else {
//                            racket2IsMovingLeft = true;
//                            racket2IsMovingRight = false;
//                        }
//                    }
                    break;

                case MotionEvent.ACTION_UP: // Stopped touching the screen
                    break;
            } // End switch
            return true;
        } // End onTouchEvent

    } // End class SquashCourtView

    @Override
    protected void onStop() {
        super.onStop();
        while(true) {
            squashCourtView.pause();
            break;
        }
        finish();
    } // end onStop()

    @Override
    protected void onPause() {
        super.onPause();
        squashCourtView.pause();
    } // End onPause

    @Override
    protected void onResume() {
        super.onResume();
        squashCourtView.resume();
    } // End onResume

    public boolean onKeyDown( int keyCode, KeyEvent event) {
        if( keyCode == KeyEvent.KEYCODE_BACK) {
            squashCourtView.pause();
            finish();
            return true;
        }
        return false;
    } // End onKeyDown

} // End MainActivity
