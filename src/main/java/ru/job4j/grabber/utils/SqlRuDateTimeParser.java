package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.util.Map.entry;

public class SqlRuDateTimeParser implements DateTimeParser {
    private static final Map<String, String> MONTHS = Map.ofEntries(
            entry("янв", "01"),
            entry("фев", "02"),
            entry("мар", "03"),
            entry("апр", "04"),
            entry("май", "05"),
            entry("июн", "06"),
            entry("июл", "07"),
            entry("авг", "08"),
            entry("сен", "09"),
            entry("окт", "10"),
            entry("ноя", "11"),
            entry("дек", "12"),
            entry("сегодня", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("d-MM-yy"))),
            entry("вчера", LocalDateTime.now()
                    .minusDays(1).format(DateTimeFormatter.ofPattern("d-MM-yy"))));

    @Override
    public LocalDateTime parse(String dateToParse) {
        LocalDateTime rsl = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yy HH:mm");

        String month = "сегодня";
        String[] dateTime  = dateToParse.trim().split(", ");

        if (dateTime[0].equals("сегодня")) {
            return LocalDateTime.parse(MONTHS.get("сегодня") + " " + dateTime[1], formatter);
        } else if (dateTime[0].equals("вчера")) {
            return LocalDateTime.parse(MONTHS.get("вчера") + " " + dateTime[1], formatter);
        }

        for (String monthsKeys : MONTHS.keySet()) {
            if (dateTime[0].contains(monthsKeys)) {
                month = monthsKeys;
                break;
            }
        }

        String[] dayAndYear = dateTime[0].trim().split(" " + month + " ");
        rsl = LocalDateTime.parse(dayAndYear[0] + "-"
                + MONTHS.get(month) + "-"
                + dayAndYear[1] + " "
                + dateTime[1],
                formatter);
        return rsl;
    }
}
