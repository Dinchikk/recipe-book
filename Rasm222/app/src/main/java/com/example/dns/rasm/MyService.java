package com.example.dns.rasm;

import java.util.concurrent.TimeUnit;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {
    int status = 0; // 0-старт сервиса, 1-запуск, 2-приостановка, 3-сброс
    int time_counter;
    int _time;
    PendingIntent pi;


    public int onStartCommand(Intent intent, int flags, int startId) {
        pi = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);
        _time = intent.getIntExtra(MainActivity.PARAM_TIME, 1);
        if (_time == 0) {
            if (status == 0) back();
            if (status == 1) {
                Intent _intent = new Intent().putExtra(MainActivity.PARAM_RESULT, time_counter);
                try {
                    pi.send(MyService.this, MainActivity.STATUS_COUNT, _intent);
                } catch (CanceledException e) {
                    e.printStackTrace();
                }
            }
        }else{
            if (status == 0) someTask();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    void back(){
        Intent intent = new Intent().putExtra(MainActivity.PARAM_RESULT, 0);
        try {
            pi.send(MyService.this, MainActivity.STATUS_NEW, intent);
        } catch (CanceledException e) {
            e.printStackTrace();
        }
        stopSelf();
    }
    void someTask() {
        status = 1;
        new Thread(new Runnable() {
            public void run() {
                try {
                    pi.send(MainActivity.STATUS_START);

                    for (int time_counter = _time; time_counter > 0; time_counter--) {
                        TimeUnit.SECONDS.sleep(1);
                        Intent intent = new Intent().putExtra(MainActivity.PARAM_RESULT, time_counter);
                        pi.send(MyService.this, MainActivity.STATUS_COUNT, intent);
                    }
                    stopSelf();
                    status = 0;
                    Intent intent = new Intent().putExtra(MainActivity.PARAM_RESULT, _time);
                    pi.send(MyService.this, MainActivity.STATUS_FINISH, intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (CanceledException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public class MBinder extends Binder{
        public MyService getService(){
            return MyService.this;
        }
    }

}
