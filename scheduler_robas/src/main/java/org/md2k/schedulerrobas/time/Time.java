package org.md2k.schedulerrobas.time;
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

import org.md2k.datakitapi.time.DateTime;

import java.util.Calendar;

public class Time {
    private static final String TIME_NOW="NOW";
    private static final String TIME_TODAY="TODAY";
    private static final String SUNDAY="SUNDAY";
    private static final String MONDAY="MONDAY";
    private static final String TUESDAY="TUESDAY";
    private static final String WEDNESDAY="WEDNESDAY";
    private static final String THURSDAY="THURSDAY";
    private static final String FRIDAY="FRIDAY";
    private static final String SATURDAY="SATURDAY";
    public static long getToday(){
        return DateTime.getTodayAtInMilliSecond("00:00:00");
    }
    public static long getTime(String value){
        value=value.trim().toUpperCase();
        if (TIME_NOW.equals(value))
            return DateTime.getDateTime();
        else if (TIME_TODAY.equals(value))
            return DateTime.getTodayAtInMilliSecond("00:00:00");
        else if (value.contains(":") && value.contains("/")) {
            Calendar temp = Calendar.getInstance();
            String[] s = value.split("[/:]");
            temp.set(Calendar.MONTH, Integer.parseInt(s[0]));
            temp.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s[1]));
            temp.set(Calendar.YEAR, Integer.parseInt(s[2]));
            temp.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s[3]));
            temp.set(Calendar.MINUTE, Integer.parseInt(s[4]));
            temp.set(Calendar.SECOND, Integer.parseInt(s[5]));
            temp.set(Calendar.MILLISECOND, 0);
            return temp.getTimeInMillis();

        } else if (value.contains(":")) {
            return DateTime.getTimeInMillis(value);
        } else if (value.contains("/")) {
            Calendar temp = Calendar.getInstance();
            String[] s = value.split("/");
            temp.set(Calendar.MONTH, Integer.parseInt(s[0]));
            temp.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s[1]));
            temp.set(Calendar.YEAR, Integer.parseInt(s[2]));
            temp.set(Calendar.HOUR_OF_DAY, 0);
            temp.set(Calendar.MINUTE, 0);
            temp.set(Calendar.SECOND, 0);
            temp.set(Calendar.MILLISECOND, 0);
            return temp.getTimeInMillis();
        }else if(value.equals(SUNDAY))
            return Calendar.SUNDAY;
        else if(value.equals(MONDAY))
            return Calendar.MONDAY;
        else if(value.equals(TUESDAY))
            return Calendar.TUESDAY;
        else if(value.equals(WEDNESDAY))
            return Calendar.WEDNESDAY;
        else if(value.equals(THURSDAY))
            return Calendar.THURSDAY;
        else if(value.equals(FRIDAY))
            return Calendar.FRIDAY;
        else if(value.equals(SATURDAY))
            return Calendar.SATURDAY;
        return DateTime.getDateTime();
    }

}
