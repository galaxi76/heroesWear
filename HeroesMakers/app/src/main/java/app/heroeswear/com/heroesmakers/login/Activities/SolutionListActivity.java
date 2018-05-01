package app.heroeswear.com.heroesmakers.login.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import app.heroeswear.com.heroesmakers.R;

public class SolutionListActivity extends AppCompatActivity {

    private Button bn_game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solution_lists_variation);

        bn_game = (Button) findViewById(R.id.btn_game);
        bn_game.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                launchGameActivity();
            }
        });
    }

    private void launchGameActivity(){
        Intent intent = new Intent(this,SolutionListActivity.class);
        startActivity(intent);
    }
}
