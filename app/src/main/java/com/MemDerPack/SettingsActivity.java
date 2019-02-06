package com.MemDerPack;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.MemDerPack.Logic.SharedPref;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    // Activity elements.
    private Switch myswitch;
    Button btnChangeLanguage;

    // Theme variable.
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

        // Setting language.
        loadLocale();

        // Creating activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // Assosiating elements.
        btnChangeLanguage = findViewById(R.id.btnChangeLanguage);
        myswitch = (Switch) findViewById(R.id.myswitch);
        if (sharedPref.loadNightModeState()) {
            myswitch.setChecked(true);
        }
        // Toolbar initializing.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SettingsActivity.this, ChatsActivity.class);
                startActivity(i);
                finish();
                //overridePendingTransition(R.anim.bottom_to_top_1, R.anim.bottom_to_top_2);

            }
        });

        // Theme checker listener.
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPref.setNightModeState(true);
                    restartApp();
                } else {
                    sharedPref.setNightModeState(false);
                    restartApp();
                }
            }
        });

        // Language button listener.
        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });

    }

    // Restart activity.
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    // Show language change message.
    private void showChangeLanguageDialog() {

        // List of languages.
        final String[] listItems = {"Русский", "English"};

        // Building the alert.
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
        mBuilder.setTitle(getString(R.string.choose_language));
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    setLocale("ru");
                    recreate();
                } else if (which == 1) {
                    setLocale("en");
                    recreate();
                }

                // Remove dialog when selected.
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    //ToDo: move these methods to SharedPrefs.

    // Setting new locale.
    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // Saving data to shared preferences.
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_lang", lang);
        editor.apply();
    }

    // Load language from shared preferences.
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_lang", "");
        setLocale(language);
    }




}
