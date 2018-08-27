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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeString;
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
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class DataKitManager {

    public DataKitManager() {
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

    public Observable<Data> subscribe(DataSource dataSource) {
        Log.d("aaa", "subscribe datasource=" + dataSource.getType()+" "+dataSource.getId());
        return Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                try {
                    DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
                    ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
                    if (dataSourceClients.size() == 0) throw new DataSourceNotFound();
                    for (int i = 0; i < dataSourceClients.size(); i++) {
                        int finalI = i;
                        dataKitAPI.subscribe(dataSourceClients.get(i), dataType -> {
                            Data data = new Data(dataSourceClients.get(finalI), dataType);
                            subscriber.onNext(data);
                        });
                        ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(i), 1);
                        if (dataTypes.size() != 0 && DateTime.getDateTime()-dataTypes.get(0).getDateTime()<5000) {
                            Data data = new Data(dataSourceClients.get(i), dataTypes.get(0));
                            subscriber.onNext(data);
                        }

                        Log.d("abc", "subscribed=" + dataSourceClients.get(i).getDataSource().getType());
                    }
                } catch (DataKitException e) {
                    Logger.e("DataKit subscribe error: "+e.getMessage()+" datasource="+dataSource.getType()+" "+dataSource.getId());
                    subscriber.onError(new DataKitAccessError());
                } catch (DataSourceNotFound dataSourceNotFound) {
                    subscriber.onError(dataSourceNotFound);
                }
            }
        }).retryWhen(observable -> observable.flatMap(new Func1<Throwable, Observable<?>>() {
            @Override
            public Observable<?> call(Throwable throwable) {
                if (throwable instanceof DataSourceNotFound)
                    return Observable.timer(1000, TimeUnit.MILLISECONDS);
                else {
                    Logger.e("DataKit subscribe error(stopped): "+throwable.getMessage()+" datasource="+dataSource.getType()+" "+dataSource.getId());
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
                    return Observable.error(throwable);
                }
            }
        })).doOnUnsubscribe(() -> {
            unsubscribe(dataSource);
        })
                .doOnCompleted(() -> unsubscribe(dataSource))
                .doOnError(throwable -> unsubscribe(dataSource));
    }
    public void disconnect(){
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            if(dataKitAPI!=null) {
                try {
                    dataKitAPI.disconnect();
                }catch (Exception e){}
            }
    }

    private void unsubscribe(DataSource dataSource) {
        Logger.e("DataKit unsubscribe: datasource="+dataSource.getType()+" "+dataSource.getId());
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
            for (int i = 0; i < dataSourceClients.size(); i++) {
                dataKitAPI.unsubscribe(dataSourceClients.get(i));

            }
        } catch (DataKitException ignored) {
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

    public void unregister(DataSource dataSource) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            ArrayList<DataSourceClient> dataSourceClients = find(dataSource);
            for (int i = 0; i < dataSourceClients.size(); i++)
                dataKitAPI.unregister(dataSourceClients.get(i));
        } catch (DataKitException ignored) {

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

    public double insertIncentive(double amount) {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            DataSourceBuilder db = new DataSourceBuilder().setType(DataSourceType.INCENTIVE);
            double total = queryTotalIncentive();
            double[] data = new double[]{amount, total + amount};
            DataSourceClient dsc = dataKitAPI.register(db);
            dataKitAPI.insert(dsc, new DataTypeDoubleArray(DateTime.getDateTime(), data));
            return total+amount;
        } catch (Exception e) {
            Logger.e("DataKit insert incentive error");
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
        return amount;
    }

    public double queryTotalIncentive() {
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            DataSourceBuilder db = new DataSourceBuilder().setType(DataSourceType.INCENTIVE);
            ArrayList<DataSourceClient> dsc = dataKitAPI.find(db);
            if (dsc.size() == 0) return 0;
            ArrayList<DataType> dt = dataKitAPI.query(dsc.get(0), 1);
            if (dt.size() == 0) return 0;
            DataTypeDoubleArray d = (DataTypeDoubleArray) dt.get(0);
            return d.getSample()[1];
        } catch (DataKitException e) {
            Logger.e("DataKit incentive query error: "+e.getMessage());
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent("DATAKIT_ERROR"));
        }
        return 0;
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
}
