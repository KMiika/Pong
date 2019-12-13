package fi.android.projekti.pong;

import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

class PongView extends SurfaceView implements Runnable {
    // pää koodi, Threadi
    Thread mGameThread = null;


    SurfaceHolder mOurHolder;


    // booleani jolla asetetaan onko peli käynnissä vai ei, volatile bool koska sitä käsitellään luokan ulkopuolellakin
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
    //pelaaja2 mPelaaja2;
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



  //  Aina kun kutsutaan new()  pongViewiin niin tämä custom construktori ajetaan


    public PongView(Context context, int x, int y) {

    /*
        The next line of code asks the
        SurfaceView class to set up our object.
    */
        super(context);

        // näytön korkeus ja leveys
        mNayttoX = x;
        mNayttoY = y;

        // alustetaan mOurHolder ja mPaint objectit
        mOurHolder = getHolder();
        mPaint = new Paint();

        // luodaan pelaaja
        mPelaaja = new pelaaja(mNayttoX, mNayttoY);
       // mPelaaja2 = new pelaaja2(mNayttoX, mNayttoY);

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
            // luodaan objectit kahdesta vaaditusta classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // ladataan ääniefektit muistiin käyttöä varten
            descriptor = assetManager.openFd("beep1.ogg");
            beep1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("loseLife.ogg");
            loseLifeID = sp.load(descriptor, 0);


        } catch (IOException e) {
            // tulostetaan error viesti consoleen
            Log.e("error", "failed to load sound files");
        }

        setupAndRestart();

    }

    public void setupAndRestart() {

        // pallo aloituskohtaan HUOM! vaihda pallon aloitus koordinaatti keskelle ruutua
        mPallo.reset(mNayttoX, mNayttoY);

        // peli ohi -> pisteet ja elämät myös resetoidaan
        if (mLives == 0) {
            mScore = 0;
            mLives = 3;
        }

    }

    @Override
    public void run() {
        while (mPlaying) {

            // otetaan current time millisekunneiksi jotta saadaan tiheä päivitys frameille
            long startFrameTime = System.currentTimeMillis();

            // päivitetään frame

            if (!mPaused) {
                update();
            }

            // piiretään frame
            draw();

        /*
            Lasketaan FPS framelle jotta sitä voidaan käyttää update metodissa
        */
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame;
            }

        }

    }

    // tähän tulee kaikki mitä pitää päivittää framejen välillä
    public void update(){

        // liikutetaan pelaajaa jos tarve
        mPelaaja.update(mFPS);
        //mPelaaja2.update(mFPS);
        mPallo.update(mFPS);
        // tarkistus jos pallo osuu pelaajaan
        if(RectF.intersects(mPelaaja.getRect(), mPallo.getRect())) {
            mPallo.setRandomXNopeus();
            mPallo.vastakkainenYNopeus();
            mPallo.clearObstacleY(mPelaaja.getRect().top - 2);
            //mPallo.clearObstacleY(mPelaaja2.getRect().bottom - 2);

            mScore++; //JOS JA KUN muutetaan pisteytystä niin muista muuttaa tämä
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


        // tässä pallo bounchaa jos se osuu pohjalle eikä pelaajaan, muutos tähän jos ja kun saa pelin muuten toimimaan
        // tähän lisätään että jos osuu bot niin p2 score++ ja pallo resetti
        if(mPallo.getRect().bottom > mNayttoY){
            mPallo.vastakkainenYNopeus();
            mPallo.clearObstacleY(mNayttoY - 2);

            // tällähetkellä menettää elämän, mutta se muutetaan vastustajan pisteeksi
            mLives--;
            sp.play(loseLifeID, 1, 1, 0, 0, 1);

            if(mLives == 0){
                mPaused = true;
                setupAndRestart();
            }
        }
        // Tässä tällähetkellä pallo kimpoaa jos se osuu yläreunaan mutta tulee muuttumaan
        //tämänkin lähtee periaattessa pois koska
        if(mPallo.getRect().top < 0){
            mPallo.vastakkainenYNopeus();
            mPallo.clearObstacleY(12);

            sp.play(beep2ID, 1, 1, 0, 0, 1);
        }
        // pallon osuminen vasempaan
        if(mPallo.getRect().left < 0){
            mPallo.vastakkainenXNopeus();
            mPallo.clearObstacleX(2);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }
        // pallon osuminen oikeaan
        if(mPallo.getRect().right > mNayttoX){
            mPallo.vastakkainenXNopeus();
            mPallo.clearObstacleX(mNayttoX - 22);

            sp.play(beep3ID, 1, 1, 0, 0, 1);
        }


    }


    // piirretään uusi "kohtaus" mitä update metodista tulee
    public void draw() {

        // "drawing surface" pitää tarkistaa että onko se validi tai peli cräshää
        if (mOurHolder.getSurface().isValid()) {

            // piiretään kaikki

            // lukitaan "mCanvas" piirtoa varten
            mCanvas = mOurHolder.lockCanvas();

            // taustaväri vihreäksi (toki voi muuttaa vaan kuvaksi drawableen jos haluaa)
            mCanvas.drawColor(Color.argb(255, 1, 1, 1));

            // vaihdetaan väri valkoiseksi
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // piirretään pelaaja
            mCanvas.drawRect(mPelaaja.getRect(), mPaint);
            //mCanvas.drawRect(mPelaaja2.getRect(), mPaint);

            // piirretään pallo
            mCanvas.drawRect(mPallo.getRect(), mPaint);


            // Change the drawing color to white
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // piirretään pistetys
            mPaint.setTextSize(40);
            mCanvas.drawText("Score: " + mScore + "   Lives: " + mLives, 10, 50, mPaint);

            // ja tämä toteuttaa lopullisen piirron/päivityksen ruudulle eli kaikki ylhäällä listatut piirrot
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }


    }
    // jos peli pausetetaan tai sammutetaan niin pitää sulkea gamethread
    public void pause() {
        mPlaying = false;


        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

        Intent intent = new Intent(getContext(),MenuActivity.class);
        getContext().startActivity(intent);
    }

    // jos peli alkaa tai restarttaa niin aloitetaan threadi
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    // The SurfaceView class implements onTouchListener --> monitoroidaan näytön kosketusta
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


