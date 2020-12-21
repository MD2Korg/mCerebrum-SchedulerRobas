package org.md2k.schedulerrobas.configuration2object;
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

import org.md2k.schedulerrobas.State;
import org.md2k.schedulerrobas.condition.ConditionManager;
import org.md2k.schedulerrobas.configuration.Configuration;
import org.md2k.schedulerrobas.datakit.DataKitManager;
import org.md2k.schedulerrobas.exception.ConfigurationFileFormatError;
import org.md2k.schedulerrobas.listen.Callback;
import org.md2k.schedulerrobas.logger.MyLogger;
import org.md2k.schedulerrobas.operation.AbstractOperation;

import java.util.ArrayList;

import rx.Observable;

public class Config2Operation {
    public static ArrayList<AbstractOperation> getOperation(String _type, String _id, MyLogger logger, Configuration configuration, String id, Callback callback) throws ConfigurationFileFormatError {
        ArrayList<AbstractOperation> abstractOperations=new ArrayList<>();
        AbstractOperation o = Config2Application.getObject(_type, _id, configuration.getApplication_list(), id, callback);
        if(o!=null){
            abstractOperations.add(o);
            return abstractOperations;
        }
            abstractOperations = Config2Notification.getObject(_type,_id, logger, configuration.getNotification_list(), configuration.getNotification_details(), id, callback);
        return abstractOperations;
    }

}
