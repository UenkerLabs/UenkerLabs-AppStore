package com.uenkerlabs.calendar;

import java.time.LocalDate;
import java.time.YearMonth;

/** Run with javac/java directly; no Android SDK or extra dependencies. */
public final class MonthGridSmokeTest {
    private static void check(boolean ok, String detail) {
        if (!ok) throw new AssertionError(detail);
    }
    public static void main(String[] args) {
        YearMonth november2026 = YearMonth.of(2026, 11);
        check(MonthGrid.firstVisible(november2026).equals(LocalDate.of(2026, 10, 26)), "Monday offset / November 2026");
        check(MonthGrid.weeks(november2026) == 6, "November 2026 is a 6-week month");
        check(MonthGrid.lastVisible(november2026).equals(LocalDate.of(2026, 12, 6)), "November last visible day");
        YearMonth february2021 = YearMonth.of(2021, 2);
        check(MonthGrid.firstVisible(february2021).equals(LocalDate.of(2021, 2, 1)), "February 2021 starts on Monday");
        check(MonthGrid.weeks(february2021) == 4, "Exact 4-week month");
        YearMonth leap2024 = YearMonth.of(2024, 2);
        check(MonthGrid.weeks(leap2024) == 5, "Leap year month");
        check(MonthGrid.weekStart(LocalDate.of(2026, 11, 1)).equals(LocalDate.of(2026, 10, 26)), "Sunday to Monday");
        for (int year = 2020; year < 2036; year++) {
            for (int month = 1; month <= 12; month++) {
                YearMonth current = YearMonth.of(year, month);
                LocalDate first = MonthGrid.firstVisible(current);
                LocalDate last = MonthGrid.lastVisible(current);
                check(first.getDayOfWeek().getValue() == 1, "grid always begins on Monday");
                check(MonthGrid.weeks(current) >= 4 && MonthGrid.weeks(current) <= 6, "month grid size in [4..6]");
                check(!first.isAfter(current.atDay(1)) && !last.isBefore(current.atEndOfMonth()), "all month days visible");
                check(last.getDayOfWeek().getValue() == 7, "grid ends on Sunday");
            }
        }
        System.out.println("PASS: MonthGrid / 192 months, week boundaries, leap years, 4/5/6-week layouts");
    }
}
