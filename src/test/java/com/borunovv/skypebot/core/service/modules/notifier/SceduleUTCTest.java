package com.borunovv.skypebot.core.service.modules.notifier;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author borunovv
 */
public class SceduleUTCTest {

    @Test
    public void testPeriodic() throws Exception {
        SceduleUTC time = SceduleUTC.makePeriodic(10, 20, new int[]{1, 4, 5});

        DateTime timeDay4_10_30 = new DateTime(2016, 9, 1, 10, 30, DateTimeZone.UTC);
        long delta = time.getDeltaSecondsBeforeNotify(timeDay4_10_30);
        assertEquals(23*60*60 + 50*60, delta); // Day5_10:20 - Day4_10:30 = 23:50

        DateTime timeDay4_10_15 = new DateTime(2016, 9, 1, 10, 15, DateTimeZone.UTC);
        delta = time.getDeltaSecondsBeforeNotify(timeDay4_10_15);
        assertEquals(5 * 60, delta); // Day4_10:20 - Day4_10:15 = 00:05

        DateTime timeDay5_10_15 = new DateTime(2016, 9, 2, 10, 15, DateTimeZone.UTC);
        delta = time.getDeltaSecondsBeforeNotify(timeDay5_10_15);
        assertEquals(5 * 60, delta); // Day5_10:20 - Day5_10:15 = 00:05

        DateTime timeDay6_10_20 = new DateTime(2016, 9, 3, 10, 20, DateTimeZone.UTC);
        delta = time.getDeltaSecondsBeforeNotify(timeDay6_10_20);
        assertEquals(2 * 24*60*60, delta); // Day1_10:20 - Day6_10:20 = 48:00

        DateTime timeDay1_09_20 = new DateTime(2016, 9, 5, 9, 20, DateTimeZone.UTC);
        delta = time.getDeltaSecondsBeforeNotify(timeDay1_09_20);
        assertEquals(1 * 60*60, delta); // Day1_10:20 - Day1_09:20 = 01:00

        assertFalse(time.isExpired(new DateTime(DateTime.now().plusYears(1))));
    }

    @Test
    public void testSingle() throws Exception {
        SceduleUTC time = SceduleUTC.makeSingle(new DateTime(2016, 9, 1, 10, 30, DateTimeZone.UTC));

        DateTime time2016_09_01_10h_40m = new DateTime(2016, 9, 1, 10, 40, DateTimeZone.UTC);
        long delta = time.getDeltaSecondsBeforeNotify(time2016_09_01_10h_40m); // -10 min
        assertEquals(-10 * 60, delta);

        DateTime time2016_09_01_10h_20m = new DateTime(2016, 9, 1, 10, 20, DateTimeZone.UTC);
        delta = time.getDeltaSecondsBeforeNotify(time2016_09_01_10h_20m); // 10 min
        assertEquals(10 * 60, delta);

        DateTime time2016_09_02_10h_30m = new DateTime(2016, 9, 2, 10, 30, DateTimeZone.UTC);
        delta = time.getDeltaSecondsBeforeNotify(time2016_09_02_10h_30m); // -1day
        assertEquals(-1 * 24*60*60, delta);

        DateTime time2016_09_01_09h_20m = new DateTime(2016, 9, 1, 9, 20, DateTimeZone.UTC);
        delta = time.getDeltaSecondsBeforeNotify(time2016_09_01_09h_20m); // 01:10
        assertEquals((1 * 60 + 10) * 60, delta);

        DateTime time2016_08_31_10h_20m = new DateTime(2016, 8, 31, 10, 20, DateTimeZone.UTC);
        delta = time.getDeltaSecondsBeforeNotify(time2016_08_31_10h_20m); // 1d 00:10
        assertEquals(1*24*60*60 + 10 * 60, delta);

        assertFalse(time.isExpired(new DateTime(2016, 9, 1, 10, 30, DateTimeZone.UTC).minusDays(1)));
        assertTrue(time.isExpired(new DateTime(2016, 9, 1, 10, 30, DateTimeZone.UTC).plusDays(1)));
        assertTrue(time.isExpired(new DateTime(2016, 9, 1, 10, 30, DateTimeZone.UTC).plusSeconds(1)));
    }
}
