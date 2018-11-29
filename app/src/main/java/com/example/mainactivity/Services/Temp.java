//package com.example.mainactivity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Build;
//import android.os.VibrationEffect;
//import android.os.Vibrator;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//
//
//import com.bumptech.glide.Glide;
//import com.example.mainactivity.Logic.PictureLogic;
//import com.example.mainactivity.Logic.UserLogic;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.widget.BaseAdapter;
//import android.widget.FrameLayout;
//
//
//import com.lorentzos.flingswipe.SwipeFlingAdapterView;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//
//public class GOVNO extends AppCompatActivity implements View.OnClickListener {
//
//    // Categories array.
//    public static final ArrayList<String> categories = new ArrayList<String>() {{
//        add("abstract");
//        add("anime");
//        add("cats");
//        add("cybersport");
//        add("disgraceful");
//        add("lentach");
//        add("mhk");
//        add("normal");
//        add("physkek");
//        add("programmer");
//    }};
//
//    // Arraylist of memes.
//    private ArrayList<PictureLogic.Picture> pictureList;
//
//    // Initializing activity elements.
//    Button btnChats;
//    CircleImageView profile_image;
//    Button username;
//
//    // Initializing firebase elements.
//    FirebaseUser firebaseUser;
//    DatabaseReference reference;
//    DatabaseReference referenceChangeRight;
//    DatabaseReference referenceChangeLeft;
//
//    // Swipes
//    public static MyAppAdapter myAppAdapter;
//    public static ViewHolder viewHolder;
//    private SwipeFlingAdapterView flingContainer;
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Toolbar initializing.
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//
//        // Associating activity elements.
//        profile_image = findViewById(R.id.profile_image);
//        username = findViewById(R.id.username);
//        btnChats = findViewById(R.id.btn_chats);
//        btnChats.setOnClickListener(this);
//        flingContainer = findViewById(R.id.frame);
//
//        // Associating firebase variables
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        referenceChangeRight = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        referenceChangeLeft = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//        // Handling username and image.
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                // Setting username.
//                UserLogic.User fireUser = dataSnapshot.getValue(UserLogic.User.class);
//                username.setText(fireUser.getUsername());
//
//                // Setting image.
//                if (fireUser.getImageURL().equals("default")) {
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Glide.with(MainActivity.this).load(fireUser.getImageURL()).into(profile_image);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        /*
//         * SWIPES HANDLING
//         */
//
//        // Take the picturelist from startactivity.
//        pictureList = LoadActivity.pictureList;
//
//        // Initializing adapter.
//        myAppAdapter = new MyAppAdapter(pictureList, MainActivity.this);
//        flingContainer.setAdapter(myAppAdapter);
//
//        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
//            @Override
//            public void removeFirstObjectInAdapter() {
//
//            }
//
//            @Override
//            public void onLeftCardExit(Object dataObject) {
//
//                // Database synchronization.
//                referenceChangeRight.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        /*
//                         * Changing preferences.
//                         */
//
//                        // Creating new reference.
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//                        // Initializing local user.
//                        UserLogic.User fireUser = dataSnapshot.getValue(UserLogic.User.class);
//
//                        // Changing preferences.
//                        PictureLogic.PictureMethods.ChangePreference(fireUser, pictureList.get(0), false);
//
//                        // Adding point to a category of current meme.
//
//                        final DatabaseReference currentMemeCategory = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Categories_seen").child(categories.get(pictureList.get(0).Category));
//                        currentMemeCategory.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                //  dataSnapshot.setValue(memeNumber + 1);
//                                dataSnapshot.
//
//                                // Creating reference for subfolder (selecting subfolder by choosing the prefered category).
//                                final DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference("Memes").child(categories.get(pictureList.get(0).Category));
//                                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                        // Getting the list of meme values (links).
//                                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
//                                        final List<Object> values = new ArrayList<>(td.values());
//
//                                        // Current meme number.
//                                        Integer memeNumber = Integer.parseInt(dataSnapshot.getValue().toString());
//                                        if (memeNumber >= values.size()) {
//                                            System.out.println("Закончились мемесы.");
//
//
//                                            return;
//                                        }
//
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
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
//
//
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//
//                // Sending them to sever.
//                databaseReference.child("Users").child(firebaseUser.getUid()).child("preferences").setValue(fireUser.getPreferencesList().toString());
//
//                /*
//                 * Adding new picture.
//                 */
//
//                // Defining the category of next meme.
//                final String category = categories.get(UserLogic.UserMethods.getCategory(fireUser.getPreferencesList()));
//
//                // Creating reference for subfolder (selecting subfolder by choosing the prefered category).
//                final DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference("Memes").child(category);
//
//                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        // Getting the list of meme values (links).
//                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
//                        final List<Object> values = new ArrayList<>(td.values());
//
//                        // Choose random meme (link).
//                        Random randomizer = new Random();
//                        Object randomMemeUrlObject = values.get(randomizer.nextInt(values.size()));
//                        String randomMemeUrl = String.valueOf(randomMemeUrlObject);
//
//                        // Taking the number of the meme to pick.
//                        final DatabaseReference memeCategory = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Categories_seen").child(category);
//                        memeCategory.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                // Current meme number.
//                                Integer memeNumber = Integer.parseInt(dataSnapshot.getValue().toString());
//                                if (memeNumber >= values.size()) {
//                                    System.out.println("Закончились мемесы.");
//
//
//                                    return;
//                                }
//                                // Making image left_to_right_2 of Url.
//                                PictureLogic.Data image = new PictureLogic.Data(String.valueOf(values.get(memeNumber)));
//
//                                memeCategory.setValue(memeNumber + 1);
//
//
//                                // Making a Picture element (attaching category to a picture).
//                                PictureLogic.Picture picture = new PictureLogic.Picture(image, categories.indexOf(category));
//
//                                // Add it to pic list.
//                                pictureList.add(picture);
//                                myAppAdapter.notifyDataSetChanged();
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
//                        // Remove the object we just swiped.
//                        pictureList.remove(0);
//                        myAppAdapter.notifyDataSetChanged();
//
//                        // Making image left_to_right_2 of Url.
//                        //PictureLogic.Data image = new PictureLogic.Data(randomMemeUrl);
//
//                        // Making a Picture element (attaching category to a picture).
//                        //PictureLogic.Picture picture = new PictureLogic.Picture(image, categories.indexOf(category));
//
//                        // Add it to pic list.
//                        //pictureList.add(picture);
//                        //myAppAdapter.notifyDataSetChanged();
//                        System.out.println(pictureList.toString());
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//                /*
//                 * Removing the picture we just swiped.
//                 */
//
//
//                //Вибрация
//
////                        Intent intentVibrate = new Intent(getApplicationContext(), VibrateService.class);
////                        startService(intentVibrate);
//
//                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                // Vibrate for 500 milliseconds
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
//                } else {
//                    //deprecated left_to_right_1 API 26
//                    v.vibrate(50);
//                }
//
//                System.out.println(fireUser.getPreferencesList().toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onRightCardExit(Object dataObject) {
//
//        // Database synchronization.
//        referenceChangeRight.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                /*
//                 * Changing preferences.
//                 */
//
//                // Creating new reference.
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//                // Initializing local user.
//                UserLogic.User fireUser = dataSnapshot.getValue(UserLogic.User.class);
//
//                // Changing preferences.
//                PictureLogic.PictureMethods.ChangePreference(fireUser, pictureList.get(0), true);
//
//                // Sending them to sever.
//                databaseReference.child("Users").child(firebaseUser.getUid()).child("preferences").setValue(fireUser.getPreferencesList().toString());
//
//                /*
//                 * Adding new picture.
//                 */
//
//                // Defining the category of next meme.
//                final String category = categories.get(UserLogic.UserMethods.getCategory(fireUser.getPreferencesList()));
//
//                // Creating reference for subfolder (selecting subfolder by choosing the prefered category).
//                DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference("Memes").child(category);
//
//                memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        // Getting the list of meme values (links).
//                        Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
//                        List<Object> values = new ArrayList<>(td.values());
//
//                        // Choose random meme (link).
//                        Random randomizer = new Random();
//                        Object randomMemeUrlObject = values.get(randomizer.nextInt(values.size()));
//                        String randomMemeUrl = String.valueOf(randomMemeUrlObject);
//
//                        // Making image left_to_right_2 of Url.
//                        PictureLogic.Data image = new PictureLogic.Data(randomMemeUrl);
//
//                        // Making a Picture element (attaching category to a picture).
//                        PictureLogic.Picture picture = new PictureLogic.Picture(image, categories.indexOf(category));
//
//                        // Add it to pic list.
//                        pictureList.add(picture);
//                        myAppAdapter.notifyDataSetChanged();
//
//                        System.out.println(pictureList.toString());
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//                /*
//                 * Removing the picture we just swiped.
//                 */
//
//                // Remove the object we just swiped.
//                pictureList.remove(0);
//                myAppAdapter.notifyDataSetChanged();
//
//                //Вибрация
//
////                        Intent intentVibrate = new Intent(getApplicationContext(), VibrateService.class);
////                        startService(intentVibrate);
//
//                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                // Vibrate for 500 milliseconds
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
//                } else {
//                    //deprecated left_to_right_1 API 26
//                    v.vibrate(50);
//                }
//
//                System.out.println(fireUser.getPreferencesList().toString());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onAdapterAboutToEmpty(int itemsInAdapter) {
//
//    }
//
//    @Override
//    public void onScroll(float scrollProgressPercent) {
//
//        View view = flingContainer.getSelectedView();
//        view.findViewById(R.id.background).setAlpha(0);
//        view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//        view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
//    }
//});
//
//
//        // Optionally add an OnItemClickListener
//        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener(){
//@Override
//public void onItemClicked(int itemPosition,Object dataObject){
//
//        View view=flingContainer.getSelectedView();
//        view.findViewById(R.id.background).setAlpha(0);
//
//        myAppAdapter.notifyDataSetChanged();
//        }
//        });
//
//
//        }
//
//
//// Methods to handle top menu with logout button.
//@Override
//public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu,menu);
//        return true;
//        }
//
//@Override
//public boolean onOptionsItemSelected(MenuItem item){
//        switch(item.getItemId()){
//        case R.id.logout:
//        FirebaseAuth.getInstance().signOut();
//        startActivity(new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        return true;
//        }
//        return false;
//        }
//
//// Handling the chat button.
//public void onClick(View v){
//        switch(v.getId()){
//        case R.id.btn_chats:
//        Intent intent=new Intent(this,ChatsActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.left_to_right_1,R.anim.left_to_right_2);
//        break;
//        case R.id.username:
//        Intent intent1=new Intent(this,ProfileActivity.class);
//        startActivity(intent1);
//        overridePendingTransition(R.anim.top_to_bottom_1,R.anim.top_to_bottom_2);
//
//default:
//        break;
//
//        }
//        }
//
//
//// Swipes handlers.
//public static class ViewHolder {
//    public static FrameLayout background;
//    public ImageView cardImage;
//}
//
//public class MyAppAdapter extends BaseAdapter {
//
//    public List<PictureLogic.Picture> parkingList;
//    public Context context;
//
//    private MyAppAdapter(List<PictureLogic.Picture> apps, Context context) {
//        this.parkingList = apps;
//        this.context = context;
//    }
//
//    @Override
//    public int getCount() {
//        return parkingList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        View rowView = convertView;
//
//
//        if (rowView == null) {
//
//            LayoutInflater inflater = getLayoutInflater();
//            rowView = inflater.inflate(R.layout.image_item, parent, false);
//            // configure view holder
//            viewHolder = new ViewHolder();
//            viewHolder.background = rowView.findViewById(R.id.background);
//            viewHolder.cardImage = rowView.findViewById(R.id.cardImage);
//            rowView.setTag(viewHolder);
//
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        Glide.with(MainActivity.this).load(parkingList.get(position).Image.getImagePath()).into(viewHolder.cardImage);
//
//        return rowView;
//    }
//
//}
//
//    private void status(String status) {
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//
//        reference.updateChildren(hashMap);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        status("online");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        status("offline");
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent startMain = new Intent(Intent.ACTION_MAIN);
//            startMain.addCategory(Intent.CATEGORY_HOME);
//            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(startMain);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//}