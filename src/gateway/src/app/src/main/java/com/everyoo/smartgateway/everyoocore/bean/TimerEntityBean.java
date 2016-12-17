package com.everyoo.smartgateway.everyoocore.bean;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class TimerEntityBean implements Serializable {

    public int year, month, day, hour, minute, second;
    private static TimerEntityBean entity;

    private TimerEntityBean() {

    }

    public static TimerEntityBean instance(Calendar calendar) {
        if (entity == null) {
            entity = new TimerEntityBean();
        }
        entity.year = calendar.get(Calendar.YEAR);
        entity.month = calendar.get(Calendar.MONTH) + 1;
        entity.day = calendar.get(Calendar.DAY_OF_MONTH);
        entity.hour = calendar.get(Calendar.HOUR_OF_DAY);
        entity.minute = calendar.get(Calendar.MINUTE);
        entity.second = calendar.get(Calendar.SECOND);
        return entity;
    }

}
