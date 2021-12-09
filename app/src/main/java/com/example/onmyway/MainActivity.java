package com.example.onmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);
        bnv.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(findViewById(R.id.fragmentContainerView));
        int currentFragmentId = navController.getCurrentDestination().getId();
        if(item.getItemId() == R.id.homeNavBar){
            if(currentFragmentId != R.id.homeFragment2){
                navController.navigate(R.id.homeFragment2);
            }
            return true;
        } else if (item.getItemId() == R.id.runningNavBar){
            if(currentFragmentId != R.id.runningFragment2){
                navController.navigate(R.id.runningFragment2);
            }
            return true;
        } else if (item.getItemId() == R.id.savedNavBar){
            if(currentFragmentId != R.id.savedFragment2){
                navController.navigate(R.id.savedFragment2);
            }
            return true;
        }
        return false;
    }
}