package com.hitick.app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.hitick.app.Fragments.SignInDetailsFragment;
import com.hitick.app.Fragments.SignUpDetailsFragment;
import com.hitick.app.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static Toolbar toolbar;
    private static Button bSignUp;
    private static Button bSignIn;
    private static FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //Setup the instance variables
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        bSignIn = (Button) findViewById(R.id.bSignIn);
        bSignUp = (Button) findViewById(R.id.bSignUp);
        fragmentContainer = (FrameLayout) findViewById(R.id.container);

        bSignIn.setOnClickListener(this);
        bSignUp.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        // Add the Sign Up Fragment Initially
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();

        if (savedInstanceState == null) {
            transaction.add(R.id.container, new SignUpDetailsFragment());
        } else {
            transaction.replace(R.id.container, new SignUpDetailsFragment());
        }

        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_actvity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.bSignIn:
                //Replace the current fragment with Sign In Details Fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new SignInDetailsFragment())
                        .commit();
                break;
            case R.id.bSignUp:
                //Replace the current fragment with Sign Up Details Fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container , new SignUpDetailsFragment())
                        .commit();
                break;
        }
    }
}
