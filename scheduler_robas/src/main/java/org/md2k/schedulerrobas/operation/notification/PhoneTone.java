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

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.MyApplication;
import org.md2k.schedulerrobas.logger.MyLogger;
import org.md2k.schedulerrobas.operation.AbstractOperation;
import org.md2k.schedulerrobas.time.Time;

import java.io.IOException;

public class PhoneTone extends AbstractOperation {
    private String format;
    private long repeat;
    private long interval;
    private Long[] at;
    private MediaPlayer mPlayer;
    private String path;
    private MyLogger logger;
    private String base;

    private Handler hAt;
    private Handler hInterval;
    private long repeatNow;
    private AssetFileDescriptor descriptor;

    public PhoneTone(String path, MyLogger myLogger, String format, long repeat, long interval, Long[] at, String base) {
        this.format = format;
        this.repeat = repeat;
        this.interval = interval;
        this.at = at;
        this.path = path;
        this.logger = myLogger;
        this.base = base;
        mPlayer = new MediaPlayer();
        hAt = new Handler();
        hInterval = new Handler();
    }

    @Override
    public void start() {
        try {
            descriptor = MyApplication.getContext().getAssets().openFd("tone.mp3");
        } catch (IOException e) {

        }
        int validIndex = -1;
        repeatNow = 0;
//        load(format);
        long curTime = DateTime.getDateTime();
        for (int i = 0; i < at.length; i++) {
            long trigTime = at[i] + Time.getToday() + Time.getTime(base);
            if (trigTime > curTime) {
                validIndex = i;
                break;
            }
        }
        if (validIndex >= at.length) return;
        if(validIndex==-1) return;
        if (validIndex != 0) hAt.postDelayed(rAt, 1000);
        for (int i = validIndex; i < at.length; i++) {
            long trigTime = at[i] + Time.getToday() + Time.getTime(base);
            hAt.postDelayed(rAt, trigTime - DateTime.getDateTime());
        }

/*
        long next = getNextAt();
        if(next==-1) return;
        hAt.postDelayed(rAt, next);
*/

    }

    @Override
    public void stop() {
        try{
            descriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            hAt.removeCallbacks(rAt);
            hInterval.removeCallbacks(rInterval);
        }catch (Exception e){}
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
            }
        } catch (Exception ignored) {
//            Logger.e("PhoneTone..stop()...failed" + "exception=" + ignored.getMessage());
            Log.e("abc", "PhoneTone..stop()...failed" + "exception=" + ignored.getMessage());
        }

    }

    private Runnable rAt = new Runnable() {
        @Override
        public void run() {
            hInterval.removeCallbacks(rInterval);
            repeatNow = 0;
            hInterval.post(rInterval);
/*
            long time = getNextAt();
            if(time!=-1)
                hAt.postDelayed(this, getNextAt());
*/

        }
    };
    private Runnable rInterval = new Runnable() {
        @Override
        public void run() {
            play();
            repeatNow++;
            if (repeatNow < repeat)
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

    /*
        private void load(String filename) {
            Log.d("abc","phonetone ... load..");
            mPlayer = new MediaPlayer();
            try {
                Uri myUri = Uri.parse(filename);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(MyApplication.getContext(), myUri);
                mPlayer.prepare();
            } catch (Exception e1) {
                try {
                    AssetFileDescriptor afd = MyApplication.getContext().getAssets().openFd("tone.mp3");
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    mPlayer.prepare();
                } catch (Exception ignored) {
                    Log.e("abc", "PhoneTone..play()..assetload()..failed e="+ignored.toString());
                }
            }
        }
    */
    private void play() {
        try {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.release();
            }
            mPlayer = new MediaPlayer();

            mPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());

            mPlayer.prepare();
//            mPlayer.setVolume(1f, 1f);
            mPlayer.start();
            logger.write(path + "/tone", "play");
            Logger.d(path + "/tone", "play");
            Log.d("abc", "phonetone play...");

        } catch (Exception e) {
            Logger.e("PhoneTone..play()..start()..failed e=" + e.toString());
            Log.e("abc", "PhoneTone..play()..start()..failed");
        }
    }

/*
    private void play() {
        try {
            if(mPlayer.isPlaying()) mPlayer.stop();
            mPlayer.prepare();
            logger.write(path + "/tone", "play");
            Logger.d(path + "/tone", "play");
            Log.d("abc", "phonetone play...");
            mPlayer.start();
        } catch (Exception e) {
            Logger.e("PhoneTone..play()..start()..failed e="+e.toString());
            Log.e("abc", "PhoneTone..play()..start()..failed");
        }
    }
*/
}
