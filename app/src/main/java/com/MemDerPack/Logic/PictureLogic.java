package com.MemDerPack.Logic;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class PictureLogic {

    // Picture element class.
    public static class Picture {



        public long id;
        public static long id_next;
        public String ImagePath;
        public int Category;

        public Picture() {
        }

        public Picture(String image, int category) {
            id = id_next;
            id_next++;
            ImagePath = image;
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


    // Methods for picture elements.
    public static class PictureMethods {


        // We take a user, picture and the fact whether we liked or not.
        public static UserLogic.User ChangePreference(UserLogic.User user, Picture pic, String forHowMuchLiked) {
            // Index of a category to change
            int index = pic.Category;

            if (forHowMuchLiked.equals("like")) {
                if (user.getPreferencesList().get(index) < 15)
                    user.setSinglePreferences(index, user.getPreferencesList().get(index) + 1);
            } else if (forHowMuchLiked.equals("dislike")) {
                if (user.getPreferencesList().get(index) > 0)
                    user.setSinglePreferences(index, user.getPreferencesList().get(index) - 1);
            } else if (forHowMuchLiked.equals("superlike")) {
                if (user.getPreferencesList().get(index) < 13)
                    user.setSinglePreferences(index, user.getPreferencesList().get(index) + 3);
            } else if (forHowMuchLiked.equals("superdislike")) {
                if (user.getPreferencesList().get(index) > 2)
                    user.setSinglePreferences(index, user.getPreferencesList().get(index) - 3);
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