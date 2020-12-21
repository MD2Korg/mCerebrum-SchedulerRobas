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
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.util.ArrayList;
import java.util.List;

public class get_study_week extends Function {
    public get_study_week() {
        super("get_study_week");
    }

    public Expression add(Expression e, ArrayList<String> details) {
        e.addLazyFunction(e.new LazyFunction(name, 0) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                long v=Long.MIN_VALUE;
                details.add(name);
                details.add(name+"()");
                DataSourceBuilder ddd = new DataSourceBuilder().setType("STUDY").setId("START");
                ArrayList<DataSourceClient> dd = DataKitManager.getInstance().find(ddd.build());
                if(dd.size()==0){
                    details.add(String.valueOf(Long.MIN_VALUE)+" [datasource not found]");
                    return create(Long.MIN_VALUE);
                }else {
                    for (int i = 0; i < dd.size(); i++) {
                        ArrayList<DataType> dataTypes = DataKitManager.getInstance().query(dd.get(i), 1);
                        if (dataTypes.size() == 0) continue;
                        long studyStartTime = ((DataTypeLong) (dataTypes.get(0))).getSample();
                        long curTime = DateTime.getDateTime();
                        v = (curTime - studyStartTime) / (1000 * 60 * 60 * 24 * 7) + 1;
                        details.add(String.valueOf(v));
                        return create(v);
                    }
                    details.add(String.valueOf(Long.MIN_VALUE)+" [data not found]");
                    return create(v);
                }
            }
        });
        return e;
    }
    public String prepare(String s) {
        return s;
    }

}
