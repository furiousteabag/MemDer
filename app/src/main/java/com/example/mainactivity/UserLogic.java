package com.example.mainactivity;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class UserLogic {

    // User class.
    public static class User {

        public ArrayList<Integer> Preferences = new ArrayList<>();
        public String Name;

        // When initializing a user we fill the preferences list
        // with 0s and addind a name.
        public User(String name) {
            for (int i = 0; i < 10; i++) {
                Preferences.add(0);
            }
            Name = name;
        }
    }

    // The same as user, but fills the Preferences with random numbers (0-15).
    public static class RandomUser extends User {

        // When initializing a RandomUser we fill the preferences list
        // with random numbers and addind a name.
        public RandomUser(String name) {
            super(name);
            for (int i = 0; i < 10; i++) {
                Preferences.add((int) (Math.random() * 15));
            }
            Name = name;
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


    // List of random girls.
    public static ArrayList<User> GetRandomUserArray() {
        ArrayList<User> AllGirls = new ArrayList<>();
        AllGirls.add(new User("Veronika"));
        AllGirls.add(new User("Anna"));
        AllGirls.add(new User("Oksana"));
        AllGirls.add(new User("Sveta"));
        AllGirls.add(new User("Angela"));
        AllGirls.add(new User("Sneganna"));
        AllGirls.add(new User("Ira"));
        AllGirls.add(new User("Alona"));
        AllGirls.add(new User("Vika"));
        AllGirls.add(new User("Nastya"));
        return AllGirls;
    }


}