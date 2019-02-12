package com.MemDerPack.Logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserLogic {

    // User class.
    public static class User {

        private String id;
        private String imageURL;
        private String username;
        private String preferences;
        private ArrayList<Integer> Preferences = new ArrayList<>();
        private String status;
        private String description;
        private Map<String, Object> Categories_seen;

        public User() {
        }

        public User(String preferences, String id, String imageURL, String username, String status) {
            this.id = id;
            this.imageURL = imageURL;
            this.username = username;
            this.preferences = preferences;
            Preferences = stringToArrayList(preferences);
            this.status = status;
        }

        //---some getters and setters---
        public String getPreferences() {
            return preferences;
        }

        public void setPreferences(String preferences) {
            this.preferences = preferences;
            Preferences = stringToArrayList(preferences);
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<String, Object> getCategories_seen() {
            return Categories_seen;
        }

        public void setCategories_seen(Map<String, Object> categories_seen) {
            Categories_seen = categories_seen;
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
        public static int SimilarityRatio(User me, User possibleFriend) {
            double rez = 0;
            int temp = 0;
            for (int i = 0; i < me.Preferences.size(); i++) {
                temp += Math.max(me.Preferences.get(i), possibleFriend.Preferences.get(i));
                rez += Math.abs(me.Preferences.get(i) - possibleFriend.Preferences.get(i));
            }
            if (temp > 0)
                return ((int) Math.round(100 - rez / temp * 100));
            return (0);
        }

        // Returns the most similar user of array of users.
        public static User FindFriend(User me, ArrayList<User> allPossibleFriends) {
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
        public static int getCategory(ArrayList<Integer> list) {
            int Sum = list.size();
            int l = 0;
            //Random r = new Random();

            for (int i = 0; i <= list.size()-1; i++) {
                Sum += list.get(i);
            }
            //int res = //r.nextInt(Sum);
            int res = (int) (Math.random() * Sum) + 1;

            while (res > 0) {
                res = res - list.get(l) - 1;
                l++;
            }


            return l - 1;
        }

        // Takes current user and list of all users and returns the sorted by preferences users array.
        public static ArrayList<User> sortUsers(UserLogic.User user, ArrayList<User> userList){
            ArrayList<UserLogic.User> sortedUsers = new ArrayList<>();
            int size = userList.size();
            for(int i = 0; i < size; i++){
                User current = FindFriend(user, userList);
                sortedUsers.add(current);
                userList.remove(current);
            }

            return sortedUsers;
        }


    }


}
