package com.MemDerPack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.MemDerPack.Logic.JSONConverter;
import com.MemDerPack.Logic.SharedPref;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import com.MemDerPack.Logic.AlphanumericComparator;
import com.MemDerPack.Logic.PictureLogic;
import com.MemDerPack.Logic.UserLogic;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.MemDerPack.Logic.JSONConverter.convertFromJSON;

public class LoadActivity extends AppCompatActivity {

    // Name of the folder with memes on firebase.
    public static final String memeFolder = "MEMES";

    // Local buffer which contains numbers of memes in each category in next 10 memes.
    public static HashMap<String, Integer> numberOfMemesInBuffer;

    //Time to launch the another activity
    private static int TIME_OUT = 4000;

    // The arraylist of pictures.
    public static ArrayList<PictureLogic.Picture> pictureList;

    // Categories array.
    public static ArrayList<String> categories;

    // Progress bar.
    ProgressBar loadingPanel;

    // Announce a firebase user.
    FirebaseUser firebaseUser;

    // For nightmode.
    SharedPref sharedPref;

    @Override
    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    protected void onCreate(Bundle savedInstanceState) {

        // Setting theme.
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }

        // Creating activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        // Attaching crashlytics.
        Fabric.with(this, new Crashlytics());

        // Attaching circular load image.
        loadingPanel = findViewById(R.id.loadingPanel);

        // Setting the meme counter to 0.
        SharedPreferences.Editor editor = getSharedPreferences("Picturelist", MODE_PRIVATE).edit();
        editor.putString("current_elem", "0");
        editor.apply();

        // Check for internet connection.
        if (haveNetworkConnection()) {

            loadingPanel.setVisibility(View.VISIBLE);

            categories = new ArrayList<>();
            numberOfMemesInBuffer = new HashMap<String, Integer>();
            pictureList = new ArrayList<>();

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            /*
             * If user opens app for the first time we want to
             * take 1 random picture from each category.
             * If user open app second or more times,
             * we want to load to picturelist array of pictures
             * containing url-s
             * from shared preferences.
             */


            // If user opens app for the first time.
            if (firebaseUser == null) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(memeFolder);

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            categories.add(snapshot.getKey().toString());
                            numberOfMemesInBuffer.put(snapshot.getKey().toString(), 1);
                        }


                        // Go into every category subfolder.
                        for (final String category : categories) {

                            // Creating reference for subfolder.
                            DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference(memeFolder).child(category);

                            memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // Getting the list of meme values (links).
                                    final Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                                    // Getting and sorting the keys.
                                    final ArrayList<String> keys = new ArrayList<>(td.keySet());


                                    // Sort.
                                    //keys.sort(new AlphanumericComparator(Locale.ENGLISH));
                                    Collections.sort(keys, new AlphanumericComparator(Locale.ENGLISH));


                                    String memeUrl = td.get(keys.get(0)).toString();

                                    // Making a Picture element (attaching category to a picture).
                                    PictureLogic.Picture picture = new PictureLogic.Picture(memeUrl, categories.indexOf(category));

                                    // Add it to pic list.
                                    pictureList.add(picture);

                                    System.out.println(pictureList.toString());


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });


                Intent i = new Intent(LoadActivity.this, StartActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.left_to_right_1, R.anim.left_to_right_2);

                // If user closed the app and came again.
            } else {


                SharedPreferences prefs = getSharedPreferences("Buffer", Activity.MODE_PRIVATE);
                String pictires = prefs.getString("pictures", "");

                pictureList = convertFromJSON(pictires);


                Intent i = new Intent(LoadActivity.this, ChatsActivity.class);
                startActivity(i);
                finish();
            }
            // If no internet connection.
        } else {
            Toast.makeText(LoadActivity.this, "No internet!", Toast.LENGTH_SHORT).show();
            loadingPanel.setVisibility(View.GONE);
        }

    }

    // Check for internet.
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}