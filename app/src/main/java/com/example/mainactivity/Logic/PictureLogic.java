package com.example.mainactivity.Logic;

import com.example.mainactivity.Logic.UserLogic;

import java.util.ArrayList;

public class PictureLogic {

    // Picture element class.
    public static class Picture {

        public int Image;
        public int Category;

        public Picture(int image, int category) {
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

        // Initializing the pictures array.
        public static ArrayList<Picture> intImagesToPictures(Integer[] images) {
            ArrayList<Picture> pictures = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                pictures.add(new Picture(images[i], i % 10));
            }
            return pictures;
        }

        // Filling the Categories Array.
        public static void FullingCategoryOfPictures(ArrayList<CategoryOfPictures> categories, ArrayList<Picture> pictures) {
            for (int i = 0; i < pictures.size() / 10; i++) {
                categories.add(new CategoryOfPictures(i));
                for (int j = 0; j < 10; j++) {
                    categories.get(i).AddPicture(pictures.get(i * 10 + j));
                }
            }
        }


    }
}