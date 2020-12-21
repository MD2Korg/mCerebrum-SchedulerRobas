package org.md2k.schedulerrobas;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.schedulerrobas.alarm_manager.MyBroadcastReceiver;
import org.md2k.schedulerrobas.condition.ConditionManager;
import org.md2k.schedulerrobas.configuration.Configuration;
import org.md2k.schedulerrobas.configuration.ConfigurationManager;
import org.md2k.schedulerrobas.datakit.DataKitManager;
import org.md2k.schedulerrobas.listen.Callback;
import org.md2k.schedulerrobas.listen.Listen;
import org.md2k.schedulerrobas.logger.MyLogger;
import org.md2k.schedulerrobas.resetapp.ResetCallback;
import org.md2k.schedulerrobas.scheduler.Scheduler;
import org.md2k.schedulerrobas.time.Time;
import org.md2k.schedulerrobas.what.WhatManager;
import org.md2k.schedulerrobas.when.WhenManager;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class ServiceScheduler extends Service {
    PowerManager pm;
    PowerManager.WakeLock wl;
    private static final String TAG = ServiceScheduler.class.getSimpleName();
    private static final long DAYS_IN_MILLIS = 1000L * 60L * 60L * 24;

    private MyLogger logger;
    Context context;
    private String id;
    private Configuration configuration;
    private Scheduler[] schedulers;

    private Listen listen;

    ResetCallback resetCallback=new ResetCallback() {
        @Override
        public void onReset() {
            stopSelf();
        }
    };

    public void onCreate() {
        super.onCreate();
        context = ServiceScheduler.this;
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ServiceScheduler:wakelock");
        wl.acquire();
        cancelAlarm();
        createAlarm();
        subscribe();
    }
    public void cancelAlarm()
    {
        try {
            Intent intent = new Intent(this, MyBroadcastReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
        }catch (Exception ignored){}
    }


    public void createAlarm(){
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long curTime = DateTime.getDateTime();
        long nextTime1230 = Time.getToday()+Time.getTime("12:29:00");
        long nextTime1930 = Time.getToday()+Time.getTime("19:29:00");
        if(curTime>nextTime1230) nextTime1230+=DAYS_IN_MILLIS;
        if(curTime>nextTime1930) nextTime1930+=DAYS_IN_MILLIS;
        if(nextTime1230<nextTime1930){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime1230, pendingIntent);
        }else{
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime1930, pendingIntent);
        }
    }
    Callback callback = new Callback() {
        @Override
        public void onReceive(String id) {
            logger.write("LISTEN", id);
            DataKitManager.getInstance().insertSystemLog("DEBUG","Service/listen",id);
            for (Scheduler scheduler : schedulers) scheduler.restartIfMatch(id);

        }
    };

    void subscribe() {
        if (!Permission.hasPermission(context))
            stopSelf();
        configuration = ConfigurationManager.read();
//        configuration = ConfigurationManager.readROBAS(ServiceScheduler.this);
        if (configuration == null)
            stopSelf();
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("DATAKIT_ERROR"));
        try {
            DataKitAPI.getInstance(this).connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    DataKitManager.getInstance().insertSystemLog("DEBUG","Service","Service starts, datakit connected, configuration="+configuration.getId());

                    createObjects();
                    listen.start();
                    for (Scheduler scheduler : schedulers) scheduler.start();
                }
            });
        } catch (DataKitException e) {
            stopSelf();
        }
    }

    void addListen(String id, Configuration.CListen cListen) {
        if (cListen == null) return;
        for (int i = 0; cListen.getTime() != null && i < cListen.getTime().length; i++)
            listen.add(id, cListen.getTime()[i]);
    }

    void createObjects() {
        schedulers = new Scheduler[configuration.getScheduler_list().length];
        logger = new MyLogger(this);
        listen = new Listen(logger, callback);
        ConditionManager conditionManager = ConditionManager.getInstance();
        for (int i = 0; i < configuration.getScheduler_list().length; i++) {
            String type = configuration.getScheduler_list()[i].getType();
            String id = configuration.getScheduler_list()[i].getId();
            WhenManager whenManager = new WhenManager(type, id, configuration.getScheduler_list()[i].getWhen(), logger);
            WhatManager whatManager = new WhatManager(type, id, this, configuration, configuration.getScheduler_list()[i].getWhat(),  logger);
            schedulers[i] = new Scheduler(type, id, whenManager, whatManager, logger);
            addListen(configuration.getScheduler_list()[i].getId(), configuration.getScheduler_list()[i].getListen());
        }
    }

/*
    void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

    }
*/

    @Override
    public void onDestroy() {
        try {
            wl.release();
        }catch (Exception e){}
        try{
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        }catch (Exception e){}
        DataKitManager.getInstance().insertSystemLog("DEBUG","Service","Service stop");
        DataKitManager.getInstance().disconnect();
        try {
            if (listen != null)
                listen.stop();
        }catch (Exception e){}
        stopForegroundService();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Logger.e("local broadcast receiver ... datakit error");
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
            stopSelf();
        }
    };

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");


        // Create notification default intent.
        Intent intent = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Scheduler app running...");


        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService() {

        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

    }

}
