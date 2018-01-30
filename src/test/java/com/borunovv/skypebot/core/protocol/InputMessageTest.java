package com.borunovv.skypebot.core.protocol;

import com.borunovv.skypebot.core.AbstractTest;
import com.borunovv.skypebot.core.service.skype.SkypeInputMessage;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author borunovv
 */
public class InputMessageTest extends AbstractTest {

    private static final String JSON = "{\n" +
            "  \"type\": \"message\",\n" +
            "  \"id\": \"6PGUZkzGPggIYsK\",\n" +
            "  \"timestamp\": \"2016-08-31T15:14:11.1Z\",\n" +
            "  \"serviceUrl\": \"https://skype.botframework.com\",\n" +
            "  \"channelId\": \"skype\",\n" +
            "  \"from\": {\n" +
            "    \"id\": \"29:1ZtLM7gPrC8GHkVeOwH0WpiFIo4_M1ME3Ar0qKJXTcgw\",\n" +
            "    \"name\": \"Vladimir\"\n" +
            "  },\n" +
            "  \"conversation\": {\n" +
            "    \"id\": \"29:1ZtLM7gPrC8GHkVeOwH0WpiFIo4_M1ME3Ar0qKJXTcgw\"\n" +
            "  },\n" +
            "  \"recipient\": {\n" +
            "    \"id\": \"28:96bbc1d7-9879-4779-aef0-60ebdfead872\",\n" +
            "    \"name\": \"TestBot\"\n" +
            "  },\n" +
            "  \"text\": \"Hello\",\n" +
            "  \"entities\": []\n" +
            "}";

    @Test
    public void testDeserialization() throws Exception {
        SkypeInputMessage msg = new SkypeInputMessage(JSON);
        System.out.println(msg);
        assertTrue(msg.isSet());
        assertEquals("message", msg.getType());

        assertEquals("message", msg.getType());
        assertEquals("29:1ZtLM7gPrC8GHkVeOwH0WpiFIo4_M1ME3Ar0qKJXTcgw", msg.getFromId());
        assertEquals("Vladimir", msg.getFromName());
        assertEquals("29:1ZtLM7gPrC8GHkVeOwH0WpiFIo4_M1ME3Ar0qKJXTcgw", msg.getConversationId());
        assertEquals("28:96bbc1d7-9879-4779-aef0-60ebdfead872", msg.getRecipientId());
        assertEquals("TestBot", msg.getRecipientName());
        assertEquals("Hello", msg.getText());

        assertEquals(JSON, msg.getJson());

    }
}
