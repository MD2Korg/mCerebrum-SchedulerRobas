package org.md2k.schedulerrobas.operation.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.MyApplication;
import org.md2k.schedulerrobas.datakit.DataKitManager;
import org.md2k.schedulerrobas.listen.Callback;
import org.md2k.schedulerrobas.logger.MyLogger;
import org.md2k.schedulerrobas.operation.AbstractOperation;
import org.md2k.schedulerrobas.time.Time;

/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
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
public class PhoneDialog extends AbstractOperation {
    private String title, content;
    private String[] buttons;
    private boolean[] confirm;
    private long at;
    private long interval;
    private String path;
    private MyLogger myLogger;
    private String base;
    private Handler h;
    private Callback callback;
    public PhoneDialog(String path, MyLogger myLogger, String title, String content, String[] buttons, boolean[] confirm, long at, long interval, String base, Callback callback) {
        this.title = title;
        this.content = content;
        this.buttons = buttons;
        this.confirm = confirm;
        this.at = at;
        this.interval = interval;
        this.path = path;
        this.myLogger = myLogger;
        this.base = base;
        this.callback = callback;
        h=new Handler();
    }
    private BroadcastReceiver mMessageReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(ActivityDialog.RESULT);
            DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/notification/dialog","result="+message);
            stop();
            callback.onReceive(message);
        }
    };

    public void start(){
        long delay = getDelay();
        if(delay !=-1) {
            h.postDelayed(r, getDelay());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).registerReceiver(mMessageReceiver,
                    new IntentFilter(ActivityDialog.INTENT_RESULT));
            showDialog(MyApplication.getContext());
        }
    }
    public void stop(){
        try {
            LocalBroadcastManager.getInstance(MyApplication.getContext()).unregisterReceiver(mMessageReceiver);
        }catch (Exception e){}
        try {
            h.removeCallbacks(r);
        }catch (Exception e){}
        try {
            ActivityDialog.fa.finish();
        } catch (Exception e) {
        }
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            stop();
            callback.onReceive("MISSED");
        }
    };
    private long getDelay(){
        long waitTime = Time.getToday() + Time.getTime(base) + interval;
        long curTime = DateTime.getDateTime();
        if(curTime>waitTime) return -1;
        else return waitTime - curTime;
    }

    private void showDialog(Context context) {
        Intent intent = new Intent(context, ActivityDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ActivityDialog.TITLE, title);
        intent.putExtra(ActivityDialog.CONTENT, content);
        intent.putExtra(ActivityDialog.BUTTONS, buttons);
        intent.putExtra(ActivityDialog.CONFIRM, confirm);
        context.startActivity(intent);
    }
}