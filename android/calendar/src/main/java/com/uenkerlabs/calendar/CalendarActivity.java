package com.uenkerlabs.calendar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** UenkerLabs calendar beta: Google Calendar-inspired navigation with original Android UI. */
public final class CalendarActivity extends Activity {
    private static final int INK = 0xff202534;
    private static final int SUBTLE = 0xff65738b;
    private static final int BLUE = 0xff315dcc;
    private static final int BACKGROUND = 0xfff6f8fc;
    private static final int LIGHT_BORDER = 0xffe7eaf0;
    private static final int READ_REQUEST = 41;
    private static final int WRITE_REQUEST = 42;

    private enum Mode { MONTH, WEEK, DAY, AGENDA }
    private final Locale language = Locale.GERMAN;
    private final DateTimeFormatter headingFormat = DateTimeFormatter.ofPattern("LLLL yyyy", language);
    private final DateTimeFormatter fullDateFormat = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", language);
    private final DateTimeFormatter compactDateFormat = DateTimeFormatter.ofPattern("EEE, d. MMM", language);
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm", language);
    private final ExecutorService queryWorker = Executors.newSingleThreadExecutor();
    private final HashMap<LocalDate, ArrayList<EventItem>> visibleEvents = new HashMap<>();
    private final ArrayList<CalendarSource> calendarSources = new ArrayList<>();
    private LocalDate selected = LocalDate.now();
    private Mode mode = Mode.MONTH;
    private LocalDate loadedFrom;
    private LocalDate loadedUntil;
    private int querySerial;
    private boolean loading;
    private String errorMessage;
    private FrameLayout frame;
    private LinearLayout mainLayout;
    private final ContentObserver updates = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override public void onChange(boolean selfChange) {
            if (allowed(Manifest.permission.READ_CALENDAR)) refreshData();
        }
    };
    private boolean observing;

    private static final class CalendarSource {
        final long id;
        final String title;
        final int color;
        final boolean writable;
        CalendarSource(long id, String title, int color, boolean writable) {
            this.id = id;
            this.title = title;
            this.color = color;
            this.writable = writable;
        }
    }

    private static final class EventItem {
        final long eventId;
        final long calendarId;
        final String title;
        final long begin;
        final long end;
        final boolean allDay;
        final int color;
        final String source;
        EventItem(long eventId, long calendarId, String title, long begin, long end,
                  boolean allDay, int color, String source) {
            this.eventId = eventId;
            this.calendarId = calendarId;
            this.title = title == null || title.trim().isEmpty() ? "(Ohne Titel)" : title;
            this.begin = begin;
            this.end = end;
            this.allDay = allDay;
            this.color = color;
            this.source = source;
        }
    }

    private static final class LoadResult {
        final HashMap<LocalDate, ArrayList<EventItem>> byDay = new HashMap<>();
        final ArrayList<CalendarSource> sources = new ArrayList<>();
        String failure;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                selected = LocalDate.parse(savedInstanceState.getString("selected", LocalDate.now().toString()));
                mode = Mode.valueOf(savedInstanceState.getString("mode", "MONTH"));
            } catch (RuntimeException ignored) { selected = LocalDate.now(); mode = Mode.MONTH; }
        }
        frame = new FrameLayout(this);
        frame.setBackgroundColor(BACKGROUND);
        setContentView(frame);
        render();
        if (!allowed(Manifest.permission.READ_CALENDAR)) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, READ_REQUEST);
        } else {
            refreshData();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (allowed(Manifest.permission.READ_CALENDAR)) {
            if (!observing) {
                try {
                    getContentResolver().registerContentObserver(CalendarContract.Events.CONTENT_URI, true, updates);
                    getContentResolver().registerContentObserver(CalendarContract.Calendars.CONTENT_URI, true, updates);
                    observing = true;
                } catch (SecurityException ignored) { observing = false; }
            }
            refreshData();
        }
    }

    @Override protected void onPause() {
        if (observing) { getContentResolver().unregisterContentObserver(updates); observing = false; }
        super.onPause();
    }

    @Override protected void onDestroy() {
        querySerial++;
        queryWorker.shutdownNow();
        super.onDestroy();
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        out.putString("selected", selected.toString());
        out.putString("mode", mode.name());
        super.onSaveInstanceState(out);
    }

    @Override public void onRequestPermissionsResult(int code, String[] permissions, int[] grants) {
        super.onRequestPermissionsResult(code, permissions, grants);
        if (code == READ_REQUEST) refreshData();
        if (code == WRITE_REQUEST) {
            if (allowed(Manifest.permission.WRITE_CALENDAR)) createEvent();
            else Toast.makeText(this, "Zum Erstellen ist der Kalender-Schreibzugriff nötig.", Toast.LENGTH_LONG).show();
        }
    }

    private int dp(float n) { return Math.round(n * getResources().getDisplayMetrics().density); }
    private boolean allowed(String name) { return checkSelfPermission(name) == PackageManager.PERMISSION_GRANTED; }
    private GradientDrawable background(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke != Color.TRANSPARENT) d.setStroke(dp(1), stroke);
        return d;
    }
    private TextView text(String value, float size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setGravity(Gravity.CENTER_VERTICAL);
        return v;
    }
    private TextView control(String value, String spoken, Runnable action) {
        TextView v = text(value, 19, INK, false);
        v.setGravity(Gravity.CENTER);
        v.setMinWidth(dp(43));
        v.setMinHeight(dp(44));
        v.setContentDescription(spoken);
        v.setBackground(background(Color.WHITE, 16, LIGHT_BORDER));
        v.setOnClickListener(view -> action.run());
        return v;
    }

    private void render() {
        if (frame == null) return;
        frame.removeAllViews();
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        frame.addView(mainLayout, new FrameLayout.LayoutParams(-1, -1));
        drawToolbar();

        SwipeBody scroll = new SwipeBody();
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        mainLayout.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(13), dp(11), dp(13), dp(100));
        scroll.addView(body);

        if (!allowed(Manifest.permission.READ_CALENDAR)) {
            showMissingPermission(body);
        } else {
            if (mode == Mode.MONTH) drawMonth(body);
            else if (mode == Mode.WEEK) drawWeek(body);
            else if (mode == Mode.DAY) drawDay(body);
            else drawAgenda(body);
            if (errorMessage != null) message(body, "Kalenderdaten können nicht gelesen werden: " + errorMessage);
            else if (loading) message(body, "Termine werden geladen …");
        }
        TextView add = text("+", 32, Color.WHITE, false);
        add.setGravity(Gravity.CENTER);
        add.setContentDescription("Neuen Termin erstellen");
        add.setBackground(background(BLUE, 18, Color.TRANSPARENT));
        add.setElevation(dp(6));
        FrameLayout.LayoutParams floating = new FrameLayout.LayoutParams(dp(62), dp(62), Gravity.BOTTOM | Gravity.RIGHT);
        floating.setMargins(0, 0, dp(20), dp(22));
        frame.addView(add, floating);
        add.setOnClickListener(v -> createEvent());
    }

    private void drawToolbar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setBackgroundColor(Color.WHITE);
        bar.setPadding(dp(14), dp(10), dp(14), dp(10));
        mainLayout.addView(bar, new LinearLayout.LayoutParams(-1, dp(69)));

        TextView title = text(toolbarTitle(), 20, INK, true);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setContentDescription("Angezeigter Zeitraum: " + toolbarTitle() + ". Tippen, um Datum auszuwählen.");
        title.setOnClickListener(v -> chooseDate());
        bar.addView(title, new LinearLayout.LayoutParams(0, -1, 1));

        TextView today = text("Heute", 13, BLUE, true);
        today.setGravity(Gravity.CENTER);
        today.setPadding(dp(8), 0, dp(8), 0);
        today.setMinHeight(dp(44));
        today.setContentDescription("Zum heutigen Datum springen");
        today.setOnClickListener(v -> { selected = LocalDate.now(); refreshData(); });
        bar.addView(today);

        bar.addView(control("‹", "Vorheriger Zeitraum", () -> move(-1)));
        bar.addView(control("›", "Nächster Zeitraum", () -> move(1)));
        TextView menu = control("☰", "Kalenderansicht auswählen", () -> { });
        bar.addView(menu);
        menu.setOnClickListener(v -> showModeMenu(menu));
    }

    private String toolbarTitle() {
        if (mode == Mode.DAY) return selected.format(compactDateFormat);
        if (mode == Mode.WEEK) return "Woche · " + selected.format(DateTimeFormatter.ofPattern("MMM yyyy", language));
        if (mode == Mode.AGENDA) return "Terminübersicht";
        String month = selected.format(headingFormat);
        return Character.toUpperCase(month.charAt(0)) + month.substring(1);
    }

    private void showModeMenu(View anchor) {
        PopupMenu menu = new PopupMenu(this, anchor);
        menu.getMenu().add(0, 1, 0, "Monat");
        menu.getMenu().add(0, 2, 1, "Woche");
        menu.getMenu().add(0, 3, 2, "Tag");
        menu.getMenu().add(0, 4, 3, "Terminübersicht");
        menu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) mode = Mode.MONTH;
            else if (item.getItemId() == 2) mode = Mode.WEEK;
            else if (item.getItemId() == 3) mode = Mode.DAY;
            else mode = Mode.AGENDA;
            refreshData();
            return true;
        });
        menu.show();
    }

    private void chooseDate() {
        new DatePickerDialog(this, (picker, year, month, day) -> {
            selected = LocalDate.of(year, month + 1, day);
            refreshData();
        }, selected.getYear(), selected.getMonthValue() - 1, selected.getDayOfMonth()).show();
    }

    private void move(int direction) {
        if (mode == Mode.MONTH) selected = selected.plusMonths(direction);
        else if (mode == Mode.WEEK) selected = selected.plusWeeks(direction);
        else if (mode == Mode.AGENDA) selected = selected.plusDays((long) 21 * direction);
        else selected = selected.plusDays(direction);
        refreshData();
    }

    private void showMissingPermission(LinearLayout body) {
        TextView title = text("Deine Termine im Blick", 22, INK, true);
        title.setPadding(dp(12), dp(25), dp(12), dp(12));
        body.addView(title);
        message(body, "UenkerLabs Kalender zeigt Termine aus den bereits eingerichteten Android-Kalendern. Dafür benötigt die App Lesezugriff. Es wird kein eigener Cloud-Dienst verwendet.");
        TextView grant = text("Kalenderzugriff erlauben", 16, Color.WHITE, true);
        grant.setGravity(Gravity.CENTER);
        grant.setBackground(background(BLUE, 14, Color.TRANSPARENT));
        grant.setMinHeight(dp(56));
        body.addView(grant);
        grant.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, READ_REQUEST));
    }

    private void drawMonth(LinearLayout parent) {
        YearMonth current = YearMonth.from(selected);
        LinearLayout weekdays = new LinearLayout(this);
        weekdays.setPadding(0, dp(4), 0, dp(7));
        for (String name : new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"}) {
            TextView day = text(name, 12, SUBTLE, true);
            day.setGravity(Gravity.CENTER);
            weekdays.addView(day, new LinearLayout.LayoutParams(0, dp(34), 1));
        }
        parent.addView(weekdays);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(7);
        grid.setRowCount(MonthGrid.weeks(current));
        grid.setBackground(background(Color.WHITE, 12, LIGHT_BORDER));
        grid.setPadding(dp(2), dp(6), dp(2), dp(6));
        LocalDate start = MonthGrid.firstVisible(current);
        for (int row = 0; row < MonthGrid.weeks(current); row++) {
            for (int col = 0; col < 7; col++) {
                final LocalDate date = start.plusDays((long) row * 7 + col);
                LinearLayout cell = new LinearLayout(this);
                cell.setOrientation(LinearLayout.VERTICAL);
                cell.setPadding(dp(2), dp(3), dp(2), dp(4));
                boolean activeMonth = YearMonth.from(date).equals(current);
                if (date.equals(selected)) cell.setBackground(background(0xffe8efff, 9, Color.TRANSPARENT));
                TextView number = text(String.valueOf(date.getDayOfMonth()), 13, activeMonth ? INK : 0xffaab2bf, date.equals(LocalDate.now()));
                number.setGravity(Gravity.CENTER);
                if (date.equals(LocalDate.now())) {
                    number.setTextColor(Color.WHITE);
                    number.setBackground(background(BLUE, 30, Color.TRANSPARENT));
                }
                cell.addView(number, new LinearLayout.LayoutParams(-1, dp(26)));
                List<EventItem> items = eventsOn(date);
                int shown = Math.min(items.size(), 2);
                for (int i = 0; i < shown; i++) {
                    EventItem item = items.get(i);
                    TextView chip = text(item.title, 9, activeMonth ? INK : SUBTLE, false);
                    chip.setMaxLines(1);
                    chip.setEllipsize(TextUtils.TruncateAt.END);
                    chip.setBackground(background(tint(item.color, 0.79f), 5, Color.TRANSPARENT));
                    chip.setPadding(dp(2), 0, dp(1), 0);
                    LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, dp(18));
                    cp.setMargins(0, dp(2), 0, 0);
                    cell.addView(chip, cp);
                    chip.setOnClickListener(v -> showEvent(item));
                }
                if (items.size() > shown) {
                    TextView more = text("+" + (items.size() - shown), 9, SUBTLE, true);
                    cell.addView(more);
                }
                cell.setContentDescription(date.format(fullDateFormat) + ", " + items.size() + " Termine");
                cell.setOnClickListener(v -> { selected = date; showDateWithoutExtraQuery(); });
                GridLayout.LayoutParams gp = new GridLayout.LayoutParams(
                    GridLayout.spec(row), GridLayout.spec(col, 1, 1f));
                gp.width = 0;
                gp.height = dp(87);
                gp.setMargins(dp(1), dp(1), dp(1), dp(1));
                grid.addView(cell, gp);
            }
        }
        parent.addView(grid, new LinearLayout.LayoutParams(-1, -2));
        addSelectedDaySection(parent);
    }

    private void drawWeek(LinearLayout parent) {
        LocalDate first = MonthGrid.weekStart(selected);
        for (int n = 0; n < 7; n++) {
            LocalDate day = first.plusDays(n);
            addDaySection(parent, day, true);
        }
    }

    private void drawDay(LinearLayout parent) { addDaySection(parent, selected, true); }

    private void drawAgenda(LinearLayout parent) {
        message(parent, "Termine ab " + selected.format(compactDateFormat) + " – nächste 21 Tage");
        int total = 0;
        for (int n = 0; n < 21; n++) {
            LocalDate date = selected.plusDays(n);
            List<EventItem> items = eventsOn(date);
            if (items.isEmpty()) continue;
            addDaySection(parent, date, false);
            total += items.size();
        }
        if (!loading && total == 0) message(parent, "Keine Termine im angezeigten Zeitraum.");
    }

    private void addSelectedDaySection(LinearLayout parent) {
        View divider = new View(this);
        divider.setBackgroundColor(LIGHT_BORDER);
        LinearLayout.LayoutParams dp1 = new LinearLayout.LayoutParams(-1, dp(1));
        dp1.setMargins(0, dp(18), 0, dp(8));
        parent.addView(divider, dp1);
        addDaySection(parent, selected, true);
    }

    private void addDaySection(LinearLayout parent, LocalDate date, boolean showEmpty) {
        TextView day = text(date.format(fullDateFormat), 17, INK, true);
        day.setPadding(dp(4), dp(14), dp(3), dp(13));
        day.setOnClickListener(v -> { selected = date; mode = Mode.DAY; refreshData(); });
        parent.addView(day);
        List<EventItem> items = eventsOn(date);
        if (items.isEmpty() && showEmpty && !loading) message(parent, "Keine Termine an diesem Tag.");
        for (EventItem item : items) addEventRow(parent, item);
    }

    private void addEventRow(LinearLayout parent, EventItem item) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackground(background(Color.WHITE, 12, LIGHT_BORDER));
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
        rp.setMargins(0, 0, 0, dp(8));
        parent.addView(row, rp);
        View stripe = new View(this);
        stripe.setBackground(background(item.color, 6, Color.TRANSPARENT));
        LinearLayout.LayoutParams stripeLp = new LinearLayout.LayoutParams(dp(5), dp(56));
        stripeLp.setMargins(dp(9), dp(7), 0, dp(7));
        row.addView(stripe, stripeLp);
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(12), dp(9), dp(9), dp(9));
        row.addView(info, new LinearLayout.LayoutParams(0, -2, 1));
        TextView title = text(item.title, 15, INK, true);
        title.setMaxLines(2);
        title.setEllipsize(TextUtils.TruncateAt.END);
        info.addView(title);
        TextView secondary = text(timeLabel(item) + " · " + item.source, 12, SUBTLE, false);
        secondary.setSingleLine(true);
        secondary.setEllipsize(TextUtils.TruncateAt.END);
        info.addView(secondary);
        row.setOnClickListener(v -> showEvent(item));
        row.setContentDescription(item.title + ", " + timeLabel(item) + ", " + item.source);
    }

    private List<EventItem> eventsOn(LocalDate date) {
        ArrayList<EventItem> list = visibleEvents.get(date);
        return list == null ? Collections.emptyList() : list;
    }

    private void message(LinearLayout parent, String value) {
        TextView note = text(value, 13, SUBTLE, false);
        note.setPadding(dp(10), dp(12), dp(10), dp(12));
        parent.addView(note);
    }

    private int tint(int c, float white) {
        int r = Math.round(Color.red(c) * (1-white) + 255 * white);
        int g = Math.round(Color.green(c) * (1-white) + 255 * white);
        int b = Math.round(Color.blue(c) * (1-white) + 255 * white);
        return Color.rgb(r, g, b);
    }

    private String timeLabel(EventItem item) {
        if (item.allDay) return "Ganztägig";
        return Instant.ofEpochMilli(item.begin).atZone(ZoneId.systemDefault()).format(timeFormat);
    }

    private void showEvent(EventItem item) {
        String start = item.allDay ? "Ganztägig" : timeLabel(item);
        String description = item.title + "\n\n" + start + "\n" + item.source;
        new AlertDialog.Builder(this)
            .setTitle("Termindetails")
            .setMessage(description)
            .setNegativeButton("Schließen", null)
            .setNeutralButton("Öffnen", (d, which) -> openSystemEvent(item, Intent.ACTION_VIEW))
            .setPositiveButton("Bearbeiten", (d, which) -> openSystemEvent(item, Intent.ACTION_EDIT))
            .show();
    }

    private void openSystemEvent(EventItem item, String action) {
        Intent intent = new Intent(action);
        intent.setData(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, item.eventId));
        try { startActivity(intent); }
        catch (ActivityNotFoundException | SecurityException ex) {
            new AlertDialog.Builder(this).setMessage("Auf diesem Gerät ist keine passende Kalender-Anwendung verfügbar.")
                .setPositiveButton("OK", null).show();
        }
    }

    private void showDateWithoutExtraQuery() {
        LocalDate[] range = wantedRange();
        if (loadedFrom != null && !selected.isBefore(loadedFrom) && selected.isBefore(loadedUntil)
            && !range[0].isBefore(loadedFrom) && !range[1].isAfter(loadedUntil)) {
            render();
        } else refreshData();
    }

    private LocalDate[] wantedRange() {
        if (mode == Mode.MONTH) {
            YearMonth month = YearMonth.from(selected);
            return new LocalDate[]{ MonthGrid.firstVisible(month), MonthGrid.lastVisible(month).plusDays(1) };
        }
        if (mode == Mode.WEEK) {
            LocalDate monday = MonthGrid.weekStart(selected);
            return new LocalDate[]{ monday, monday.plusDays(7) };
        }
        if (mode == Mode.AGENDA) return new LocalDate[]{ selected, selected.plusDays(21) };
        return new LocalDate[]{ selected, selected.plusDays(1) };
    }

    private void refreshData() {
        querySerial++;
        final int request = querySerial;
        visibleEvents.clear();
        calendarSources.clear();
        loadedFrom = null;
        loadedUntil = null;
        loading = false;
        errorMessage = null;
        if (!allowed(Manifest.permission.READ_CALENDAR)) { render(); return; }
        final LocalDate[] range = wantedRange();
        loading = true;
        render();
        queryWorker.execute(() -> {
            LoadResult result = queryCalendar(range[0], range[1]);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed() || request != querySerial) return;
                loading = false;
                errorMessage = result.failure;
                visibleEvents.clear();
                visibleEvents.putAll(result.byDay);
                calendarSources.clear();
                calendarSources.addAll(result.sources);
                loadedFrom = range[0];
                loadedUntil = range[1];
                render();
            });
        });
    }

    private LoadResult queryCalendar(LocalDate fromDate, LocalDate untilDate) {
        LoadResult result = new LoadResult();
        HashMap<Long, CalendarSource> known = new HashMap<>();
        try (Cursor sources = getContentResolver().query(
            CalendarContract.Calendars.CONTENT_URI,
            new String[]{ CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.VISIBLE, CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CALENDAR_COLOR }, null, null, null)) {
            if (sources != null) while (sources.moveToNext()) {
                if (sources.getInt(2) != 1) continue;
                long id = sources.getLong(0);
                CalendarSource source = new CalendarSource(id,
                    sources.getString(1) == null ? "Kalender" : sources.getString(1),
                    sources.isNull(4) ? BLUE : (sources.getInt(4) | 0xff000000),
                    sources.getInt(3) >= CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR);
                known.put(id, source);
                result.sources.add(source);
            }
        } catch (Exception ex) {
            result.failure = ex.getClass().getSimpleName();
            return result;
        }
        ZoneId zone = ZoneId.systemDefault();
        long fromMillis = fromDate.atStartOfDay(zone).toInstant().toEpochMilli();
        long untilMillis = untilDate.atStartOfDay(zone).toInstant().toEpochMilli();
        android.net.Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, fromMillis);
        ContentUris.appendId(builder, untilMillis);
        try (Cursor instances = getContentResolver().query(builder.build(),
            new String[]{ CalendarContract.Instances.EVENT_ID, CalendarContract.Instances.TITLE,
                CalendarContract.Instances.BEGIN, CalendarContract.Instances.END,
                CalendarContract.Instances.CALENDAR_ID, CalendarContract.Instances.ALL_DAY },
            null, null, CalendarContract.Instances.BEGIN + " ASC")) {
            if (instances != null) while (instances.moveToNext()) {
                long calId = instances.getLong(4);
                CalendarSource src = known.get(calId);
                if (src == null) continue;
                long begin = instances.getLong(2), end = instances.getLong(3);
                boolean allDay = instances.getInt(5) != 0;
                ZoneId eventZone = allDay ? ZoneOffset.UTC : zone;
                LocalDate firstDay = Instant.ofEpochMilli(begin).atZone(eventZone).toLocalDate();
                LocalDate lastDay = Instant.ofEpochMilli(Math.max(begin, end - 1)).atZone(eventZone).toLocalDate();
                if (firstDay.isBefore(fromDate)) firstDay = fromDate;
                if (!lastDay.isBefore(untilDate)) lastDay = untilDate.minusDays(1);
                if (firstDay.isAfter(lastDay)) continue;
                EventItem event = new EventItem(instances.getLong(0), calId, instances.getString(1),
                    begin, end, allDay, src.color, src.title);
                for (LocalDate day = firstDay; !day.isAfter(lastDay); day = day.plusDays(1)) {
                    result.byDay.computeIfAbsent(day, ignored -> new ArrayList<>()).add(event);
                }
            }
        } catch (Exception ex) { result.failure = ex.getClass().getSimpleName(); }
        Comparator<EventItem> order = Comparator.comparing((EventItem e) -> !e.allDay).thenComparingLong(e -> e.begin);
        for (List<EventItem> entries : result.byDay.values()) entries.sort(order);
        return result;
    }

    private void createEvent() {
        if (!allowed(Manifest.permission.WRITE_CALENDAR)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, WRITE_REQUEST);
            return;
        }
        if (calendarSources.isEmpty()) { refreshData(); Toast.makeText(this, "Kalender werden geladen. Bitte erneut versuchen.", Toast.LENGTH_SHORT).show(); return; }
        ArrayList<CalendarSource> writable = new ArrayList<>();
        for (CalendarSource source : calendarSources) if (source.writable) writable.add(source);
        if (writable.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Kein beschreibbarer Kalender")
                .setMessage("Bitte prüfe in Android, ob ein synchronisierter Kalender zum Bearbeiten eingerichtet ist.")
                .setPositiveButton("OK", null).show();
            return;
        }
        String[] names = new String[writable.size()];
        for (int i = 0; i < writable.size(); i++) names[i] = writable.get(i).title;
        new AlertDialog.Builder(this).setTitle("Kalender für den Termin wählen")
            .setItems(names, (dialog, which) -> promptTitle(writable.get(which))).show();
    }

    private void promptTitle(CalendarSource target) {
        EditText title = new EditText(this);
        title.setSingleLine(true);
        title.setHint("Titel des Termins");
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setPadding(dp(20), dp(4), dp(20), dp(3));
        wrapper.addView(title, new LinearLayout.LayoutParams(-1, -2));
        new AlertDialog.Builder(this)
            .setTitle("Neuer Termin · " + selected.format(compactDateFormat))
            .setView(wrapper)
            .setNegativeButton("Abbrechen", null)
            .setPositiveButton("Weiter", (dialog, id) -> {
                String name = title.getText().toString().trim();
                if (name.isEmpty()) { Toast.makeText(this, "Bitte einen Titel eingeben.", Toast.LENGTH_SHORT).show(); return; }
                LocalTime now = LocalTime.now();
                new TimePickerDialog(this, (picker, hour, minute) -> saveEvent(target, name, hour, minute),
                    now.getHour(), now.getMinute(), true).show();
            }).show();
    }

    private void saveEvent(CalendarSource target, String title, int hour, int minute) {
        ZonedDateTime start = selected.atTime(hour, minute).atZone(ZoneId.systemDefault());
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, target.id);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DTSTART, start.toInstant().toEpochMilli());
        values.put(CalendarContract.Events.DTEND, start.plusHours(1).toInstant().toEpochMilli());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().getId());
        try {
            android.net.Uri saved = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
            if (saved == null) throw new IllegalStateException("calendar provider did not return an event URI");
            Toast.makeText(this, "Termin gespeichert", Toast.LENGTH_LONG).show();
            refreshData();
        } catch (Exception ex) {
            new AlertDialog.Builder(this).setTitle("Termin nicht gespeichert")
                .setMessage("Der Android-Kalender hat den Termin nicht übernommen (" + ex.getClass().getSimpleName() + ").")
                .setPositiveButton("OK", null).show();
        }
    }

    /** Horizontal swipe changes calendar page; vertical scrolling and normal taps keep working. */
    private final class SwipeBody extends ScrollView {
        private float downX, downY;
        SwipeBody() { super(CalendarActivity.this); }
        @Override public boolean dispatchTouchEvent(MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                downX = event.getX();
                downY = event.getY();
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                float dx = event.getX() - downX;
                float dy = event.getY() - downY;
                if (Math.abs(dx) > dp(70) && Math.abs(dx) > Math.abs(dy) * 1.45f) {
                    MotionEvent cancel = MotionEvent.obtain(event);
                    cancel.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(cancel);
                    cancel.recycle();
                    move(dx > 0 ? -1 : 1);
                    return true;
                }
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
