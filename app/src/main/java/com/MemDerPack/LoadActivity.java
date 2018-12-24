package com.MemDerPack;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoadActivity extends AppCompatActivity {

    public static final String memeFolder = "MEMES";


    //ToDo: Время загрузки в зависимости от инета
    //ToDo: share
    //ToDo: 10 картинок из буфера сохранять в кэш и подгружать их при повоторном заходе
    //ToDo: очищать кэш
    //ToDo: Санек должен сделать чтобы у всех пользователей могла добавляться категория к categories_seen

    public static HashMap<String, Integer> numberOfMemesInBuffer;

    private static int TIME_OUT = 4000; //Time to launch the another activity

    // The buffer.
    public static ArrayList<PictureLogic.Picture> pictureList;

    // Categories array.
    public static ArrayList<String> categories;


    // Announce a firebase user.
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        /*
         *
         *  Filling the categories array
         *
         */
//
        categories = new ArrayList<>();
        numberOfMemesInBuffer = new HashMap<String, Integer>();


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
                            @TargetApi(Build.VERSION_CODES.N)
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                // Getting the list of meme values (links).
                                final Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                                // Getting and sorting the keys.
                                final ArrayList<String> keys = new ArrayList<>(td.keySet());


                                // Sort.
                                keys.sort(new AlphanumericComparator(Locale.ENGLISH));


                                String memeUrl = td.get(keys.get(0)).toString();

                                // Making image of Url.
                                PictureLogic.Data image = new PictureLogic.Data(memeUrl);

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


                    }
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

            // If user closed the app and came again.
        } else {

            // User reference.
            final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            // Memes reference.
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(memeFolder);

            // Filling the categories array and local buffer array.
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        // Filling them.
                        categories.add(snapshot.getKey().toString());
                        numberOfMemesInBuffer.put(snapshot.getKey().toString(), 0);

                    }


                    // Getting the instanse of user.
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // Initializing local user.
                            final UserLogic.User fireUser = dataSnapshot.getValue(UserLogic.User.class);


                            // Repeating choosing category and adding picture to buffer
                            for (int i = 0; i < categories.size(); i++)
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        // Defining the category of next meme.
                                        final String categoryNext = categories.get(UserLogic.UserMethods.getCategory(fireUser.getPreferencesList()));

                                        // Adding a point to local buffer.
                                        numberOfMemesInBuffer.put(categoryNext, numberOfMemesInBuffer.get(categoryNext) + 1);

                                        // Creating reference for subfolder (selecting subfolder by choosing the prefered category).
                                        DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference(memeFolder).child(categoryNext);

                                        memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @TargetApi(Build.VERSION_CODES.N)
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                // Getting the list of meme values (links).
                                                final Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                                                // Getting and sorting the keys.
                                                final ArrayList<String> keys = new ArrayList<>(td.keySet());

                                                // Sort.
                                                keys.sort(new AlphanumericComparator(Locale.ENGLISH));

                                                // Taking the number of the meme to pick.
                                                DatabaseReference memeCategory = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Categories_seen").child(categoryNext);

                                                memeCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        String memeUrl;
                                                        try {
                                                            // Current meme number.
                                                            Integer memeNumber;
                                                            memeNumber = Integer.parseInt(dataSnapshot.getValue().toString()) + numberOfMemesInBuffer.get(categoryNext);
                                                            numberOfMemesInBuffer.put(categoryNext, numberOfMemesInBuffer.get(categoryNext) + 1);
                                                            memeUrl = td.get(keys.get(memeNumber)).toString();
                                                        } catch (Exception e) {
                                                            memeUrl = td.get(keys.get(keys.size() - 1)).toString();
                                                        }

                                                        // Making image of Url.
                                                        PictureLogic.Data image = new PictureLogic.Data(memeUrl);

                                                        // Making a Picture element (attaching category to a picture).
                                                        PictureLogic.Picture picture = new PictureLogic.Picture(image, LoadActivity.categories.indexOf(categoryNext));

                                                        // Add it to pic list.
                                                        pictureList.add(picture);


                                                        System.out.println(pictureList.toString());

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                }, 10);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    /*
                     * Adding new picture.
                     */

//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            Intent i = new Intent(LoadActivity.this, MainActivity.class);
//                            startActivity(i);
//                            finish();
//
//                        }
//                    }, TIME_OUT);


//                    // Go into every category subfolder.
//                    for (final String category : categories) {
//
//                        // Creating reference for subfolder.
//                        DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference("Memes").child(category);
//
//                        memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                // Getting the list of meme values (links).
//                                final Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
//
//                                // Getting and sorting the keys.
//                                final ArrayList<String> keys = new ArrayList<>(td.keySet());
//                                Collections.sort(keys);
//
//                                List<Object> values = new ArrayList<>(td.values());
//
//                                // Taking the number of the meme to pick.
//                                DatabaseReference memeCategory = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Categories_seen").child(category);
//                                memeCategory.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                        // Current meme number.
//                                        Integer memeNumber = Integer.parseInt(dataSnapshot.getValue().toString());
//                                        if (memeNumber >= td.size()) {
//                                            System.out.println("Закончились мемесы.");
//                                            memeNumber = td.size() - 1;
//
//                                        }
//
//                                        String memeUrl = td.get(keys.get(memeNumber)).toString();
//
//                                        // Making image of Url.
//                                        PictureLogic.Data image = new PictureLogic.Data(memeUrl);
//
//                                        // Making a Picture element (attaching category to a picture).
//                                        PictureLogic.Picture picture = new PictureLogic.Picture(image, categories.indexOf(category));
//
//                                        // Add it to pic list.
//                                        pictureList.add(picture);
//
//                                        System.out.println(pictureList.toString());
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent i = new Intent(LoadActivity.this, MainActivity.class);
//                                startActivity(i);
//                                finish();
//                            }
//                        }, TIME_OUT);
//
//                    }


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