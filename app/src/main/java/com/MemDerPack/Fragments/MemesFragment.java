package com.MemDerPack.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.MemDerPack.ChatsActivity;
import com.MemDerPack.LoadActivity;
import com.MemDerPack.Logic.PictureLogic;
import com.MemDerPack.Logic.UserLogic;
import com.MemDerPack.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.MemDerPack.LoadActivity.memeFolder;
import static com.MemDerPack.LoadActivity.numberOfMemesInBuffer;

public class MemesFragment extends Fragment {

    // Arraylist of memes.
    private ArrayList<PictureLogic.Picture> pictureList;

    // Initializing firebase elements.
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    DatabaseReference referenceChangeRight;
    DatabaseReference referenceChangeLeft;

    // Swipes
    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private SwipeFlingAdapterView flingContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memes, container, false);


        flingContainer = view.findViewById(R.id.frame);


        // Associating firebase variables
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        referenceChangeRight = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        referenceChangeLeft = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        /*
         * SWIPES HANDLING
         */

        // Take the picturelist from startactivity.
        pictureList = LoadActivity.pictureList;

        // Initializing adapter.
        myAppAdapter = new MyAppAdapter(pictureList, getContext());
        flingContainer.setAdapter(myAppAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                // Database synchronization.
                referenceChangeLeft.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        /*
                         * Changing preferences.
                         */

                        // Creating new reference.
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        // Initializing local user.
                        final UserLogic.User fireUser = dataSnapshot.getValue(UserLogic.User.class);

                        // Changing preferences.
                        PictureLogic.PictureMethods.ChangePreference(fireUser, pictureList.get(0), false);

                        // Sending them to sever.
                        databaseReference.child("Users").child(firebaseUser.getUid()).child("preferences").setValue(fireUser.getPreferencesList().toString());

                        /*
                         * Adding a point to number of meme.
                         */

                        // String of current category.
                        final String category = LoadActivity.categories.get(pictureList.get(0).Category);

                        // Number of meme in category.

                        // Adding point to a category of current meme.
                        final DatabaseReference currentMemeCategory = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                                .child("Categories_seen").child(category);

                        currentMemeCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                // Current meme number.
                                final Integer memeNumber = Integer.parseInt(dataSnapshot.getValue().toString());

                                // Creating reference for subfolder (selecting subfolder by choosing the prefered category).
                                final DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference(memeFolder).child(category);

                                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        // Getting the hasmap of memes.
                                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                                        if (memeNumber >= td.size()) {
                                            System.out.println("Закончились мемесы.");
                                            //numberOfMemesInBuffer.put(category, 0);

                                        } else {
                                            currentMemeCategory.setValue(memeNumber + 1);
                                            //Decreaze number of meme of this category in buffer.
                                            numberOfMemesInBuffer.put(category, numberOfMemesInBuffer.get(category) - 1);
                                        }
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


                        /*
                         * Adding new picture.
                         */

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                // Defining the category of next meme.
                                final String categoryNext = LoadActivity.categories.get(UserLogic.UserMethods.getCategory(fireUser.getPreferencesList()));

                                // Creating reference for subfolder (selecting subfolder by choosing the prefered category).
                                DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference(memeFolder).child(categoryNext);

                                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        // Getting the list of meme values (links).
                                        final Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                                        // Getting and sorting the keys.
                                        final ArrayList<String> keys = new ArrayList<>(td.keySet());
                                        Collections.sort(keys);

                                        // Taking the number of the meme to pick.
                                        DatabaseReference memeCategory = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Categories_seen").child(categoryNext);

                                        memeCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                String memeUrl;
                                                // Current meme number.
                                                Integer memeNumber;
                                                memeNumber = Integer.parseInt(dataSnapshot.getValue().toString()) + numberOfMemesInBuffer.get(categoryNext);
                                                if (memeNumber < td.size()) {
                                                    numberOfMemesInBuffer.put(categoryNext, numberOfMemesInBuffer.get(categoryNext) + 1);
                                                    memeUrl = td.get(keys.get(memeNumber)).toString();
                                                } else {
                                                    memeUrl = td.get(keys.get(keys.size() - 1)).toString();
                                                }


                                                // Making image of Url.
                                                PictureLogic.Data image = new PictureLogic.Data(memeUrl);

                                                // Making a Picture element (attaching category to a picture).
                                                PictureLogic.Picture picture = new PictureLogic.Picture(image, LoadActivity.categories.indexOf(categoryNext));

                                                // Add it to pic list.
                                                pictureList.add(picture);
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

                                /*
                                 * Removing the picture we just swiped.
                                 */

                                // Remove the object we just swiped.
                                pictureList.remove(0);
                                myAppAdapter.notifyDataSetChanged();


                                System.out.println(fireUser.getPreferencesList().toString());


                            }
                        }, 10);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // Vibration.
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(30);
                }

                deleteCache(getActivity().getApplicationContext());


            }

            @Override
            public void onRightCardExit(Object dataObject) {

                // Database synchronization.
                referenceChangeRight.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        /*
                         * Changing preferences.
                         */

                        // Creating new reference.
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        // Initializing local user.
                        final UserLogic.User fireUser = dataSnapshot.getValue(UserLogic.User.class);

                        // Changing preferences.
                        PictureLogic.PictureMethods.ChangePreference(fireUser, pictureList.get(0), true);

                        // Sending them to sever.
                        databaseReference.child("Users").child(firebaseUser.getUid()).child("preferences").setValue(fireUser.getPreferencesList().toString());

                        /*
                         * Adding a point to number of meme.
                         */

                        // String of current category.
                        final String category = LoadActivity.categories.get(pictureList.get(0).Category);

                        // Number of meme in category.

                        // Adding point to a category of current meme.
                        final DatabaseReference currentMemeCategory = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                                .child("Categories_seen").child(category);

                        currentMemeCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                // Current meme number.
                                final Integer memeNumber = Integer.parseInt(dataSnapshot.getValue().toString());

                                // Creating reference for subfolder (selecting subfolder by choosing the prefered category).
                                final DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference(memeFolder).child(category);

                                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        // Getting the hasmap of memes.
                                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                                        if (memeNumber >= td.size()) {
                                            System.out.println("Закончились мемесы.");
                                            // numberOfMemesInBuffer.put(category, 0);

                                        } else {
                                            currentMemeCategory.setValue(memeNumber + 1);
                                            //Decreaze number of meme of this category in buffer.
                                            numberOfMemesInBuffer.put(category, numberOfMemesInBuffer.get(category) - 1);
                                        }
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


                        /*
                         * Adding new picture.
                         */

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                // Defining the category of next meme.
                                final String categoryNext = LoadActivity.categories.get(UserLogic.UserMethods.getCategory(fireUser.getPreferencesList()));

                                // Creating reference for subfolder (selecting subfolder by choosing the prefered category).
                                DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference(memeFolder).child(categoryNext);

                                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        // Getting the list of meme values (links).
                                        final Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                                        // Getting and sorting the keys.
                                        final ArrayList<String> keys = new ArrayList<>(td.keySet());
                                        Collections.sort(keys);

                                        // Taking the number of the meme to pick.
                                        DatabaseReference memeCategory = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Categories_seen").child(categoryNext);

                                        memeCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                String memeUrl;
                                                Integer memeNumber;
                                                memeNumber = Integer.parseInt(dataSnapshot.getValue().toString()) + numberOfMemesInBuffer.get(categoryNext);
                                                if (memeNumber < td.size()) {
                                                    numberOfMemesInBuffer.put(categoryNext, numberOfMemesInBuffer.get(categoryNext) + 1);
                                                    memeUrl = td.get(keys.get(memeNumber)).toString();
                                                } else {
                                                    memeUrl = td.get(keys.get(keys.size() - 1)).toString();
                                                }

                                                // Making image of Url.
                                                PictureLogic.Data image = new PictureLogic.Data(memeUrl);

                                                // Making a Picture element (attaching category to a picture).
                                                PictureLogic.Picture picture = new PictureLogic.Picture(image, LoadActivity.categories.indexOf(categoryNext));

                                                // Add it to pic list.
                                                pictureList.add(picture);

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

                                /*
                                 * Removing the picture we just swiped.
                                 */

                                // Remove the object we just swiped.
                                pictureList.remove(0);
                                myAppAdapter.notifyDataSetChanged();

                                //Вибрация

//                        Intent intentVibrate = new Intent(getApplicationContext(), VibrateService.class);
//                        startService(intentVibrate);


                                System.out.println(fireUser.getPreferencesList().toString());


                            }
                        }, 10);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated left_to_right_1 API 26
                    v.vibrate(30);
                }

                deleteCache(getActivity().getApplicationContext());

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {


                View view = flingContainer.getSelectedView();
                //view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);


                myAppAdapter.notifyDataSetChanged();
            }
        });


        return view;
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    // Swipes handlers.
    public static class ViewHolder {
        public static FrameLayout background;
        public ImageView cardImage;
    }

    public class MyAppAdapter extends BaseAdapter {

        public List<PictureLogic.Picture> parkingList;
        public Context context;

        private MyAppAdapter(List<PictureLogic.Picture> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.image_item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.background = rowView.findViewById(R.id.background);
                viewHolder.cardImage = rowView.findViewById(R.id.cardImage);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Glide.with(MemesFragment.this).load(parkingList.get(position).Image.getImagePath()).into(viewHolder.cardImage);

            return rowView;
        }
    }
}
