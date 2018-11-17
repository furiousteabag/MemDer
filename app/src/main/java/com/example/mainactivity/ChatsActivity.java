package com.example.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ChatsActivity extends AppCompatActivity implements View.OnClickListener{

    Button Memes;
    Button Girl;
    TextView best;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        best = (TextView) findViewById(R.id.textView);
        best.setText(MainActivity.name);
        Girl = (Button) findViewById(R.id.button8);
        Girl.setOnClickListener(this);

        Memes = (Button) findViewById(R.id.button4);
        Memes.setOnClickListener(this);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.button4:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.button8:
                Intent intent1 = new Intent(this, Messages.class);
                startActivity(intent1);


                break;
            default: break;
        }
    }


    }

