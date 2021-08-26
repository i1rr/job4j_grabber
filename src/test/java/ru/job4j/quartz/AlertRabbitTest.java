package ru.job4j.quartz;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AlertRabbitTest {

    @Test
    public void isInt() {
        AlertRabbit ar = new AlertRabbit();
        assertTrue(ar.isInt("66"));
        assertFalse(ar.isInt("kasha"));
    }
}