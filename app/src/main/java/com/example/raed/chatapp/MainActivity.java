package com.example.raed.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.auth.AuthUI;

import static com.example.raed.chatapp.MainActivityFragment.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FragmentManager manager;
    MainActivityFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        manager = getSupportFragmentManager();
        fragment = (MainActivityFragment) manager.findFragmentById(R.id.fragment);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        fragment.onSignedOutInitialized();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
//        if(item.getItemId() == R.id.menu_singout) {
//            Log.d(TAG, "onOptionsItemSelected: MainActivity sign out ");
//            AuthUI.getInstance().signOut(this);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if(resultCode == RESULT_OK){
                    Log.d(TAG, "onActivityResult: Signed in successfully");
                }else {
                    Log.d(TAG, "onActivityResult: Signed out ");
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
