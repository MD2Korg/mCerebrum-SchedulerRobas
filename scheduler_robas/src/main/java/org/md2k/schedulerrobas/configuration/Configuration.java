package org.md2k.schedulerrobas.configuration;
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


import org.md2k.datakitapi.source.datasource.DataSource;

import java.util.HashMap;

public class Configuration  extends AbstractConfig{
    private CSchedulerList[] scheduler_list;
    private CNotificationDetails[] notification_details;
    private CNotificationList[] notification_list;
    private CApplicationList[] application_list;
    private CIncentiveList[] incentive_list;

    public CSchedulerList[] getScheduler_list() {
        return scheduler_list;
    }

    public CNotificationDetails[] getNotification_details() {
        return notification_details;
    }

    public CNotificationList[] getNotification_list() {
        return notification_list;
    }

    public CApplicationList[] getApplication_list() {
        return application_list;
    }

    public CIncentiveList[] getIncentive_list() {
        return incentive_list;
    }

    public class CSchedulerList extends AbstractConfig{
        private CListen listen;
        private CWhen[] when;
        private CWhat[][] what;

        public CListen getListen() {
            return listen;
        }

        public CWhen[] getWhen() {
            return when;
        }

        public CWhat[][] getWhat() {
            return what;
        }
    }
    public class CNotificationList extends AbstractConfig{
        private CNotification[] notification;

        public CNotification[] getNotification() {
            return notification;
        }
    }
    public class CNotification{
        private String condition;
        private String[] notification_details_id;

        public String getCondition() {
            return condition;
        }

        public String[] getNotification_details_id() {
            return notification_details_id;
        }
    }

    public class CNotificationDetails extends AbstractConfig{
        private String format;
        private int repeat;
        private String interval;
        private String base;
        private String[] at;
        private CNotiPhMessage message;

        public String getFormat() {
            return format;
        }

        public int getRepeat() {
            return repeat;
        }

        public String getInterval() {
            return interval;
        }

        public String[] getAt() {
            return at;
        }

        public String getBase(){ return base;}
        public CNotiPhMessage getMessage() {
            return message;
        }

        public class CNotiPhMessage{
            private String title;
            private String content;
            private String[] choice;
            private CNotiPhButton[] button;

            public String getTitle() {
                return title;
            }

            public String getContent() {
                return content;
            }

            public String[] getChoice() {
                return choice;
            }

            public CNotiPhButton[] getButtons() {
                return button;
            }
        }
        public class CNotiPhButton{
            private String title;
            private boolean confirm;

            public String getTitle() {
                return title;
            }

            public boolean isConfirm() {
                return confirm;
            }
        }

    }
    public class CListen {
        private DataSource[] datasource;
        private String[] time;

        public DataSource[] getDatasource() {
            return datasource;
        }

        public String[] getTime() {
            return time;
        }
    }

    public static class CWhen {
        private String condition;
        private String start_time;
        private String end_time;
        private CTriggerRule[] trigger_rule;

        public CWhen(String condition, String start_time, String end_time, CTriggerRule[] trigger_rule) {
            this.condition = condition;
            this.start_time = start_time;
            this.end_time = end_time;
            this.trigger_rule = trigger_rule;
        }

        public String getCondition() {
            return condition;
        }

        public String getStart_time() {
            return start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public CTriggerRule[] getTrigger_rule() {
            return trigger_rule;
        }
    }
    public class CWhat{
        private String condition;
        private CAction action;

        public String getCondition() {
            return condition;
        }

        public CAction getAction() {
            return action;
        }
    }
    public class CAction{
        private String[][] transition;

        public String[][] getTransition() {
            return transition;
        }
    }
    public static class CTriggerRule{
        private String trigger_time;
        private String condition;
        private String retry_after;

        public CTriggerRule(String trigger_time, String condition, String retry_after) {
            this.trigger_time = trigger_time;
            this.condition = condition;
            this.retry_after = retry_after;
        }

        public String getTrigger_time() {
            return trigger_time;
        }

        public String getCondition() {
            return condition;
        }

        public String getRetry_after() {
            return retry_after;
        }
    }



    public class CApplicationList extends AbstractConfig{
        private CApplication[] application;

        public CApplication[] getApplication() {
            return application;
        }
    }
    public class CApplication{
        private String condition;
        private String package_name;
        private String timeout;
        private HashMap<String, String> parameter;

        public String getCondition() {return condition;
        }

        public String getPackage_name() {
            return package_name;
        }

        public String getTimeout() {
            return timeout;
        }

        public HashMap<String, String> getParameter() {
            return parameter;
        }

    }

    public class CIncentiveList extends  AbstractConfig{
        private CIncentive[] incentive;

        public CIncentive[] getIncentive() {
            return incentive;
        }
    }
    public class CIncentive{
        private String condition;
        private double amount;
        private String[] message;

        public String getCondition() {
            return condition;
        }

        public double getAmount() {
            return amount;
        }

        public String[] getMessage() {
            return message;
        }
    }


}
