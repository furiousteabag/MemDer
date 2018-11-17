package com.example.mainactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnChats;
    ImageSwitcher imageSwitcher;

    UserLogic.User Me = new UserLogic.User("Frontender");


    public static String name = "";
    //Test line


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // OnCreate - Fedor's huinya, which we do not touch.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnChats = findViewById(R.id.button2);
        btnChats.setOnClickListener(this);
        imageSwitcher = findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(
                        new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.out);
        imageSwitcher.setOutAnimation(out);


        //Filling the categories with pictures.
        PictureLogic.PictureMethods.FullingCategoryOfPictures(categories, pictureList);

        // Setting the first image.
        imageSwitcher.setImageResource(pictureList.get(firstImageIndex).Image);

        // Fedino govno.
        Animation in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.in);
        imageSwitcher.setInAnimation(in);


        imageSwitcher.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                boolean flag = true;
                int category = 0;

                Animation out2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.out2);
                imageSwitcher.setOutAnimation(out2);

                PictureLogic.PictureMethods.ChangePreference(Me, currentPicture, flag);
                currentPicture = pictureList.get(UserLogic.UserMethods.GetCategory(Me.Preferences) * 10 + (int) (Math.random() * 9));
                imageSwitcher.setImageResource(currentPicture.Image);
                System.out.println(Me.Preferences.toString());
            }

            public void onSwipeLeft() {
                boolean flag = false;
                int category = 0;

                Animation out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.out);
                imageSwitcher.setOutAnimation(out);

                PictureLogic.PictureMethods.ChangePreference(Me, currentPicture, flag);
                currentPicture = pictureList.get(UserLogic.UserMethods.GetCategory(Me.Preferences) * 10 + (int) (Math.random() * 9));
                imageSwitcher.setImageResource(currentPicture.Image);
                System.out.println(Me.Preferences.toString());
            }
        });


    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                Intent intent = new Intent(this, ChatsActivity.class);
                startActivity(intent);
//                ArrayList<BusinessLogic.User> AllGirls = BusinessLogic.GetRandomUserArray();
//                name = Me.FindGirl(AllGirls).Name;
//                for (BusinessLogic.User p : AllGirls) {
//                    System.out.println(p.Name + " " + Me.koef(p) + " " + p.Preferences.toString());
//                }
//                System.out.println(name);
                break;
            default:
                break;

        }
    }

    // Initializing image array.
    Integer[] images =
            {
                    R.drawable.x00, R.drawable.x01, R.drawable.x02, R.drawable.x03, R.drawable.x04, R.drawable.x05, R.drawable.x06,
                    R.drawable.x07, R.drawable.x08, R.drawable.x09,
                    R.drawable.x10, R.drawable.x11, R.drawable.x12, R.drawable.x13, R.drawable.x14, R.drawable.x15, R.drawable.x16,
                    R.drawable.x17, R.drawable.x18, R.drawable.x19,
                    R.drawable.x20, R.drawable.x21, R.drawable.x22, R.drawable.x23, R.drawable.x24, R.drawable.x25, R.drawable.x26,
                    R.drawable.x27, R.drawable.x28, R.drawable.x29,
                    R.drawable.x30, R.drawable.x31, R.drawable.x32, R.drawable.x33, R.drawable.x34, R.drawable.x35, R.drawable.x36,
                    R.drawable.x37, R.drawable.x38, R.drawable.x39,
                    R.drawable.x40, R.drawable.x41, R.drawable.x42, R.drawable.x43, R.drawable.x44, R.drawable.x45, R.drawable.x46,
                    R.drawable.x47, R.drawable.x48, R.drawable.x49,
                    R.drawable.x50, R.drawable.x51, R.drawable.x52, R.drawable.x53, R.drawable.x54, R.drawable.x55, R.drawable.x56,
                    R.drawable.x57, R.drawable.x58, R.drawable.x59,
                    R.drawable.x60, R.drawable.x61, R.drawable.x62, R.drawable.x63, R.drawable.x64, R.drawable.x65, R.drawable.x66,
                    R.drawable.x67, R.drawable.x68, R.drawable.x69,
                    R.drawable.x70, R.drawable.x71, R.drawable.x72, R.drawable.x73, R.drawable.x74, R.drawable.x75, R.drawable.x76,
                    R.drawable.x77, R.drawable.x78, R.drawable.x79,
                    R.drawable.x80, R.drawable.x81, R.drawable.x82, R.drawable.x83, R.drawable.x84, R.drawable.x85, R.drawable.x86,
                    R.drawable.x87, R.drawable.x88, R.drawable.x89,
                    R.drawable.x90, R.drawable.x91, R.drawable.x92, R.drawable.x93, R.drawable.x94, R.drawable.x95, R.drawable.x96,
                    R.drawable.x97, R.drawable.x98, R.drawable.x99,
            };

    // Initializing Categories array.
    ArrayList<PictureLogic.CategoryOfPictures> categories = new ArrayList<>();

    // Initializing a Picture array.
    ArrayList<PictureLogic.Picture> pictureList = PictureLogic.PictureMethods.intImagesToPictures(images);

    int firstImageIndex = (int) (Math.random() * 99);
    public PictureLogic.Picture currentPicture = pictureList.get(firstImageIndex);


}