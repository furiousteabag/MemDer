package com.example.mainactivity.Logic;

import android.media.Image;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mainactivity.Logic.UserLogic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureLogic {

    // Picture element class.
    public static class Picture {

        public Data Image;
        public int Category;

        public Picture(Data image, int category) {
            Image = image;
            Category = category;
        }
    }

    // Categories class.
    public static class CategoryOfPictures {

        public int Category;
        public ArrayList<Picture> Pictures = new ArrayList<>();

        // Initializing a Category without pictures.
        public CategoryOfPictures(int category) {
            Category = category;
        }

        // Initializing a Category with array of pictures.
        public CategoryOfPictures(int category, ArrayList<Picture> pictures) {
            Category = category;
            ArrayList<Picture> Pictures = pictures;
        }

        // Method to add picture.
        public void AddPicture(Picture picture) {
            Pictures.add(picture);
        }

        // Method to take picture.
        public Picture TakePicture(int index) {
            return Pictures.get(index);
        }

        // Method to remove picture.
        public void RemovePicture(int index) {
            Pictures.remove(index);
        }

    }

    //Class contains id or path to picture.
    public static class Data {


//        private int drawableId;
//
//        public Data(int drawableId) {
//            this.drawableId = drawableId;
//        }
//
//        public int getImagePath() {
//            return drawableId;
//        }


        //   Получваем картинку по ссылке из инета.
        private String imagePath;

        public Data(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    // Methods for picture elements.
    public static class PictureMethods {


        // We take a user, picture and the fact whether we liked or not.
        public static UserLogic.User ChangePreference(UserLogic.User user, Picture pic, boolean liked) {
            // Index of a category to change
            int index = pic.Category;

            if (liked) {
                if (user.getPreferencesList().get(index) < 15)
                    user.setSinglePreferences(index, user.getPreferencesList().get(index) + 1);
            } else {
                if (user.getPreferencesList().get(index) > 0)
                    user.setSinglePreferences(index, user.getPreferencesList().get(index) - 1);
            }

            return user;
        }

//        // Initializing the pictures array.
//        public static ArrayList<Picture> intImagesToPictures(Integer[] images) {
//            ArrayList<Picture> pictures = new ArrayList<>();
//            for (int i = 0; i < 100; i++) {
//                pictures.add(new Picture(images[i], i % 10));
//            }
//            return pictures;
//        }

        // Filling the Categories Array.
        public static void FullingCategoryOfPictures(ArrayList<CategoryOfPictures> categories, ArrayList<Picture> pictures) {
            for (int i = 0; i < pictures.size() / 10; i++) {
                categories.add(new CategoryOfPictures(i));
                for (int j = 0; j < 10; j++) {
                    categories.get(i).AddPicture(pictures.get(i * 10 + j));
                }
            }
        }


        private byte[] image;

        public byte[] DownloadSinglePicture(String path) {

            image = null;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Memes/");


            storageReference.child(path).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    image = bytes;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


            return image;
        }

//        static List<Object> values;
//        public static List<Object> getListOfMemesFromCategory(String category){
//
//            values = null;
//
//            DatabaseReference memeReference = FirebaseDatabase.getInstance().getReference("Memes").child(category);
//
//            memeReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
//                    values = new ArrayList<>(td.values());
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//            return values;
//
//        }


//        public static void FullingBufferofPictures(Image[] images){
//            for (int i = 0; i < 10; i++){
//
//
//                final FirebaseStorage firebaseStorage = ;
//
//                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
//                final StorageReference imgRef = mStorageRef.child("images/img.jpg");
//                final long ONE_MEGABYTE = 1024*1024;
//
//
//
//
//                imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Glide.with(Cari.this).load(imgRef).diskCacheStrategy(DiskCacheStrategy.ALL).into(kuryerImg);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//
//            }
//        }
    }
}