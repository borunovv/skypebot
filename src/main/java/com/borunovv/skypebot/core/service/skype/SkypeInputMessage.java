package com.borunovv.skypebot.core.service.skype;

import com.borunovv.skypebot.core.util.JsonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author borunovv
 */

/**
 * {
 * "type": "message",
 * "id": "6PGUZkzGPggIYsK",
 * "timestamp": "2016-08-31T15:14:11.1Z",
 * "serviceUrl": "https://skype.botframework.com",
 * "channelId": "skype",
 * "from": {
 * "id": "29:1ZtLM7gPrC8GHkVeOwH0WpiFIo4_M1ME3Ar0qKJXTcgw",
 * "name": "Vladimir"
 * },
 * "conversation": {
 * "id": "29:1ZtLM7gPrC8GHkVeOwH0WpiFIo4_M1ME3Ar0qKJXTcgw"
 * },
 * "recipient": {
 * "id": "28:96bbc1d7-9879-4779-aef0-60ebdfead872",
 * "name": "TestBot"
 * },
 * "text": "Hello",
 * "entities": []
 * }
 */
public class SkypeInputMessage {

    private static SimpleDateFormat TIMESTAMP_FORMAT ;
    static {
        TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private static final String PARAM_TYPE = "type";
    private static final String PARAM_ID = "id";
    private static final String PARAM_TIMESTAMP = "timestamp";
    private static final String PARAM_SERVICE_URL = "serviceUrl";
    private static final String PARAM_CHANNEL_ID = "channelId";
    private static final String PARAM_FROM = "from";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_CONVERSATION = "conversation";
    private static final String PARAM_RECIPIENT = "recipient";
    private static final String PARAM_TEXT = "text";

    private Map<String, Object> params;
    private String originalJson;


    @SuppressWarnings("unchecked")
    public SkypeInputMessage(String json) {
        this.originalJson = json;
        if (json != null) {
            params = (Map<String, Object>) JsonUtils.fromJson(json, Map.class);
        }
    }

    public boolean isSet() {
        return params != null
                && !params.isEmpty()
                && getType() != null;
    }

    public String getType() {
        return getParam(PARAM_TYPE);
    }

    public String getFromId() {
        return getSubParam(PARAM_FROM, PARAM_ID);
    }

    public String getFromName() {
        return getSubParam(PARAM_FROM, PARAM_NAME);
    }

    public String getConversationId() {
        return getSubParam(PARAM_CONVERSATION, PARAM_ID);
    }

    public String getRecipientId() {
        return getSubParam(PARAM_RECIPIENT, PARAM_ID);
    }

    public String getRecipientName() {
        return getSubParam(PARAM_RECIPIENT, PARAM_NAME);
    }

    public String getText() {
        return getParam(PARAM_TEXT);
    }

    public Date getTimeStamp() {
        String dateStr = getParam(PARAM_TIMESTAMP);
        if (dateStr != null) {
            try {
                return TIMESTAMP_FORMAT.parse(dateStr);
            } catch (ParseException ignore) {
            }
        }
        return null;
    }

    public String getJson() {
        return originalJson;
    }

    private String getSubParam(String parent, String child) {
        if (isSet() && params.containsKey(parent)) {
            return getParam((Map<String, Object>) params.get(parent), child);
        } else {
            return null;
        }
    }

    private String getParam(String name) {
        return getParam(params, name);
    }

    private String getParam(Map<String, Object> params, String name) {
        return params != null ?
                params.get(name).toString() :
                null;
    }

    @Override
    public String toString() {
        return "InputMessage{" +
                "originalJson='\n" + originalJson + '\n' +
                "\n isSet=" + isSet() +
                "\n type='" + getType() + '\'' +
                "\n timestamp: " + getTimeStamp() +
                "\n senderId='" + getFromId() + '\'' +
                "\n senderName='" + getFromName() + '\'' +
                "\n conversationId='" + getConversationId() + '\'' +
                "\n recipientId='" + getRecipientId() + '\'' +
                "\n recipientName='" + getRecipientName() + '\'' +
                "\n text='" + getText() + '\'' +
                '}';
    }
}
