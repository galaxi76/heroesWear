package app.heroeswear.com.heroesmakers.login.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.heroeswear.com.heroesfb.Logger;
import app.heroeswear.com.heroesmakers.R;
import common.BaseActivity;

/**
 * Created by livnatavikasis on 01/05/2018.
 */

public class HomePageActivity extends BaseActivity {

    private Button bn_game;
    private Button bn_album;
    private Button bn_cont;
    private Button bt_okay;
    private Button bn_rec;
    private Button btn_music;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initMenues();

        bt_okay = (Button) findViewById(R.id.okay_button);
        bt_okay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                moveTaskToBack(true);
            }
        });

        bn_rec = (Button) findViewById(R.id.btn_rec);
        bn_rec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                launchActivity(AudioPlayerActivity.class);
            }
        });

        bn_game = (Button) findViewById(R.id.btn_game);
        bn_game.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                launchActivity(TriviaActivity.class);
            }
        });

        bn_cont = (Button) findViewById(R.id.btn_cont);
        bn_cont.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                launchActivity(EmergencyContactsActivity.class);
            }
        });

        btn_music = (Button) findViewById(R.id.btn_music);
        btn_music.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
                Intent chooser = Intent.createChooser(intent, "Pick me");
                startActivity(chooser);
            }
        });

        bn_album = (Button) findViewById(R.id.btn_album);
        bn_album.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openDefaultGallery();
            }
        });
    }

    private void launchActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void openDefaultGallery() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
        } catch (Exception e) {
            Logger.Companion.e(e.getMessage());
            Toast.makeText(this, "Error: can't open gallery", Toast.LENGTH_LONG).show();
        }
    }

}
