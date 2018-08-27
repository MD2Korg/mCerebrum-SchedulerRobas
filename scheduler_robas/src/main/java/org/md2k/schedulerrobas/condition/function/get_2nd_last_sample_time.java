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
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class get_2nd_last_sample_time extends Function {
    public get_2nd_last_sample_time(DataKitManager dataKitManager) {
        super("get_2nd_last_sample_time",dataKitManager);
    }

    public Expression add(Expression e, ArrayList<String> d) {
        e.addLazyFunction(e.new LazyFunction(name, -1) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                String s=name+"(";
                for(int i=0;i<lazyParams.size();i++) {
                    if(i!=0) s+=",";
                    s += lazyParams.get(i).getString();
                }
                s+=")=";
                DataSourceBuilder db = createDataSource(lazyParams,0);
                ArrayList<DataSourceClient> dd = dataKitManager.find(db.build());
                if(dd.size()==0){
                    s+=String.valueOf(-1)+" [datasource not found]";
                    d.add(s);return create(-1);
                }else {
                    ArrayList<Long> list=new ArrayList<>();
                    for (int i = 0; i < dd.size(); i++) {
                        ArrayList<DataType> dataTypes = dataKitManager.query(dd.get(i), 2);
                        for(int j=0;j<dataTypes.size();j++)
                            list.add(dataTypes.get(j).getDateTime());
                    }
                    Collections.sort(list);
                    long time;
                    if(list.size()<=1){
                        time=-1;
                        s+=String.valueOf(time)+" [data not found]";
                        d.add(s);
                    }else {
                        time = list.get(list.size()-2);
                        s+=String.valueOf(time)+" [time="+DateTime.convertTimeStampToDateTime(time)+"]";
                        d.add(s);
                    }
                    return create(time);
                }
            }
        });
        return e;
    }

    public String prepare(String s) {
        return s;
    }
}
