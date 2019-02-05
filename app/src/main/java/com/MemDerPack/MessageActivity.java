package com.MemDerPack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.MemDerPack.Fragments.APIService;
import com.MemDerPack.Logic.SharedPref;
import com.MemDerPack.Notifications.Client;
import com.MemDerPack.Notifications.Data;
import com.MemDerPack.Notifications.MyResponse;
import com.MemDerPack.Notifications.Sender;
import com.MemDerPack.Notifications.Token;
import com.bumptech.glide.Glide;
import com.MemDerPack.Adapter.MessageAdapter;
import com.MemDerPack.Logic.Chat;
import com.MemDerPack.Logic.UserLogic;

import com.MemDerPack.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    // Activity elements.
    CircleImageView profile_image;
    TextView username;
    ImageButton btn_send;
    EditText text_send;
    Button btn_profile_click;
    RecyclerView recyclerView;

    // Initializing firebase elements.
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String userid;
    boolean notify = false;

    // Message adapter.
    MessageAdapter messageAdapter;
    List<Chat> mchat;

    // For check if messages are seen.
    ValueEventListener seenListener;

    // Some intent.
    Intent intent;

    // For notifications.
    APIService apiService;

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
        setContentView(R.layout.activity_message);

        // Toolbar initializing.
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessageActivity.this, ChatsActivity.class);
                startActivity(i);
                finish();
//                overridePendingTransition(R.anim.right_to_left_1, R.anim.right_to_left_2);
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Associating activity elements.
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_profile_click = findViewById(R.id.btn_profile_click);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        // Associating firebase variables.

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        // Handling username and image.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Setting username.
                UserLogic.User fireUser = dataSnapshot.getValue(UserLogic.User.class);
                username.setText(fireUser.getUsername());

                // Setting image.
                if (fireUser.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(fireUser.getImageURL()).into(profile_image);
                }

                readMessages(firebaseUser.getUid(), userid, fireUser.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    try {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isseen", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(final String sender, final String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        // Add user to chat fragment.
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(sender)
                .child(receiver);

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(sender);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(receiver);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRefReceiver.child("id").setValue(sender);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    UserLogic.User user = dataSnapshot.getValue(UserLogic.User.class);
                    if (notify) {
                        sendNotification(receiver, user.getUsername(), msg);
                    }
                    notify = false;

                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New message", userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readMessages(final String myid, final String userid, final String imageurl) {

        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        // Handling username and image.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    try {


                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                            mchat.add(chat);
                        }

                        messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                        recyclerView.setAdapter(messageAdapter);
                    } catch (Exception e) {
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    // Handling the chat button.
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.username:
//                Intent intent1 = new Intent(this, ProfileActivity.class);
//                intent1.putExtra("userid", userid);
//                startActivity(intent1);
//                overridePendingTransition(R.anim.top_to_bottom_1, R.anim.top_to_bottom_2);
//                break;
//            case R.id.profile_image:
//                Intent intent2 = new Intent(this, ProfileActivity.class);
//                intent2.putExtra("userid", userid);
//                startActivity(intent2);
//                overridePendingTransition(R.anim.top_to_bottom_1, R.anim.top_to_bottom_2);
//                break;
            case R.id.btn_profile_click:
                Intent intent2 = new Intent(this, ProfileActivity.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
//                overridePendingTransition(R.anim.top_to_bottom_1, R.anim.top_to_bottom_2);
                break;


            default:
                break;

        }
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
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
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MessageActivity.this, ChatsActivity.class);
        startActivity(i);
        finish();
//        overridePendingTransition(R.anim.right_to_left_1, R.anim.right_to_left_2);
    }
}
