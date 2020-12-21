package org.md2k.schedulerrobas.condition.function;
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

import com.udojava.evalex.Expression;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;
import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class get_data_quality extends Function {
    public get_data_quality() {
        super("get_data_quality");
    }

    public Expression add(Expression e, ArrayList<String> details) {
        e.addLazyFunction(e.new LazyFunction(name, -1) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                details.add(name);
                //todo: check data quality code
                double good =-1;
                String s=name+"(";
                DataSourceBuilder db = createDataSource(lazyParams,2);
                long sTime = lazyParams.get(0).eval().longValue();
                long eTime = lazyParams.get(1).eval().longValue();
                sTime=sTime-sTime%(1000*60);
                eTime = eTime-eTime%(1000*60);
                s+= DateTime.convertTimeStampToDateTime(sTime)+", "+ DateTime.convertTimeStampToDateTime(eTime);
                for(int i=2;i<lazyParams.size();i++)
                    s+=","+lazyParams.get(i).getString();
                details.add(s+")");
                ArrayList<DataSourceClient> dd = DataKitManager.getInstance().find(db.build());
                if(dd.size()==0){
                    details.add("0 [datasource not found]");
                }else {
                    String st="";
                    for (int i = 0; i < dd.size(); i++) {
                        ArrayList<DataType> dataTypes = DataKitManager.getInstance().query(dd.get(i), sTime, eTime);
                        double g = getDataQuality(dataTypes, sTime, eTime);
                        if (g > good) {
                            st=getDataQualityString(dataTypes, sTime, eTime);
                            good = g;
                        }
                    }
                    details.add(st);
                }
                return create(good);
            }
        });
        return e;
    }
    private String getDataQualityString(ArrayList<DataType> dataTypes, long sTime, long eTime){
        long minute = (eTime - sTime)/(1000*60);
        long goodMinute = 0;
        for(int i=0;i<dataTypes.size();i++){
            int[] res=((DataTypeIntArray)(dataTypes.get(i))).getSample();
            if(res[DATA_QUALITY.GOOD]>30) goodMinute++;
        }
        double good = ((goodMinute*100.0)/minute);
        String st=String.format(Locale.getDefault(), "%.2f",good)+"[total minute="+String.valueOf(minute)+" sample="+String.valueOf(dataTypes.size()+" good="+String.valueOf(goodMinute)+"]");
        return st;
    }
    private double getDataQuality(ArrayList<DataType> dataTypes, long sTime, long eTime){
        long minute = (eTime - sTime)/(1000*60);
        long goodMinute = 0;
        for(int i=0;i<dataTypes.size();i++){
            int[] res=((DataTypeIntArray)(dataTypes.get(i))).getSample();
            if(res[DATA_QUALITY.GOOD]>30) goodMinute++;
        }
        return ((goodMinute*100.0)/minute);
    }

    public String prepare(String s) {
        return s;
    }
}
