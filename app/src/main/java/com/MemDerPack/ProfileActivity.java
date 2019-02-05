package com.MemDerPack;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.MemDerPack.Logic.SharedPref;
import com.bumptech.glide.Glide;
import com.MemDerPack.Logic.UserLogic;

import com.MemDerPack.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    // Activity elements.
    CircleImageView image_profile;
    TextView username;

    // Firebase elements.
    DatabaseReference reference;
    FirebaseUser fuser;

    // Storage reference (for image).
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    String userid;


    EditText user_description;

    Intent intent;

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
        setContentView(R.layout.activity_profile);

        // Toolbar initializing.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Профиль");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fuser.getUid().equals(userid)) {
                    Intent i = new Intent(ProfileActivity.this, MessageActivity.class);
                    i.putExtra("userid", userid);
                    startActivity(i);
                    finish();
//                    overridePendingTransition(R.anim.bottom_to_top_1, R.anim.bottom_to_top_2);
                } else if (intent.getStringExtra("form") != null) {
                    Intent i = new Intent(ProfileActivity.this, ChatsActivity.class);
                    startActivity(i);
                    finish();
//                    overridePendingTransition(R.anim.bottom_to_top_1, R.anim.bottom_to_top_2);
                }
            }
        });

        // Initializing activity elements.
        image_profile = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        user_description = findViewById(R.id.user_description);


//        View.OnClickListener editTextClickListener = new View.OnClickListener()
//
//        {
//
//            public void onClick(View v)
//            {
//                if (v.getId() == user_description.getId())
//                {
//                    user_description.setCursorVisible(true);
//                }
//
//            }
//        };
//
//        user_description.setOnClickListener(editTextClickListener);
//
//        user_description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId,
//                                          KeyEvent event) {
//                user_description.setCursorVisible(false);
//                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    in.hideSoftInputFromWindow(user_description.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//                return false;
//            }
//        });


        // Initialing firebase elements.
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        // Initializing storage element.
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        // Setting up listener for changing username and image.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Setting username.
                UserLogic.User user = dataSnapshot.getValue(UserLogic.User.class);
                username.setText(user.getUsername());

                // Setting image.
                if (user.getImageURL().equals("default")) {
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (fuser.getUid().equals(userid)) {
            // Listening for clicks on picture.
            image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImage();
                }
            });
        }


        // Setting up description from DB.
        setTextDescription();


        if (!fuser.getUid().equals(userid)) {
            user_description.setEnabled(false);
        }

        if (fuser.getUid().equals(userid)) {
            user_description.setEnabled(true);
        }


        if (fuser.getUid().equals(userid)) {
            // Edit description.
            user_description.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateDescription(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void setTextDescription() {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(userid).child("description");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (fuser.getUid().equals(userid)) {
                    if (dataSnapshot.getValue() != null) {
                        user_description.setText(dataSnapshot.getValue().toString());
                    }
                } else {
                    if (dataSnapshot.getValue() == null) {
                        user_description.setText("no information given");
                    } else {
                        user_description.setText(dataSnapshot.getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateDescription(String description) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        HashMap<String, Object> map = new HashMap<>();
        map.put("description", description);
        database.updateChildren(map);
    }

    private String getFileExtension(Uri uri) {
        // getContext()
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "No image selected.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null & data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), "Upload left_to_right_1 progress...", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }

        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (!fuser.getUid().equals(userid)) {
            Intent i = new Intent(ProfileActivity.this, MessageActivity.class);
            i.putExtra("userid", userid);
            startActivity(i);
            finish();
//            overridePendingTransition(R.anim.bottom_to_top_1, R.anim.bottom_to_top_2);
        } else if (intent.getStringExtra("form") != null) {
            Intent i = new Intent(ProfileActivity.this, ChatsActivity.class);
            startActivity(i);
            finish();
//            overridePendingTransition(R.anim.bottom_to_top_1, R.anim.bottom_to_top_2);
        }
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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


}
