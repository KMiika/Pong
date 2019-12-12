package fi.android.projekti.pong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    PongView pongView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            // haetaan display objekti jotta sadaan ruudun tiedot käsittelyyn
            Display display = getWindowManager().getDefaultDisplay();

            // ladataan ruudun koko pisteinä
            Point size = new Point();
            display.getSize(size);

            // asetetaan pongview pää näkymäksi
            pongView = new PongView(this, size.x, size.y);
            setContentView(pongView);
        }



    // tämä tapahtuu kun käynnistetään peli
    @Override
    protected void onResume() {
        super.onResume();


        pongView.resume();
    }

    // tämä tapahtuu kun pelaaja lopettaa pelin
    @Override
    protected void onPause() {
        super.onPause();


        pongView.pause();
    }

}

