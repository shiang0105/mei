
package org.meimen.meimen.utils;

import java.util.Calendar;

public class CalendarUtils {

    public static Calendar getTheFirstDayOfPreviousMonth(Calendar originalDay) {
        Calendar previousDay = (Calendar) originalDay.clone();
        previousDay.roll(Calendar.MONTH, false);
        return previousDay;
    }
}
