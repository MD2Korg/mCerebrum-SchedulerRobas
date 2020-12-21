package org.md2k.schedulerrobas.datakit;
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
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.datatype.DataTypeStringArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.MyApplication;
import org.md2k.schedulerrobas.exception.DataKitAccessError;
import org.md2k.schedulerrobas.exception.DataSourceNotFound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class DataKitManager {
    private static DataKitManager instance;
    private HashMap<String, DataSourceClient> dataSourceClients;

    public static DataKitManager getInstance() {
        if (instance == null) instance = new DataKitManager();
        return instance;
    }
    private DataKitManager() {
        dataSourceClients = new HashMap<>();
    }

/*
    public ArrayList<Data> getSample(DataSource dataSource, int sampleNo) throws DataKitAccessError, DataSourceNotFound {
        ArrayList<Data> data = new ArrayList<>();
        try {
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(dataSourceBuilder);
            if (dataSourceClients.size() == 0) {
                throw new DataSourceNotFound();
            } else {
                for (int i = 0; i < dataSourceClients.size(); i++) {
                    ArrayList<DataType> dataTypes1 = dataKitAPI.query(dataSourceClients.get(i), sampleNo);
                    for (int j = 0; j < dataTypes1.size(); j++)
                        data.add(new Data(dataSourceClients.get(i), dataTypes1.get(j)));
                }
                return data;
            }
        } catch (DataKitException e) {
            throw new DataKitAccessError();
        } catch (DataSourceNotFound dataSourceNotFound) {
            throw new DataSourceNotFound();
        }
    }

    public ArrayList<Data> getSample(DataSource dataSource, long startTime, long endTime) throws DataKitAccessError, DataSourceNotFound {
        ArrayList<Data> data = new ArrayList<>();
        try {
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(dataSource);
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(dataSourceBuilder);
            if (dataSourceClients.size() == 0) {
                throw new DataSourceNotFound();
            } else {
                for (int i = 0; i < dataSourceClients.size(); i++) {
                    ArrayList<DataType> dataTypes1 = dataKitAPI.query(dataSourceClients.get(i), startTime, endTime);
                    for (int j = 0; j < dataTypes1.size(); j++)
                        data.add(new Data(dataSourceClients.get(i), dataTypes1.get(j)));
                }
                return data;
            }
        } catch (DataKitException e) {
            throw new DataKitAccessError();
        } catch (DataSourceNotFound dataSourceNotFound) {
            throw new DataSourceNotFound();
        }
    }
*/

    public ArrayList<DataSourceClient> find(final DataSource dataSource) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            return dataKitAPI.find(new DataSourceBuilder(dataSource));
        } catch (DataKitException e) {
            Logger.e("DataKit find error: "+e.getMessage()+" datasource="+dataSource.getType()+" "+dataSource.getId());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
        return new ArrayList<>();
    }

    public void disconnect(){
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            if(dataKitAPI!=null) {
                try {
                    dataKitAPI.disconnect();
                }catch (Exception e){}
            }
    }

    public Observable<Boolean> connect(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                try {
                    DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
                    dataKitAPI.connect(() -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    });

                } catch (Exception e) {
                    Logger.e("DataKit connect error: "+e.getMessage());
                    subscriber.onError(e);
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
                }
            }
        });
    }

    public DataSourceClient register(DataSource dataSource) throws DataKitAccessError {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            return dataKitAPI.register(new DataSourceBuilder(dataSource));
        } catch (DataKitException e) {
            Logger.e("datakit register  error="+e.toString()+" datasource="+dataSource.getType()+" "+dataSource.getId());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
            throw new DataKitAccessError();
        }
    }

    public void insert(DataSourceClient dataSourceClient, DataType dataType) throws DataKitAccessError {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            dataKitAPI.insert(dataSourceClient, dataType);
        } catch (DataKitException e) {
            Logger.e( "datakit insert error="+e.toString()+" datasource="+dataSourceClient.getDataSource().getType()+" "+dataSourceClient.getDataSource().getId());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
            throw new DataKitAccessError();
        }
    }

    public ArrayList<DataType> query(DataSourceClient dataSourceClient, int i) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            return dataKitAPI.query(dataSourceClient, i);
        } catch (DataKitException e) {
            Logger.e("DataKit query error: "+e.getMessage()+" datasource="+dataSourceClient.getDataSource().getType()+" "+dataSourceClient.getDataSource().getId());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
            return new ArrayList<DataType>();
        }
    }

    public ArrayList<DataType> query(DataSourceClient dataSourceClient, long sTime, long eTime) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            return dataKitAPI.query(dataSourceClient, sTime, eTime);
        } catch (DataKitException e) {
            Logger.e("DataKit query error: "+e.getMessage()+" datasource="+dataSourceClient.getDataSource().getType()+" "+dataSourceClient.getDataSource().getId());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
            return new ArrayList<DataType>();
        }
    }

    public void insert(String type, String id, String message) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(type).setId(id);
            DataSourceClient d = dataKitAPI.register(dataSourceBuilder);
            dataKitAPI.insert(d, new DataTypeString(DateTime.getDateTime(), message));
        } catch (Exception e) {
            Logger.e("DataKit insert error: "+e.getMessage()+" datasource="+type+" "+id);
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
    }
    public void insertSystemLog(String type, String path, String message) {
        Log.d("system_log", type + " -> " + path + " -> " + message);
        Logger.d(path+","+message);
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            if (dataSourceClients.get("SYSTEM_LOG") == null) {
                DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType("SYSTEM_LOG");
                DataSourceClient d = dataKitAPI.register(dataSourceBuilder);
                dataSourceClients.put("SYSTEM_LOG", d);
            }
            dataKitAPI.insert(dataSourceClients.get("SYSTEM_LOG"), new DataTypeStringArray(DateTime.getDateTime(), new String[]{DateTime.convertTimeStampToDateTime(DateTime.getDateTime()), type, path.replace(",", ";"), message.replace(",", ";")}));
        } catch (Exception e) {
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
    }

}
