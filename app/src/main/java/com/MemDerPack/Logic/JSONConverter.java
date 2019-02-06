package com.MemDerPack.Logic;

import com.MemDerPack.Logic.PictureLogic;
import com.google.gson.Gson;

import java.util.ArrayList;

public class JSONConverter {

    static Gson json = new Gson();

    public static String convertToJSON(ArrayList<PictureLogic.Picture> pictureList) {

        String jsonFile = "";

        for (int i = pictureList.size()-9; i < pictureList.size(); i++) {

            jsonFile += json.toJson(pictureList.get(i));
            jsonFile += "@";
        }

        return jsonFile;
    }

    public static ArrayList<PictureLogic.Picture> convertFromJSON(String jsonFile){
        ArrayList<PictureLogic.Picture> pictureList = new ArrayList<PictureLogic.Picture>();
        PictureLogic.Picture picture = new PictureLogic.Picture();

        String[] jsonFileArray = jsonFile.split("@");

        for (int i = 0; i<10; i++){

            pictureList.add(json.fromJson(jsonFileArray[i], PictureLogic.Picture.class));
        }

        return pictureList;
    }

}
