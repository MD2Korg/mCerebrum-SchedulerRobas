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

import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class time_offset extends Function {
    public time_offset(DataKitManager dataKitManager) {
        super("time_offset",dataKitManager);
    }

    public Expression add(Expression e, ArrayList<String> d) {
        e.addLazyFunction(e.new LazyFunction(name, 1) {
            @Override
            public Expression.LazyNumber lazyEval(final List<Expression.LazyNumber> lazyParams) {
                return new Expression.LazyNumber() {
                    @Override
                    public BigDecimal eval() {
//                        String s = name+"("+lazyParams.get(0).getString()+")=";
                        long res = getValue(lazyParams.get(0).getString());
//                        s+=String.format(Locale.getDefault(), "%d",res);
//                        d.add(s);
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

    private static long getValue(String v){
        String[] s=v.split(":");
        return Long.valueOf(s[0])*60*60*1000+Long.valueOf(s[1])*60*1000+Long.valueOf(s[2])*1000;
    }
    public String prepare(String s){
        Pattern MY_PATTERN = Pattern.compile("\\([0-9]+:[0-9]+:[0-9]+\\)");
        while(true) {
            Matcher m = MY_PATTERN.matcher(s);
            if (m.find()) {
                String p = m.group(0);
                String n=p.charAt(0)+"\""+p.substring(1,p.length()-1)+"\""+p.charAt(p.length()-1);
                s = s.replace(p, n);
            }
            else break;
        }
        return s;
    }
}
