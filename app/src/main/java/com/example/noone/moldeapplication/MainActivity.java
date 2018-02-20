package com.example.noone.moldeapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.noone.moldeapplication.category.CategoryFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Load Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, new CategoryFragment())
                .commit();

    }
}
