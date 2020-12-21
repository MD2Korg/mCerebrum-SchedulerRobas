package org.md2k.schedulerrobas.what;
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
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.md2k.schedulerrobas.condition.ConditionManager;
import org.md2k.schedulerrobas.configuration.Configuration;
import org.md2k.schedulerrobas.configuration2object.Config2Operation;
import org.md2k.schedulerrobas.datakit.DataKitManager;
import org.md2k.schedulerrobas.exception.ConfigurationFileFormatError;
import org.md2k.schedulerrobas.listen.Callback;
import org.md2k.schedulerrobas.logger.MyLogger;
import org.md2k.schedulerrobas.operation.AbstractOperation;

import java.util.ArrayList;
import java.util.Random;

public class WhatManager {
    private String type;
    private String id;
    private Context context;
    private Configuration configuration;
    private Configuration.CWhat[][] cWhats;
    private MyLogger logger;
    private ArrayList<AbstractOperation> notification;
    private ArrayList<AbstractOperation> application;
    private String command;

    public WhatManager(String type, String id, Context context, Configuration configuration, Configuration.CWhat[][] cWhats, MyLogger logger) {
        this.type = type;
        this.id = id;
        this.context = context;
        this.configuration = configuration;
        this.cWhats = cWhats;
        this.logger = logger;
    }
    public void start(){
        if (cWhats.length == 0) {
            logger.write(type + "/" + id + "/what", "nothing to trigger, failed");
            DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")","failed: nothing to trigger");
            return;
        }
        Configuration.CWhat cWhat = getWhat(cWhats);
        if (cWhat == null){
            logger.write(type + "/" + id + "/what", "condition=false, trigger failed");
            DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")/condition","false: trigger failed");
            return;
        }
        DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")/condition","true: condition=["+cWhat.getCondition()+"]");
        logger.write(type + "/" + id + "/what", "condition=[" + cWhat.getCondition() + "]=true");
        DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")/state","delivered: ["+cWhat.getAction().getTransition()[0][0]+"]");

        logger.write(type + "/" + id + "/what/state[" + cWhat.getAction().getTransition()[0][0] + "]", "delivered");

        DataKitManager.getInstance().insert(type,id,"DELIVERED");
        prepare(cWhat.getAction().getTransition()[0]);
        for(int i = 0;i<notification.size();i++)
            notification.get(i).start();
    }

    private Callback cNotification = new Callback() {
        @Override
        public void onReceive(String res) {
            DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")/notification",res);
            logger.write(type+"/"+id+"/notification",res);
            for(int i = 0;i<notification.size();i++)
                notification.get(i).stop();
            if(res.toUpperCase().equals(command.toUpperCase())){
                DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")/state(EMA)","delivered");
                logger.write(type + "/" + id + "/what/state[ema]", "delivered");
                application.get(0).start();
            }
        }
    };
    Callback cApp = new Callback() {
        @Override
        public void onReceive(String time) {
            DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")/state[EMA]","response="+time);
            logger.write(type + "/" + id + "/what/state[ema]", "response="+time);
            application.get(0).stop();
        }
    };
    private void prepare(String[] transition){
        try {
            notification = Config2Operation.getOperation(type,id, logger, configuration, transition[0], cNotification);
            application = Config2Operation.getOperation(type,id, logger, configuration, transition[2], cApp);
            command = transition[1];
        } catch (ConfigurationFileFormatError configurationFileFormatError) {
            configurationFileFormatError.printStackTrace();
        }
    }

    private Configuration.CWhat getWhat(Configuration.CWhat[][] cWhats) {
        int r = new Random().nextInt(cWhats.length);
        if(cWhats.length>1) {
            logger.write(type + "/" + id + "/what", "random selection=" + String.valueOf(r));
            DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")/condition","random selection ["+String.valueOf(r)+"]");
        }
        for (int i = 0; i < cWhats[r].length; i++)
            if (ConditionManager.getInstance().isTrue(cWhats[r][i].getCondition())) {
                DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/what("+type+" "+id+")/condition","true for ["+String.valueOf(r)+" "+String.valueOf(i)+"]");
                logger.write(type + "/" + id + "/what", "condition=[" + cWhats[r][i].getCondition() + "]=true, what [" + r + "," + i + "]");
                return cWhats[r][i];
            }
        return null;
    }
}
