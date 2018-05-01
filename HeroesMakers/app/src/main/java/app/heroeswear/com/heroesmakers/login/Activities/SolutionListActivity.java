package app.heroeswear.com.heroesmakers.login.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import app.heroeswear.com.heroesmakers.R;
import common.BaseActivity;

public class SolutionListActivity extends BaseActivity {

    private Button btn_game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.solution_lists_variation);

        btn_game = (Button) findViewById(R.id.btn_game);
        btn_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGameActivity();
            }
        });
        }

    private void launchGameActivity(){
        Intent intent = new Intent(this, TriviaActivity.class);
        startActivity(intent);
    }
    }