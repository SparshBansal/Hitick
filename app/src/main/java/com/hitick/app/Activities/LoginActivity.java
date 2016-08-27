package com.hitick.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hitick.app.Fragments.SignInFragment;
import com.hitick.app.Fragments.SignUpFragment;
import com.hitick.app.JsonParser;
import com.hitick.app.R;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity implements JsonParser.OnLoginListener {

    private static final String TITLE_SIGN_IN = "SIGN IN";
    private static final String TITLE_SIGN_UP = "SIGN UP";

    private ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.vp_login_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_tabs);

        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
    }

    // Initialises and sets up the view pager
    private void setupViewPager() {
        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add our fragments one by one
        Fragment signInFrag = new SignInFragment();
        Fragment signUpFrag = new SignUpFragment();

        mAdapter.addFragment(signInFrag, TITLE_SIGN_IN);
        mAdapter.addFragment(signUpFrag, TITLE_SIGN_UP);

        viewPager.setAdapter(mAdapter);
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

    // Adapter class for the tabs
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragmentList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String fragmentTitle) {
            fragmentList.add(fragment);
            fragmentTitleList.add(fragmentTitle);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public void onLogin(boolean status, String message) {
        if (status) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
