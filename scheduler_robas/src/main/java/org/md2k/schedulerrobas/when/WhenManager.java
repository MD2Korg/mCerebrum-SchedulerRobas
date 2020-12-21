package org.md2k.schedulerrobas.when;
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

import com.orhanobut.logger.Logger;

import org.md2k.schedulerrobas.condition.ConditionManager;
import org.md2k.schedulerrobas.configuration.Configuration;
import org.md2k.schedulerrobas.datakit.DataKitManager;
import org.md2k.schedulerrobas.logger.MyLogger;

import java.util.ArrayList;

public class WhenManager {
    private String type, id;
    private Configuration.CWhen[] cWhenList;
    //    private int[] tries;
    private MyLogger logger;

    public WhenManager(String type, String id, Configuration.CWhen[] cWhenList, MyLogger logger) {
        this.type = type;
        this.id = id;
        this.cWhenList = cWhenList;
        this.logger = logger;
    }

    public boolean start() {
        ArrayList<String> debug = new ArrayList<>();
        boolean condition = ConditionManager.getInstance().isTrue(cWhenList[0].getCondition(), debug);
        String s = "";
        for (int i = 0; i < debug.size(); i++)
            s += debug.get(i) + ";";
        logger.write(type + "/" + id + "/when[" + Integer.toString(0) + "]/condition[" + condition + "]", s);
        DataKitManager.getInstance().insertSystemLog("DEBUG", "Service/when("+type+" "+id+")/condition",String.valueOf(condition)+" ["+s+"]");
        return condition;
    }

}
