package com.example.mainactivity.Logic;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserLogic {

    // User class.
    public static class User {

        private String id;
        private String imageURL;
        private String username;
        private String preferences;
        private ArrayList<Integer> Preferences = new ArrayList<>();

        public User() {
        }

        public User(String preferences, String id, String imageURL, String username) {
            this.id = id;
            this.imageURL = imageURL;
            this.username = username;
            this.preferences = preferences;
            Preferences = stringToArrayList(preferences);
        }

        //---some getters and setters---
        public String getPreferences() {
            return preferences;
        }

        public void setPreferences(String preferences) {
            this.preferences = preferences;
            Preferences = stringToArrayList(preferences);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public ArrayList<Integer> getPreferencesList() {
            return Preferences;
        }

        public void setSinglePreferences(int index, int number) { Preferences.set(index, number); }

        public void setUsername(String username) { this.username = username; }

        public String getUsername() {
            return username;
        }

        // Transform a String into ArrayList.
        public ArrayList<Integer> stringToArrayList(String preferences) {
            String replace = preferences.replace("[", "").replace("]", "");
            List<String> arrayList = new ArrayList<String>(Arrays.asList(replace.split(",")));
            ArrayList<Integer> Preferences = new ArrayList<>();
            for (String fav : arrayList) {
                Preferences.add(Integer.parseInt(fav.trim()));
            }
            return Preferences;
        }
    }

    // Methods attached to User element.
    public static class UserMethods {
        // Returns the percentage of similarity of two person.
        public int SimilarityRatio(User me, User possibleFriend) {
            double rez = 0;
            int temp = 0;
            for (int i = 0; i < 10; i++) {
                temp += Math.max(me.Preferences.get(i), possibleFriend.Preferences.get(i));
                rez += Math.abs(me.Preferences.get(i) - possibleFriend.Preferences.get(i));
            }
            if (temp > 0)
                return ((int) Math.round(100 - rez / temp * 100));
            return (0);
        }

        // Returns the most similar user of array of users.
        public User FindFriend(User me, ArrayList<User> allPossibleFriends) {
            int max = 0;
            int temp = 0;
            User fittest = null;
            for (User currentUser : allPossibleFriends) {
                temp = SimilarityRatio(me, currentUser);
                if (temp >= max) {
                    max = temp;
                    fittest = currentUser;
                }
            }
            return (fittest);
        }

        // Function that gets Preferences and returns category
        public static int GetCategory(ArrayList<Integer> list) {
            int Sum = 10;
            int l = 0;
            //Random r = new Random();

            for (int i = 0; i <= 9; i++) {
                Sum += list.get(i);
            }
            //int res = //r.nextInt(Sum);
            int res = (int) (Math.random() * Sum) + 1;

            System.out.println(Sum + " " + res);
            while (res > 0) {
                res = res - list.get(l) - 1;
                l++;
            }
            System.out.println(l - 1);

            return l - 1;
        }
    }


//    // List of random girls.
//    public static ArrayList<User> GetRandomUserArray() {
//        ArrayList<User> AllGirls = new ArrayList<>();
//        AllGirls.add(new User("Veronika"));
//        AllGirls.add(new User("Anna"));
//        AllGirls.add(new User("Oksana"));
//        AllGirls.add(new User("Sveta"));
//        AllGirls.add(new User("Angela"));
//        AllGirls.add(new User("Sneganna"));
//        AllGirls.add(new User("Ira"));
//        AllGirls.add(new User("Alona"));
//        AllGirls.add(new User("Vika"));
//        AllGirls.add(new User("Nastya"));
//        return AllGirls;
//    }


}
