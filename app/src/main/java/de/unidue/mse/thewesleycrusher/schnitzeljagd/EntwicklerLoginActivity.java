package de.unidue.mse.thewesleycrusher.schnitzeljagd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EntwicklerLoginActivity extends AppCompatActivity implements View.OnClickListener {


    EditText pwEingabe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entwickler_login);

        Button login = (Button) findViewById(R.id.button_login);
        login.setOnClickListener(this);

        pwEingabe = (EditText) findViewById(R.id.editText_Password);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                go();
                break;
        }
    }


    public void go() {
        String pw = pwEingabe.getText().toString();
        if (pw == "ab")
            startActivity(new Intent(EntwicklerLoginActivity.this, NewGameActivity.class));
        else
            startActivity(new Intent(EntwicklerLoginActivity.this, NewGameActivity.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}
