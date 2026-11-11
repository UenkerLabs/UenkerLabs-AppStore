package com.uenkerlabs.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

/** Pure date arithmetic for a Monday-first month grid, independent of Android. */
public final class MonthGrid {
    private MonthGrid() { }

    public static LocalDate weekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate firstVisible(YearMonth month) {
        return weekStart(month.atDay(1));
    }

    public static int weeks(YearMonth month) {
        int offset = month.atDay(1).getDayOfWeek().getValue() - 1;
        return (offset + month.lengthOfMonth() + 6) / 7;
    }

    public static LocalDate lastVisible(YearMonth month) {
        return firstVisible(month).plusDays((long) weeks(month) * 7 - 1);
    }
}
