package fi.android.projekti.pong;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity {

    PongView pongView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // haetaan näyttö objekti jotta päästään näytön tietoihin käsiksi
        Display display = getWindowManager().getDefaultDisplay();

        // tarkistetaan näytön koko pisteinä
        Point size = new Point();
        display.getSize(size);

        // asetetaan pongview näkymään näytölle
        pongView = new PongView(this, size.x, size.y);
        setContentView(pongView);

    }

    // tämä tapahtuu kun pelaaja aloittaa pelin
    @Override
    protected void onResume() {
        super.onResume();


        pongView.resume();
    }

    // laitetaan peli pauselle kun käyttäjä sulkee pelin
    @Override
    protected void onPause() {
        super.onPause();


        pongView.pause();
    }

}
