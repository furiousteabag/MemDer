package com.MemDerPack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.MemDerPack.Fragments.MemesFragment;
import com.MemDerPack.Logic.PictureLogic;
import com.MemDerPack.Logic.SharedPref;
import com.bumptech.glide.Glide;
import com.MemDerPack.Fragments.ChatsFragment;
import com.MemDerPack.Fragments.UsersFragment;
import com.MemDerPack.Logic.UserLogic;

import com.MemDerPack.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsActivity extends AppCompatActivity {

    // Firebase data.
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    // For nightmode.
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Setting theme.
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_lang", "");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // Creating activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        // Toolbar initializing.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MemDer");

        //Initializing firebase.
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        // Initializing activity elements.
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);


        // Initializing adapter.
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MemesFragment(), getString(R.string.memes));
        viewPagerAdapter.addFragment(new ChatsFragment(), getString(R.string.chats));
        viewPagerAdapter.addFragment(new UsersFragment(), getString(R.string.users));

        // Associating adapter with form elements.
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    // Methods to handle top menu with logout button.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatsActivity.this, StartActivity.class));
                finish();
                return true;
            case R.id.profile:
                Intent intent1 = new Intent(ChatsActivity.this, ProfileActivity.class);
                intent1.putExtra("userid", firebaseUser.getUid());
                intent1.putExtra("form", firebaseUser.getUid());
                startActivity(intent1);
                finish();
                return true;
            case R.id.settings:
                Intent intent2 = new Intent(ChatsActivity.this, SettingsActivity.class);
                startActivity(intent2);
                finish();
                return true;
        }
        return false;
    }

    // Pages class.
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    // Handling the chat button.
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_profile_click:
                Intent intent1 = new Intent(this, ProfileActivity.class);
                intent1.putExtra("userid", firebaseUser.getUid());
                intent1.putExtra("form", firebaseUser.getUid());
                startActivity(intent1);
                overridePendingTransition(R.anim.top_to_bottom_1, R.anim.top_to_bottom_2);
                break;
//            case R.id.profile_image:
//                Intent intent2 = new Intent(this, ProfileActivity.class);
//                intent2.putExtra("userid", firebaseUser.getUid());
//                intent2.putExtra("form", firebaseUser.getUid());
//                startActivity(intent2);
//                overridePendingTransition(R.anim.top_to_bottom_1, R.anim.top_to_bottom_2);
//                break;
            default:
                break;

        }
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_to_left_1, R.anim.right_to_left_2);
    }
}
