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
import org.md2k.datakitapi.datatype.DataTypeDouble;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeFloat;
import org.md2k.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.datatype.DataTypeLongArray;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class get_last_sample extends Function {
    public get_last_sample(DataKitManager dataKitManager) {
        super("get_last_sample",dataKitManager);
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

                DataSourceBuilder db = createDataSource(lazyParams,1);
                ArrayList<DataSourceClient> dd = dataKitManager.find(db.build());
                if(dd.size()==0){
                    s+=String.valueOf(Long.MIN_VALUE)+" [datasource not found]";
                    d.add(s);return create(Long.MIN_VALUE);
                }else {
                    for (int i = 0; i < dd.size(); i++) {
                        ArrayList<DataType> dataTypes = dataKitManager.query(dd.get(i), 1);
                        if (dataTypes.size() == 0) continue;
                        double curValue = getValue(dataTypes.get(0), lazyParams.get(0).eval().intValue());
                        s+=String.format(Locale.getDefault(), "%.2f",curValue);
                        d.add(s);
                        return create(curValue);
                    }
                }
                s+=String.valueOf(Long.MIN_VALUE)+" [data not found]";
                d.add(s);return create(Long.MIN_VALUE);
            }
        });
        return e;
    }

    private double getValue(DataType dataType, int index) {
        if (dataType instanceof DataTypeInt)
            return ((DataTypeInt) dataType).getSample();
        else if (dataType instanceof DataTypeIntArray)
            return ((DataTypeIntArray) dataType).getSample()[index];
        else if (dataType instanceof DataTypeLong)
            return ((DataTypeLong) dataType).getSample();
        else if (dataType instanceof DataTypeLongArray)
            return ((DataTypeLongArray) dataType).getSample()[index];
        if (dataType instanceof DataTypeFloat)
            return ((DataTypeFloat) dataType).getSample();
        else if (dataType instanceof DataTypeFloatArray)
            return ((DataTypeFloatArray) dataType).getSample()[index];
        if (dataType instanceof DataTypeDouble)
            return ((DataTypeDouble) dataType).getSample();
        else if (dataType instanceof DataTypeDoubleArray)
            return ((DataTypeDoubleArray) dataType).getSample()[index];
        else return Long.MIN_VALUE;
    }
    public String prepare(String s) {
        return s;
    }
}
