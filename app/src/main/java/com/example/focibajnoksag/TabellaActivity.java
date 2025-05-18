package com.example.focibajnoksag;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class TabellaActivity extends AppCompatActivity {

    private static final String TAG = "TabellaActivity";
    private RecyclerView recyclerView;
    private TabellaAdapter tabellaAdapter;
    private List<Team> teamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabella);

        Button btnTopTeams = findViewById(R.id.btnTopTeams);
        Button btnMin10Points = findViewById(R.id.btnMin10Points);
        Button btnStartsWithR = findViewById(R.id.btnStartsWithR);

        btnTopTeams.setOnClickListener(v -> loadTopTeams());
        btnMin10Points.setOnClickListener(v -> loadTeamsWithMinPoints(10));
        btnStartsWithR.setOnClickListener(v -> loadTeamsStartingWith("R"));


        recyclerView = findViewById(R.id.recyclerViewTabella);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        teamList = new ArrayList<>();
        tabellaAdapter = new TabellaAdapter(this, teamList);
        recyclerView.setAdapter(tabellaAdapter);

        loadTeamsFromFirestore(); // Első betöltés

        // BottomNavigationView kezelése
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_tabella);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(TabellaActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Animáció nélkül
                return true;
            } else if (id == R.id.nav_tabella) {
                return true; // Már itt vagyunk
            }
            return false;
        });
    }

    // Lifecycle Hook
    @Override
    protected void onResume() {
        super.onResume();
        loadTeamsFromFirestore();
    }

    private void loadTeamsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teams")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        teamList.clear();
                        for (var document : queryDocumentSnapshots) {
                            String name = document.getString("name");
                            long points = document.getLong("points");
                            teamList.add(new Team(name, (int) points));
                        }

                        // Csökkenő sorrend pontszám alapján
                        Collections.sort(teamList, new Comparator<Team>() {
                            @Override
                            public int compare(Team t1, Team t2) {
                                return Integer.compare(t2.getPoints(), t1.getPoints());
                            }
                        });

                        // Helyezés beállítása
                        for (int i = 0; i < teamList.size(); i++) {
                            teamList.get(i).setRank(i + 1);
                        }

                        tabellaAdapter.notifyDataSetChanged();

                        // Animáció alkalmazása
                        Animation animation = AnimationUtils.loadAnimation(TabellaActivity.this, R.anim.tabellaanimation);
                        recyclerView.startAnimation(animation);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting teams: ", e));
    }

    private void loadTopTeams() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teams")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    teamList.clear();
                    for (var document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        long points = document.getLong("points");
                        teamList.add(new Team(name, (int) points));
                    }
                    tabellaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Top 5 lekérdezés hiba: ", e));
    }

    private void loadTeamsWithMinPoints(int minPoints) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teams")
                .whereGreaterThanOrEqualTo("points", minPoints)
                .orderBy("points", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    teamList.clear();
                    for (var document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        long points = document.getLong("points");
                        teamList.add(new Team(name, (int) points));
                    }
                    tabellaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Min. pont lekérdezés hiba: ", e));
    }

    private void loadTeamsStartingWith(String prefix) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("teams")
                .orderBy("name")
                .startAt(prefix)
                .endAt(prefix + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    teamList.clear();
                    for (var document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        long points = document.getLong("points");
                        teamList.add(new Team(name, (int) points));
                    }
                    tabellaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "R-el kezdődő csapatok hiba: ", e));
    }

}
