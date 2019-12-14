package fi.android.projekti.pong;

import android.graphics.RectF;

public class Tietokonepelaaja {



 /*
    AI:N LUONTI VAIHEESSA, LUOKASTA TULEE ERINLAINEN KUIN SE TÄLLÄ HETKELLÄ ON
    private RectF mRect;

    // palikan/pelaajan kokoon vaikuttavat muuttujat
    private float mLeveys;
    private float mKorkeus;

    // X Y koordinaatit palikalle
    private float mXCoord;
    private float mYCoord;
    //  tulee määrittämään pixels per sekunti nopeuden pelaajalle/palikalle
    private float mPelaajaNopeus;


    // Pelaajan/palikan liikkumissuunnat
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    private int mPelaajanLiike = STOPPED;


    private int mNayttoX;
    private int mNayttoY;

    int mP;
    pallo pal;
    // construktori

    public Tietokonepelaaja(int x, int y, int mP, pallo plo){

        mNayttoX = x;
        mNayttoY = y;
        pal = plo;

        // 1/8 ruudun leveys
        mLeveys = mNayttoX / 8;

        // 1/25 ruudun mKorkeus
        mKorkeus = mNayttoY / 25;

        if (mP == 1)
        // pelaajan aloituskohta
        {
            mXCoord = mNayttoX / 2;
            mYCoord = mNayttoY - 20;
        }
        else {
            mXCoord = mNayttoX / 2;
            mYCoord = mNayttoY + 20;
        }
        mRect = new RectF(mXCoord, mYCoord, mXCoord + mLeveys, mYCoord + mKorkeus);

        // kuinka nopeaa pelaaja kulkee ruudun poikki (1 sekunti (pixels per second))
        mPelaajaNopeus = mNayttoX;
    }



    // luodaan get metodi jotta saadaan kutsuttua pelaajaa PongView luokassa
    public RectF getRect(){
        return mRect;
    }


    // metodi jolla asetetaan pelaajan suunta pelissä

    public void setMovementState(int state){
        mPelaajanLiike = state;
    }

    // tätä update metodia kutsutaan PongView luokassa
    // määrittää jos pelaajan liikkeeseen pitää tehdä muutos ja kordinaatteihin
    public void update(long fps){

        if(mPelaajanLiike == LEFT){
            mXCoord = mXCoord - mPelaajaNopeus / fps;
        }

        if(mPelaajanLiike == RIGHT){
            mXCoord = mXCoord + mPelaajaNopeus / fps;
        }

        // varmistetaan ettei mennä ruudusta yli
        if(mRect.left < 0)
        {
            mXCoord = 0;
        }
        if(mRect.right > mNayttoX)
        {
            // pelaajan/palikan leveys
            mXCoord = mNayttoX - (mRect.right - mRect.left);

        }

        // päivitetään Rect grafiikka (pelaaja/palikka)
        mRect.left = mXCoord;
        mRect.right = mXCoord + mLeveys;
    }
*/
}