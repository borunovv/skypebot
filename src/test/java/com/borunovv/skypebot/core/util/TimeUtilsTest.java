package com.borunovv.skypebot.core.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author borunovv
 */
public class TimeUtilsTest {

    @Test
    public void testSecondsToTime() throws Exception {
        assertEquals("00:00:12", TimeUtils.secondsToTime(12));
        assertEquals("02:15:43", TimeUtils.secondsToTime(2 * 3600 + 15 * 60 + 43));
    }
}
