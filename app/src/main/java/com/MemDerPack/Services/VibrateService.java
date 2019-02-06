package com.MemDerPack.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

public class VibrateService extends Service {

    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);

        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        //Указываем длительность вибрации в миллисекундах:
        vibrator.vibrate(2000);

        Toast.makeText(getApplicationContext(), "Вибрация активирована", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}