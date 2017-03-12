package com.example.aya.bestmovies.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import com.example.aya.bestmovies.R;
import com.example.aya.bestmovies.fragments.ListItemsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{

    public static boolean mTwoPane;
    @BindView(R.id.toolbar)Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // The detail container view will be present only in the
        // large-screen layouts
        // If this view is present, then the
        // activity should be in two-pane mode.
        if (findViewById(R.id.detail_container) != null)
        {
            mTwoPane = true;

        }


        ListItemsFragment listitemsfragment=new ListItemsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.item_container, listitemsfragment).commit();


    }

}
