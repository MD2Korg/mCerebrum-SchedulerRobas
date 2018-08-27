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
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.util.ArrayList;
import java.util.List;

public class is_active extends Function {
    public is_active(DataKitManager dataKitManager) {
        super("is_active", dataKitManager);
    }

    public Expression add(Expression e, ArrayList<String> d) {
        e.addLazyFunction(e.new LazyFunction(name, 2) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                long sTime = lazyParams.get(0).eval().longValue();
                long eTime = lazyParams.get(1).eval().longValue();
                boolean isActive = isActive(sTime, eTime,d);
                if (isActive) return create(1);
                else return create(0);
            }
        });
        return e;
    }

    private boolean isActive(long prevTime, long curTime, ArrayList<String> d) {
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.STRESS_ACTIVITY);
        String ss = DateTime.convertTimeStampToDateTime(prevTime)+", "+DateTime.convertTimeStampToDateTime(curTime);
        ArrayList<DataSourceClient> dataSourceClientArrayList = dataKitManager.find(dataSourceBuilder.build());
        if (dataSourceClientArrayList.size() == 0) {
            d.add(name + "("+ss+")=0 [datasource not found]");
            return false;
        }
        ArrayList<DataType> dataTypes = dataKitManager.query(dataSourceClientArrayList.get(0), prevTime, curTime);
        if (dataTypes.size() == 0) {
            d.add(name + "("+ss+")=0 [data not found]");
            return false;
        }
        double samples = 0;
        for (int i = 0; i < dataTypes.size(); i++) {
            double sample = ((DataTypeDouble) dataTypes.get(i)).getSample();
            if ((int) sample == 0) samples++;
        }
        if (samples / dataTypes.size() >= 0.66) {
            d.add(name + "("+ss+")=0 [person not active]");
            return false;
        } else {
            d.add(name + "("+ss+")=1 [person active]");
            return true;
        }
    }

    public String prepare(String s) {
        return s;
    }
}
