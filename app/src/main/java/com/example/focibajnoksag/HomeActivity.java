package com.example.focibajnoksag;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.EditText;
import android.text.InputType;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.pm.PackageManager;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

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

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // már itt vagyunk
            } else if (id == R.id.nav_tabella) {
                Intent intent = new Intent(this, TabellaActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        setDailyReminder();

        //uploadTeamsToFirestore();
    }

    private void setDailyReminder() {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 3);
        calendar.set(Calendar.SECOND, 0);

        // Ha az idő már elmúlt ma, holnapra állítjuk
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private void uploadTeamsToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String[] teamNames = {
                "Alavés", "Atlético", "Barcelona", "Betis",
                "Bilbao", "Celta", "Espanyol", "Getafe",
                "Girona", "Las Palmas", "Leganés", "Mallorca",
                "Osasuna", "Rayo", "Real Madrid", "Sevilla",
                "Sociedad", "Valencia", "Valladolid", "Villarreal"
        };

        for (String name : teamNames) {
            Map<String, Object> team = new HashMap<>();
            team.put("name", name);
            team.put("points", 0);

            db.collection("teams").document(name)
                    .set(team)
                    .addOnSuccessListener(aVoid -> Log.d("UPLOAD", "Sikeres feltöltés: " + name))
                    .addOnFailureListener(e -> Log.e("UPLOAD", "Hiba történt: " + name, e));
        }
    }

    // A pont hozzáadása/levonása dialógus megjelenítése
    public void showPointDialog(final String teamName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pont módosítása");

        // Az input mező a pont hozzáadásához vagy levonásához
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Csak számokat enged
        builder.setView(input);

        // Műveletek gombok: Hozzáadás és Levonás
        builder.setPositiveButton("Hozzáadás", (dialog, which) -> {
            try {
                int pointsToAdd = Integer.parseInt(input.getText().toString());
                updateTeamPoints(teamName, pointsToAdd);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Érvénytelen szám!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Levonás", (dialog, which) -> {
            try {
                int pointsToSubtract = Integer.parseInt(input.getText().toString());
                updateTeamPoints(teamName, -pointsToSubtract);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Érvénytelen szám!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Mégse", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // A csapat pontszámának frissítése a Firestore-ban
    private void updateTeamPoints(String teamName, int pointsDelta) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference teamRef = db.collection("teams").document(teamName);

        teamRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int currentPoints = documentSnapshot.getLong("points").intValue();
                int updatedPoints = currentPoints + pointsDelta;

                // Frissítjük a pontot a Firestore-ban
                teamRef.update("points", updatedPoints)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("UPDATE", teamName + " új pontszám: " + updatedPoints);
                            Toast.makeText(this, teamName + " pontszáma frissítve!", Toast.LENGTH_SHORT).show();

                            showNotification(teamName, updatedPoints);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("UPDATE", "Hiba történt: " + e.getMessage());
                            Toast.makeText(this, "Hiba történt a pontszám frissítésekor.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.d("UPDATE", teamName + " nem található!");
            }
        });
    }

    private void showNotification(String teamName, int newPoints) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(LOG_TAG, "Értesítés engedély megtagadva, nem küldhető!");
                return; // Kilépünk, mert nincs engedély
            }
        }

        String channelId = "team_updates_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Csapatfrissítések",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Pont frissítve")
                .setContentText(teamName + " új pontszáma: " + newPoints)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
