package org.md2k.schedulerrobas.configuration;
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
import android.os.Environment;

import com.google.gson.Gson;

import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.schedulerrobas.exception.ConfigurationFileFormatError;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConfigurationManager {
    private static final String CONFIG_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mCerebrum/org.md2k.schedulerrobas/";
    private static final String CONFIG_FILENAME = "config.json";


    public static Configuration read() {
        try {
            return Storage.readJson(CONFIG_DIRECTORY+CONFIG_FILENAME, Configuration.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static Configuration readMoffitt(Context context) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("config_moffitt.json")));
            return new Gson().fromJson(br, Configuration.class);
        } catch (Exception e) {
            return null;
        }
    }
    public static Configuration readRice(Context context) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("config_rice.json")));
            return new Gson().fromJson(br, Configuration.class);
        } catch (Exception e) {
            return null;
        }
    }
    public static Configuration readROBAS(Context context) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("config_robas.json")));
            return new Gson().fromJson(br, Configuration.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Configuration read(BufferedReader br) throws ConfigurationFileFormatError {
        try {
            return new Gson().fromJson(br, Configuration.class);
        } catch (Exception e) {
            throw new ConfigurationFileFormatError();
        }
    }
}
