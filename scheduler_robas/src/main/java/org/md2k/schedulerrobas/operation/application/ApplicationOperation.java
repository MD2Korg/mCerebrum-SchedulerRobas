package org.md2k.schedulerrobas.operation.application;
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.schedulerrobas.MyApplication;
import org.md2k.schedulerrobas.listen.Callback;
import org.md2k.schedulerrobas.operation.AbstractOperation;
import org.md2k.schedulerrobas.operation.notification.ActivityDialog;

import java.util.HashMap;
import java.util.Map;

public class ApplicationOperation extends AbstractOperation {
    private String type;
    private String id;
    private String packageName;
    private long timeout;
    private HashMap<String, String> parameters;
    private Callback callback;
    private Handler h;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String resType=intent.getStringExtra("TYPE");
            if("RESULT".equals(resType)) {
                String message = intent.getStringExtra("OUTPUT");
//                String message = intent.getStringExtra(ActivityDialog.RESULT);
                Log.d("abc", "message = "+message);
                callback.onReceive(message);
            }
        }
    };


    public ApplicationOperation(String type, String id, String packageName, long timeout, HashMap<String, String> parameters, Callback callback) {
        this.type = type;
        this.id = id;
        this.packageName = packageName;
        this.timeout = timeout;
        this.parameters = parameters;
        h= new Handler();
        this.callback=callback;
    }
    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent=new Intent();
            intent.setAction("org.md2k.scheduler.request");
            intent.putExtra("TYPE","TIMEOUT");
            MyApplication.getContext().sendBroadcast(intent);
            stop();
            callback.onReceive("TIMEOUT");
        }
    };

    @Override
    public void start(){
        MyApplication.getContext().registerReceiver(mMessageReceiver,
                new IntentFilter("org.md2k.scheduler.response"));
        triggerApp();
        h.postDelayed(r, timeout);

    }
    @Override
    public void stop(){
        try {
            MyApplication.getContext().unregisterReceiver(mMessageReceiver);
        }catch (Exception e){}
        try {
            h.removeCallbacks(r);
        }catch (Exception e){}
        try {
            ActivityDialog.fa.finish();
        } catch (Exception e) {
        }

    }


    private void triggerApp() {
        Intent intent = MyApplication.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(parameters!=null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                intent.putExtra(key, value);
            }
        }
        if(id!=null)
        intent.putExtra("type",type+"_"+id);
        else         intent.putExtra("type",type);
        intent.putExtra("id","DATA");
        MyApplication.getContext().startActivity(intent);
    }


}
