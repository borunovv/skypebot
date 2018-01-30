package com.borunovv.skypebot.core.service.modules;

/**
 * @author borunovv
 */
public interface IChannel {
    public void sendTextMessage(String conversationOrRecipientId, String userName, String text);
}
