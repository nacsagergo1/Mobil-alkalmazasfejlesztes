package com.example.focibajnoksag;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity {

    private FirebaseUser user;
    private static final String LOG_TAG = HomeActivity.class.getName();

    int[] teamLogos = {
            R.drawable.alaves, R.drawable.atletico, R.drawable.barcelona, R.drawable.betis,
            R.drawable.bilbao, R.drawable.celta, R.drawable.espanyol, R.drawable.getafe,
            R.drawable.girona, R.drawable.laspalmas, R.drawable.leganes, R.drawable.mallorca,
            R.drawable.osasuna, R.drawable.rayo, R.drawable.realmadrid, R.drawable.sevilla,
            R.drawable.sociedad, R.drawable.valencia, R.drawable.valladolid, R.drawable.villarreal
    };

    String[] teamNames = {
            "Alavés", "Atlético", "Barcelona", "Betis",
            "Bilbao", "Celta", "Espanyol", "Getafe",
            "Girona", "Las Palmas", "Leganés", "Mallorca",
            "Osasuna", "Rayo", "Real Madrid", "Sevilla",
            "Sociedad", "Valencia", "Valladolid", "Villarreal"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Hitelesített felhasználó");
        } else {
            Log.d(LOG_TAG, "Nem hitelesített felhasználó");
            finish();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GridView gridView = findViewById(R.id.gridViewTeams);
        gridView.setAdapter(new com.example.focibajnoksag.TeamAdapter(this, teamLogos, teamNames));
    }
}