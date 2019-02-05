package com.MemDerPack.Logic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.MemDerPack.R;

import java.util.Locale;

public class SharedPref {

    SharedPreferences mySharedPref ;

    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("filename",Context.MODE_PRIVATE);
    }

    // This method will save the nightMode State : True or False.
    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode",state);
        editor.commit();
    }

    // This method will load the Night Mode State.
    public Boolean loadNightModeState (){
        Boolean state = mySharedPref.getBoolean("NightMode",false);
        return  state;
    }


}
