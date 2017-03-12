package com.example.aya.bestmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.example.aya.bestmovies.R;

/**
 * Created by aya on 09/11/2016.
 */

public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);


        Thread timer=new Thread() {
            public void run() {

                try
                {
                    sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally {
                    Intent main=new Intent("mainActivity");
                    startActivity(main);
                }
            }

        };
        timer.start();

    }
}
