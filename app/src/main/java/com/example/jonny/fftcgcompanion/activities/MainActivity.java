package com.example.jonny.fftcgcompanion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.jonny.fftcgcompanion.R;
import com.example.jonny.fftcgcompanion.views.DecksFragment;
import com.example.jonny.fftcgcompanion.views.SettingsFragment;
import com.example.jonny.fftcgcompanion.views.ViewCardsFragment;

public class MainActivity extends AppCompatActivity
{
    private TextView m_toolbarTitleTextView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_toolbarTitleTextView = (TextView)findViewById(R.id.toolbar_title_textview);

        loadFragment(ViewCardsFragment.newInstance());

        BottomNavigationView bottomNavBar = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                Fragment fragment = null;
                switch (menuItem.getItemId())
                {
                    case R.id.navigation_home:
                    {
                        m_toolbarTitleTextView.setText(R.string.title_cards);
                        fragment = ViewCardsFragment.newInstance();
                    }
                    break;
                    case R.id.navigation_decks:
                    {
                        m_toolbarTitleTextView.setText(R.string.title_decks);
                        fragment = DecksFragment.newInstance();
                    }
                    break;
                    case R.id.navigation_settings:
                    {
                        m_toolbarTitleTextView.setText(R.string.title_settings);
                        fragment = SettingsFragment.newInstance();
                    }
                    break;
                }

                if (fragment != null)
                {
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }
        });

    }

    private void loadFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_activity_fragment_framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
