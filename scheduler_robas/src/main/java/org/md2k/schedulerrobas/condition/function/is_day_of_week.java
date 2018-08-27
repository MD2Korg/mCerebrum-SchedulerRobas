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

import org.md2k.datakitapi.time.DateTime;
import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class is_day_of_week extends Function {
    public is_day_of_week(DataKitManager dataKitManager) {
        super("is_day_of_week",dataKitManager);
    }

    public Expression add(Expression e, ArrayList<String> d) {
        e.addLazyFunction(e.new LazyFunction(name, 1) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                return new Expression.LazyNumber() {
                    @Override
                    public BigDecimal eval() {
                        int res = 0;
                        Calendar c = Calendar.getInstance();
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        String dd = lazyParams.get(0).getString();
                        if(dayOfWeek==getDayNumber(dd))
                            res=1;
                        String s = name+"("+dd+")="+String.valueOf(res)+" [today:"+ DateTime.convertTimeStampToDateTime(c.getTimeInMillis(),"EEE, MMM d, yyyy");
                        d.add(s);
                        return new BigDecimal(res);
                    }

                    @Override
                    public String getString() {
                        return null;
                    }
                };
            }
        });
        return e;
    }
    private int getDayNumber(String s){
        switch(s){
            case "SUNDAY": return Calendar.SUNDAY;
            case "MONDAY": return Calendar.MONDAY;
            case "TUESDAY": return Calendar.TUESDAY;
            case "WEDNESDAY": return Calendar.WEDNESDAY;
            case "THURSDAY": return Calendar.THURSDAY;
            case "FRIDAY": return Calendar.FRIDAY;
            case "SATURDAY": return Calendar.SATURDAY;
            default:return -1;
        }
    }
    public String prepare(String str){
        return str;
    }
}
