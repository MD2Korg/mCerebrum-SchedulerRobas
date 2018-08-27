package org.md2k.schedulerrobas.operation.notification;
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

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.MyApplication;
import org.md2k.schedulerrobas.logger.MyLogger;
import org.md2k.schedulerrobas.operation.AbstractOperation;
import org.md2k.schedulerrobas.time.Time;

public class PhoneVibrate extends AbstractOperation {
    private long repeat;
    private long interval;
    private Long[] at;
    private String path;
    private MyLogger myLogger;
    private String base;
    private Handler hAt;
    private Handler hInterval;
    private long repeatNow;

    public PhoneVibrate(String path, MyLogger myLogger, long repeat, long interval, Long[] at, String base) {
        this.repeat = repeat;
        this.interval = interval;
        this.at = at;
        this.path = path;
        this.myLogger=myLogger;
        this.base = base;
        hAt = new Handler();
        hInterval = new Handler();
    }
    @Override
    public void start(){
        int validIndex = -1;
        repeatNow=0;
//        load(format);
        long curTime = DateTime.getDateTime();
        for(int i =0;i<at.length;i++) {
            long trigTime = at[i] + Time.getToday() + Time.getTime(base);
            if(trigTime>curTime) {validIndex=i;break;}
        }
        if(validIndex>=at.length) return;
        if(validIndex==-1) return;
        if(validIndex!=0) hAt.postDelayed(rAt, 1000);
        for(int i=validIndex;i<at.length;i++){
            long trigTime = at[i] + Time.getToday() + Time.getTime(base);
            hAt.postDelayed(rAt, trigTime-DateTime.getDateTime());
        }
/*

        Log.d("abc","Phone vibrate start..");
        repeatNow=0;
        long next = getNextAt();
        if(next==-1) return;
        hAt.postDelayed(rAt, next);

*/
    }
    @Override
    public void stop(){
        try {
            hAt.removeCallbacks(rAt);
            hInterval.removeCallbacks(rInterval);
        }catch (Exception e){}
    }
    private Runnable rAt = new Runnable() {
        @Override
        public void run() {
            hInterval.removeCallbacks(rInterval);
            repeatNow=0;
            hInterval.post(rInterval);
/*
            long time = getNextAt();
            if(time!=-1)
            hAt.postDelayed(this, getNextAt());
*/

        }
    };
    private Runnable rInterval=new Runnable() {
        @Override
        public void run() {
            vibrate();
            repeatNow++;
            if(repeatNow<repeat)
                hInterval.postDelayed(this, interval);
        }
    };
/*
    long getNextAt(){
        long curTime = DateTime.getDateTime();
        for(int i =0;i<at.length;i++) {
            long trigTime = at[i] + Time.getToday() + Time.getTime(base);
            if(trigTime>curTime) return trigTime-curTime;
            else if(trigTime+15*1000>curTime) return 1000;
        }
        return -1;
    }
*/

    private void vibrate() {
        Vibrator vibrator;
        vibrator = (Vibrator) MyApplication.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.vibrate(300);
    }
}
