package com.MemDerPack.Logic;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.MemDerPack.Logic.PictureLogic;
import com.google.gson.Gson;

import java.util.ArrayList;

public class JSONConverter {

    static Gson json = new Gson();

    public static String convertToJSON(ArrayList<PictureLogic.Picture> pictureList, int numberOfCurrentMeme) {

        String jsonFile = "";

        for (int i = numberOfCurrentMeme; i < pictureList.size(); i++) {

            jsonFile += json.toJson(pictureList.get(i));
            jsonFile += "@";
        }

        Log.d("JSON_TO", jsonFile);

        return jsonFile;
    }

    public static ArrayList<PictureLogic.Picture> convertFromJSON(String jsonFile){
        ArrayList<PictureLogic.Picture> pictureList = new ArrayList<PictureLogic.Picture>();

        String[] jsonFileArray = jsonFile.split("@");

        for (int i = 0; i<jsonFileArray.length; i++){

            pictureList.add(json.fromJson(jsonFileArray[i], PictureLogic.Picture.class));
        }

        Log.d("JSON_FROM_SIZE", String.valueOf(pictureList.size()));
        Log.d("JSON_FROM", pictureList.toString());

        return pictureList;
    }

}
