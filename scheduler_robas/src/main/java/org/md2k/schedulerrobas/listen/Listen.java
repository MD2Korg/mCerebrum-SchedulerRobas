package org.md2k.schedulerrobas.listen;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following _condition are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of _condition and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of _condition and the following disclaimer in the documentation
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

import android.os.Handler;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.datakit.DataKitManager;
import org.md2k.schedulerrobas.logger.MyLogger;
import org.md2k.schedulerrobas.time.Time;

import java.util.ArrayList;

public class Listen {
    private static final long DAYS_IN_MILLIS = 24L * 60L * 60L * 1000L;
    private static final long DAYS_IN_SECOND = 24L * 60L * 60L;
    private ArrayList<String> times;
    private ArrayList<String> ids;
    private Handler h;
    private Callback callback;
    private MyLogger logger;

    public Listen(MyLogger logger, Callback callback) {
        times = new ArrayList<>();
        ids = new ArrayList<>();
        this.callback= callback;
        h=new Handler();
        this.logger = logger;
    }

    public void add(String id, String time) {
        for (int i = 0; i < times.size(); i++)
            if (times.get(i).equals(time)) return;
        times.add(time);
        ids.add(id);
    }
    public void start(){
        h.postDelayed(r, getNearestTriggerTime());
    }
    public void stop(){
        h.removeCallbacks(r);
    }
    private String getTime(long time){
        String res=null;
        long min=-1;
        for(int i = 0;i<times.size();i++){
            long t = Time.getToday()+Time.getTime(times.get(i));
            long diff = time - t;
            if(diff<0) diff = -diff;
            if(min==-1 || min>diff){
                min=diff;
                res=times.get(i);
            }
        }
        return res;
    }
    private long getNearestTriggerTime(){
        long res = -1 ;
        long curTime = DateTime.getDateTime();
        for(int i = 0;i<times.size();i++){
            long t=Time.getToday()+Time.getTime(times.get(i));
            while(t<curTime) t+=DAYS_IN_MILLIS;
            if(res==-1 || res>t) res = t;
        }
        DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/listen/next_trigger",DateTime.convertTimeStampToDateTime(res));
        logger.write("listen","Next Trigger Time = "+DateTime.convertTimeStampToDateTime(res));
        return res-DateTime.getDateTime();
    }
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            h.postDelayed(this, getNearestTriggerTime());
            callback.onReceive(findId(getTime(DateTime.getDateTime())));
        }
    };
    private String findId(String time){
        for(int i =0;i<times.size();i++)
            if(times.get(i).equals(time))
                return ids.get(i);
        return ids.get(0);
    }

}
