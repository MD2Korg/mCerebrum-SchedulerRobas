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
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.source.application.ApplicationBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class get_phone_battery extends Function {
    public get_phone_battery(DataKitManager dataKitManager) {
        super("get_phone_battery",dataKitManager);
    }

    public Expression add(Expression e, ArrayList<String> d) {
        e.addLazyFunction(e.new LazyFunction(name, 0) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                double res = getBattery();
                String st = name+"()=";
                if(res==200){
                    st+="100 [datasource not found]";
                    res=100;
                }else if(res==300){
                    st+="100 [data not found]";
                    res=100;
                }
                else st+=String.format(Locale.getDefault(), "%.2f",res);
                d.add(st);
                return create(res);
            }
        });
        return e;
    }
    private double getBattery(){
        ApplicationBuilder a = new ApplicationBuilder().setId("org.md2k.phonesensor");
        DataSourceBuilder d = new DataSourceBuilder().setType(DataSourceType.BATTERY).setApplication(a.build());
        ArrayList<DataSourceClient> dsc = dataKitManager.find(d.build());
        if(dsc.size()==0) return 200;
        ArrayList<DataType> dt = dataKitManager.query(dsc.get(0), 1);
        if(dt.size()==0) return 300;
        return ((DataTypeDoubleArray) dt.get(0)).getSample()[0];
    }
    public String prepare(String s) {
        return s;
    }
}
