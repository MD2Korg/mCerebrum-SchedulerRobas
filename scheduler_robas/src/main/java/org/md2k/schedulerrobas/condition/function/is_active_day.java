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
import org.md2k.schedulerrobas.time.Time;

import java.util.ArrayList;
import java.util.List;

public class is_active_day extends Function {
    public is_active_day() {
        super("is_active_day");
    }

    public Expression add(Expression e, ArrayList<String> details) {
        e.addLazyFunction(e.new LazyFunction(name, 0) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                details.add(name);
                details.add(name+"()");

                long dayStart=getDay("START");
                long dayEnd = getDay("END");
                long now = DateTime.getDateTime();
                long today = Time.getToday();
                if(dayStart==-1 || now<dayStart || dayStart<today) {
                    details.add("false [day is not started]");
                    return create(0);
                }
                if(dayStart<dayEnd) {
                    details.add("false [day is ended]");
                    return create(0);
                }
                details.add("true [daystart="+DateTime.convertTimeStampToDateTime(dayStart)+", now="+DateTime.convertTimeStampToDateTime(now));
                return create(1);
            }
        });
        return e;
    }
    private long getDay(String id){
        DataSourceBuilder d = new DataSourceBuilder().setType("DAY").setId(id);
        ArrayList<DataSourceClient> dsc = DataKitManager.getInstance().find(d.build());
        if(dsc.size()==0) return -1;
        ArrayList<DataType> dt = DataKitManager.getInstance().query(dsc.get(0), 1);
        if(dt.size()==0) return -1;
        return ((DataTypeLong) dt.get(0)).getSample();
    }
    public String prepare(String s) {
        return s;
    }
}
