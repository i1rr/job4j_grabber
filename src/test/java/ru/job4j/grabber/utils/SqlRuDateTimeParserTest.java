package ru.job4j.grabber.utils;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SqlRuDateTimeParserTest {

    @Test
    public void parse() {
        SqlRuDateTimeParser srdtp = new SqlRuDateTimeParser();
       String testDate =  "2 дек 19, 22:29";
       assertThat(srdtp.parse(testDate).toString(), is("2019-12-02T22:29"));
        assertThat(srdtp.parse(testDate), is(LocalDateTime.class));
    }
}