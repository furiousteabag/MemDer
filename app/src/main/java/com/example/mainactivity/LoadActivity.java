package com.example.mainactivity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.mainactivity.Logic.PictureLogic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LoadActivity extends AppCompatActivity {

    private static int TIME_OUT = 4000; //Time to launch the another activity

    // The buffer.
    public static ArrayList<PictureLogic.Picture> pictureList;

    // Categories array.
    public static final ArrayList<String> categories = new ArrayList<String>() {{
        add("abstract");
        add("anime");
        add("cats");
        add("cybersport");
        add("disgraceful");
        add("lentach");
        add("mhk");
        add("normal");
        add("physkek");
        add("programmer");
    }};


    // Announce a firebase user.
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        pictureList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        /*
         * If user opens app for the first time we want to
         * take 1 random picture from each category.
         * If user open app second or more times,
         * we want to put to picturelist his array
         * list of urls of his pictures.
         */


        // If user opens app for the first time.
        if (firebaseUser == null) {
            // Go into every category subfolder.
            for (final String category : categories) {

                // Creating reference for subfolder.
                DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference("Memes").child(category);

                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Getting the list of meme values (links).
                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                        List<Object> values = new ArrayList<>(td.values());

                        // Choose random meme (link).
                        Random randomizer = new Random();
                        Object randomMemeUrlObject = values.get(randomizer.nextInt(values.size()));
                        String randomMemeUrl = String.valueOf(randomMemeUrlObject);

                        // Making image left_to_right_2 of Url.
                        PictureLogic.Data image = new PictureLogic.Data(randomMemeUrl);

                        // Making a Picture element (attaching category to a picture).
                        PictureLogic.Picture picture = new PictureLogic.Picture(image, categories.indexOf(category));

                        // Add it to pic list.
                        pictureList.add(picture);

                        System.out.println(pictureList.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(LoadActivity.this, StartActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, TIME_OUT);

            }
        } else {
            // Go into every category subfolder.
            for (final String category : categories) {

                // Creating reference for subfolder.
                DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference("Memes").child(category);

                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Getting the list of meme values (links).
                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                        List<Object> values = new ArrayList<>(td.values());

                        // Choose random meme (link).
                        Random randomizer = new Random();
                        Object randomMemeUrlObject = values.get(randomizer.nextInt(values.size()));
                        String randomMemeUrl = String.valueOf(randomMemeUrlObject);

                        // Making image left_to_right_2 of Url.
                        PictureLogic.Data image = new PictureLogic.Data(randomMemeUrl);

                        // Making a Picture element (attaching category to a picture).
                        PictureLogic.Picture picture = new PictureLogic.Picture(image, categories.indexOf(category));

                        // Add it to pic list.
                        pictureList.add(picture);

                        System.out.println(pictureList.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(LoadActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, TIME_OUT);

            }
        }
    }
}
