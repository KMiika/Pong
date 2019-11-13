package fi.android.projekti.pong;

import android.graphics.RectF;

import java.util.Random;

public class pallo {

    private RectF pRect;
    private float pXNopeus;
    private float pYNopeus;
    private float pallonLeveys;
    private float pallonKorkeus;


    public pallo(int screenX, int screenY) {
        //pallon koko
        pallonLeveys = screenX / 100;
        pallonKorkeus = pallonLeveys;
        //pallon nopeus aloituksessa = neljäsosa ruudunkoosta
        pYNopeus = screenY / 4;
        pXNopeus = pYNopeus;

        pRect = new RectF();
    }
    public RectF getRect(){
        return pRect;
    }

    //pallon liike vaihtuu joka framella
    public void update(long fps){
        pRect.left = pRect.left + (pXNopeus / fps);
        pRect.top = pRect.top + (pYNopeus / fps);
        pRect.right = pRect.left + pallonLeveys;
        pRect.bottom = pRect.top - pallonKorkeus;
    }

    public void vastakkainenYNopeus(){
        pYNopeus = -pYNopeus;
    }


    public void vastakkainenXNopeus(){
        pXNopeus = -pXNopeus;
    }

    public void setRandomXNopeus(){


        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            vastakkainenXNopeus();
        }
    }

    // Kasvatetaan nopeutta 10%

    // voi vähentää tai kasvattaa 10 jos haluaa helpomman tai vaikeamman
    public void kasvataNopus(){
        pXNopeus = pXNopeus + pXNopeus / 10;
        pYNopeus = pYNopeus + pYNopeus / 10;
    }


    public void clearObstacleY(float y){
        pRect.bottom = y;
        pRect.top = y - pallonKorkeus;
    }

    public void clearObstacleX(float x){
        pRect.left = x;
        pRect.right = x + pallonLeveys;
    }

    public void reset(int x, int y){
        pRect.left = x / 2;
        pRect.top = y - 20;
        pRect.right = x / 2 + pallonLeveys;
        pRect.bottom = y - 20 - pallonKorkeus;
    }


}