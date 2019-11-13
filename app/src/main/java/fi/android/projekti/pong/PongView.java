package fi.android.projekti.pong;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class PongView extends SurfaceView implements Runnable {
    // pää koodi, Threadi
    Thread mGameThread = null;

    // We need a SurfaceHolder object
    // We will see it in action in the draw method soon.
    SurfaceHolder mOurHolder;

    // A boolean which we will set and unset
    // when the game is running- or not
    // It is volatile because it is accessed from inside and outside the thread
    volatile boolean mPlaying;

    // peli alkaa pausella
    boolean mPaused = true;


    Canvas mCanvas;
    Paint mPaint;

    // seuraa FPS:ää
    long mFPS;

    // näytön koko
    int mNayttoX;
    int mNayttoY;

    // palikka
    pelaaja mPelaaja;
    //pelaaja2 mPelaaja2

    // pallo
    pallo mPallo;

    // äänet
    SoundPool sp;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;

    // pisteet
    int mScore = 0;

    // elämät
    int mLives = 3;


    /*
    When the we call new() on pongView
    This custom constructor runs
*/

    public PongView(Context context, int x, int y) {

    /*
        The next line of code asks the
        SurfaceView class to set up our object.
    */
        super(context);

        // näytön leveys ja korkeus
        mNayttoX = x;
        mNayttoY = y;


        mOurHolder = getHolder();
        mPaint = new Paint();

        // luodaan pelaaja
        mPelaaja = new pelaaja(mNayttoX, mNayttoY);
        //mPelaaja2 = new pelaaja(,NayttoX, mNayttoY);

        // luodaan pallo
        mPallo = new pallo(mNayttoX, mNayttoY);

    /*
        Instantiate our sound pool
        dependent upon which version
        of Android is present
    */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }


        try {
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("beep1.ogg");
            beep1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeID = sp.load(descriptor, 0);

            //descriptor = assetManager.openFd("explode.ogg");
            //explodeID = sp.load(descriptor, 0);

        } catch (IOException e) {
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }

        setupAndRestart();

    }

    public void setupAndRestart() {

        // Put the mPallo back to the start
        mPallo.reset(mNayttoX, mNayttoY);

        // if game over reset scores and mLives
        if (mLives == 0) {
            mScore = 0;
            mLives = 3;
        }

    }

    @Override
    public void run() {
        while (mPlaying) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            // Update the frame
            if (!mPaused) {
                update();
            }

            // Draw the frame
            draw();

        /*
            Calculate the FPS this frame
            We can then use the result to
            time animations in the update methods.
        */
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame;
            }

        }

    }

    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.
    public void update(){

        // Move the mPelaaja if required
        mPelaaja.update(mFPS);
        //mPelaaja2.update(mFPS);
        mPallo.update(mFPS);
        // Check for mPallo colliding with mPelaaja
        if(RectF.intersects(mPelaaja.getRect(), mPallo.getRect())) {
            mPallo.setRandomXNopeus();
            mPallo.vastakkainenYNopeus();
            mPallo.clearObstacleY(mPelaaja.getRect().top - 2);

            mScore++;
            mPallo.kasvataNopus();

            sp.play(beep1ID, 1, 1, 0, 0, 1);
        }
        /*
        multiplayeriin jotakuinkin näin pisteytys update
        if (scoreTop > 9 || scoreBot > 9) {
                if (scoreTop > scoreBot) {
                    lose = true;
                } else {
                    win = true;

                }
                scoreTop = 0;
                scoreBot = 0;
                displayScore = false;
            }
        */


        // Bounce the mPallo back when it hits the bottom of screen
        if(mPallo.getRect().bottom > mNayttoY){
            mPallo.vastakkainenYNopeus();
            mPallo.clearObstacleY(mNayttoY - 2);

            // Lose a life
            mLives--;
            sp.play(loseLifeID, 1, 1, 0, 0, 1);

            if(mLives == 0){
                mPaused = true;
                setupAndRestart();
            }
        }
        // Bounce the mPallo back when it hits the top of screen
        if(mPallo.getRect().top < 0){
            mPallo.vastakkainenYNopeus();
            mPallo.clearObstacleY(12);

            sp.play(beep2ID, 1, 1, 0, 0, 1);
        }
        // If the mPallo hits left wall bounce
        if(mPallo.getRect().left < 0){
            mPallo.vastakkainenXNopeus();
            mPallo.clearObstacleX(2);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }
        // If the mPallo hits right wall bounce
        if(mPallo.getRect().right > mNayttoX){
            mPallo.vastakkainenXNopeus();
            mPallo.clearObstacleX(mNayttoX - 22);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }


    }


    // Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (mOurHolder.getSurface().isValid()) {

            // Draw everything here

            // Lock the mCanvas ready to draw
            mCanvas = mOurHolder.lockCanvas();

            // Clear the screen with my favorite color
            mCanvas.drawColor(Color.argb(255, 120, 197, 87));

            // Choose the brush color for drawing
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mPelaaja
            mCanvas.drawRect(mPelaaja.getRect(), mPaint);
            //mCanvas.drawRect(mPelaaja2.getRect(), mPaint)

            // Draw the mPallo
            mCanvas.drawRect(mPallo.getRect(), mPaint);


            // Change the drawing color to white
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mScore
            mPaint.setTextSize(40);
            mCanvas.drawText("Score: " + mScore + "   Lives: " + mLives, 10, 50, mPaint);

            // Draw everything to the screen
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }


    }
    // If the Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If the Activity starts/restarts
    // start our thread.
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // pelaaja koskee ruutuun
            case MotionEvent.ACTION_DOWN:

                mPaused = false;

                // tarkistus kummalle puolen pelaaja koskee näyttöä (vasen oikea)
                if(motionEvent.getX() > mNayttoX / 2){
                    mPelaaja.setMovementState(mPelaaja.RIGHT);
                }
                else{
                    mPelaaja.setMovementState(mPelaaja.LEFT);
                }

                break;

            // kun pelaaja ei kosketa ruutuun
            case MotionEvent.ACTION_UP:

                mPelaaja.setMovementState(mPelaaja.STOPPED);
                break;
        }
        return true;
    }
}


