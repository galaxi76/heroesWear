package app.heroeswear.com.heroesmakers.login.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import app.heroeswear.com.heroesmakers.R;
import common.BaseActivity;

public class SolutionListActivity extends BaseActivity {

    private Button bn_game;
    private Button bn_album;
    private Button bn_cont;
    private Button bt_okay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solution_lists_variation);

        bt_okay = (Button) findViewById(R.id.okay_button);
        bn_game = (Button) findViewById(R.id.okay_button);
        bn_game.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                moveTaskToBack(true);
            }
        });

        bn_game = (Button) findViewById(R.id.btn_game);
        bn_game.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                launchActivity(TriviaActivity.class);
            }
        });

        bn_cont = (Button) findViewById(R.id.btn_cont);
        bn_cont.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                launchActivity(EmergencyContactsActivity.class);
            }
        });


        bn_album = (Button) findViewById(R.id.btn_album);
        bn_album.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (Build.VERSION.SDK_INT < 19) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                    }
                }
            }
        });


    }

    private void launchActivity(Class cls){
        Intent intent = new Intent(this,cls);
        startActivity(intent);
    }
}

