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

        // haetaan display objekti jotta saadaan ruudun tiedot käsittelyyn
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
