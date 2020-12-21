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

import org.md2k.datakitapi.source.application.ApplicationBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platformapp.PlatformAppBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class Function {
    String name;
    Function(String name){
        this.name = name;
    }
    public abstract String prepare(String s);
    public abstract Expression add(Expression e, ArrayList<String> details);
    Expression.LazyNumber create(final int v){
        return new Expression.LazyNumber() {
            @Override
            public BigDecimal eval() {
                return new BigDecimal(v);
            }

            @Override
            public String getString() {
                return null;
            }
        };
    }
    Expression.LazyNumber create(final long v){
        return new Expression.LazyNumber() {
            @Override
            public BigDecimal eval() {
                return new BigDecimal(v);
            }

            @Override
            public String getString() {
                return null;
            }
        };
    }
    Expression.LazyNumber create(final double v){
        return new Expression.LazyNumber() {
            @Override
            public BigDecimal eval() {
                return new BigDecimal(v);
            }

            @Override
            public String getString() {
                return null;
            }
        };
    }
    DataSourceBuilder createDataSource(List<Expression.LazyNumber> lazyParams,int si) {
        DataSourceBuilder d = new DataSourceBuilder();
        if (lazyParams.size() > si && isValid(lazyParams.get(si).getString()))
            d = d.setType(lazyParams.get(si).getString());
        if (lazyParams.size() > si+1 && isValid(lazyParams.get(si+1).getString()))
            d = d.setId(lazyParams.get(si+1).getString());
        if (lazyParams.size() > si+2) {
            PlatformBuilder p = new PlatformBuilder();
            boolean flag = false;
            if (lazyParams.size() > si+2 && isValid(lazyParams.get(si+2).getString())) {
                p = p.setType(lazyParams.get(si+2).getString());
                flag = true;
            }
            if (lazyParams.size() > si+3 && isValid(lazyParams.get(si+3).getString())) {
                p = p.setId(lazyParams.get(si+3).getString());
                flag = true;
            }
            if (flag)
                d = d.setPlatform(p.build());
        }
        if (lazyParams.size() > si+4) {
            PlatformAppBuilder p = new PlatformAppBuilder();
            boolean flag = false;
            if (lazyParams.size() > si+4 && isValid(lazyParams.get(si+4).getString())) {
                p = p.setType(lazyParams.get(si+4).getString());
                flag = true;
            }
            if (lazyParams.size() >si+5 && isValid(lazyParams.get(si+5).getString())) {
                p = p.setId(lazyParams.get(si+5).getString());
                flag = true;
            }
            if (flag)
                d = d.setPlatformApp(p.build());
        }
        if (lazyParams.size() >si+6) {
            ApplicationBuilder p = new ApplicationBuilder();
            boolean flag = false;
            if (lazyParams.size() >si+6 && isValid(lazyParams.get(si+6).getString())) {
                p = p.setType(lazyParams.get(si+6).getString());
                flag = true;
            }
            if (lazyParams.size() >si+7 && isValid(lazyParams.get(si+7).getString())) {
                p = p.setId(lazyParams.get(si+7).getString());
                flag = true;
            }
            if (flag)
                d = d.setApplication(p.build());
        }
        return d;
    }
    private boolean isValid(String e) {
        if (e == null) return false;
        if (e.length() == 0) return false;
        if (e.equalsIgnoreCase("NULL")) return false;
        return true;
    }
}
