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

import com.google.gson.Gson;
import com.udojava.evalex.Expression;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.core.data_format.privacy.PrivacyData;
import org.md2k.schedulerrobas.datakit.DataKitManager;

import java.util.ArrayList;
import java.util.List;

public class is_privacy_on extends Function {
    public is_privacy_on() {
        super("is_privacy_on");
    }

    public Expression add(Expression e, ArrayList<String> details) {
        e.addLazyFunction(e.new LazyFunction(name, 0) {
            @Override
            public Expression.LazyNumber lazyEval(List<Expression.LazyNumber> lazyParams) {
                boolean b = isValid(details);
                if (b) return create(1);
                else return create(0);
            }
        });
        return e;
    }

    private boolean isValid(ArrayList<String> details) {
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.PRIVACY);
        ArrayList<DataSourceClient> dataSourceClientArrayList = DataKitManager.getInstance().find(dataSourceBuilder.build());
        details.add(name);
        details.add(name+"()");

        if (dataSourceClientArrayList.size() == 0) {
            details.add("0 [datasource not found]");
            return false;
        }
        ArrayList<DataType> dataTypes = DataKitManager.getInstance().query(dataSourceClientArrayList.get(0), 1);
        if (dataTypes.size() == 0) {
            details.add("0 [data not found]");
            return false;
        }
        DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataTypes.get(0);
        Gson gson = new Gson();
        PrivacyData privacyData = gson.fromJson(dataTypeJSONObject.getSample().toString(), PrivacyData.class);
        if (privacyData.isStatus() == false) {
            details.add("0 [privacy status=false]");
            return false;
        }
        if (privacyData.getDuration().getValue() + privacyData.getStartTimeStamp() <= DateTime.getDateTime()) {
            details.add("0 [privacy_time < current_time]");
            return false;
        }
        for (int i = 0; i < privacyData.getPrivacyTypes().size(); i++) {
            if (privacyData.getPrivacyTypes().get(i).getId().equals("ema_intervention")) {
                details.add("1 [ema privacy enabled]");
                return true;
            }
        }
        details.add("0 [privacy is not enabled]");
        return false;
    }

    public String prepare(String s) {
        return s;
    }
}
