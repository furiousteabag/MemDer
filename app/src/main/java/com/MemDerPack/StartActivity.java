package com.MemDerPack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.MemDerPack.Logic.SharedPref;
import com.MemDerPack.R;

public class StartActivity extends AppCompatActivity {

    // Activity elements.
    Button login, register;

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

        // Creating activity.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        onClick(getCurrentFocus());


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.left_to_right_1, R.anim.left_to_right_2);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.left_to_right_1, R.anim.left_to_right_2);
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onClick(View v) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
//        builder.setTitle("Политика приватности.")
//                .setMessage(Html.fromHtml("https://goo.gl/X3n6gn"))
//                .setCancelable(false)
//                .setNegativeButton("Понятно.",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//        AlertDialog alert = builder.create();
//        alert.show();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(StartActivity.this);

        builder1.setTitle("Политика приватности.");
        builder1.setMessage(Html.fromHtml("Чтобы продолжить, согласитесь с <a href=\"https://goo.gl/X3n6gn\">политикой приватности</a>"));

        builder1.setCancelable(false);
        builder1.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog Alert1 = builder1.create();
        Alert1 .show();
        ((TextView)Alert1.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}
