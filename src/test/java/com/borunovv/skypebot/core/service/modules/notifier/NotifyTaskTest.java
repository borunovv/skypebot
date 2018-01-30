package com.borunovv.skypebot.core.service.modules.notifier;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;

/**
 * @author borunovv
 */
public class NotifyTaskTest {

    @Test
    public void testUpdate() throws Exception {
        NotifyTask task = new NotifyTask("", "", "",
                SceduleUTC.makeSingle(new DateTime(2016, 9, 1, 10, 20, DateTimeZone.UTC)));

        boolean needNotify = task.update(new DateTime(2016, 9, 1, 10, 10, DateTimeZone.UTC));
        assertFalse(needNotify);

        needNotify = task.update(new DateTime(2016, 9, 1, 10, 15, DateTimeZone.UTC));
        assertFalse(needNotify);

        needNotify = task.update(new DateTime(2016, 9, 1, 10, 19, DateTimeZone.UTC));
        assertFalse(needNotify);

        needNotify = task.update(new DateTime(2016, 9, 1, 10, 20, 0, DateTimeZone.UTC));
        assertTrue(needNotify);

        needNotify = task.update(new DateTime(2016, 9, 1, 10, 20, 1, DateTimeZone.UTC));
        assertFalse(needNotify);
    }
}
