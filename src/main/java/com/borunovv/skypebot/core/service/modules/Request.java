package com.borunovv.skypebot.core.service.modules;

import java.util.Date;

/**
 * @author borunovv
 */
public class Request {

    public final Date timestamp;
    public final String userId;
    public final String userName;
    public final String text;

    public Request(Date timestamp, String userId, String userName, String text) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Request{" +
                "timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
