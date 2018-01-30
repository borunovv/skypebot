package com.borunovv.skypebot.core.service.skype;

import com.borunovv.skypebot.core.service.AbstractService;
import com.borunovv.skypebot.core.service.modules.ModuleManager;
import com.borunovv.skypebot.core.service.modules.Request;
import com.borunovv.skypebot.core.util.JsonUtils;
import com.borunovv.skypebot.core.util.StringUtils;
import com.borunovv.skypebot.core.util.UrlReader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author borunovv
 */
@Service
public class SkypeChatService extends AbstractService {

    public void process(SkypeInputMessage msg) {
        LOG.info(">> Request from '" + msg.getFromName() + "': '" + msg.getText() + "'");

        moduleManager.processRequest(
                new Request(msg.getTimeStamp(), msg.getFromId(), msg.getFromName(), filterEditedText(msg.getText())));
    }

    private String filterEditedText(String text) {
        String prefix = "Edited previous message: ";
        String suffix = "<e_m ts=";
        if (text.startsWith(prefix) && text.contains(suffix)) {
            // Мы имеем дело с отредактированный сообщением.
            return text.substring(prefix.length(), text.indexOf(suffix));
        } else {
            return text;
        }
    }

    public void sendTextMessage(String conversationOrRecipientId, String userName, String text) {
        String oauthToken = tokenService.getOauthToken();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", "message/text");
        params.put("text", text);
        String json = JsonUtils.toJson(params);

        String url = String.format("https://apis.skype.com/v3/conversations/%s/activities",
                conversationOrRecipientId);

        UrlReader.Request req = new UrlReader.Request(UrlReader.Method.POST, url,
                StringUtils.getBytesUtf8(json), "application/json; charset=utf-8");
        req.setExtraHeader("Authorization", "Bearer " + oauthToken);

        try {
            UrlReader.Response resp = UrlReader.send(req);
            if (resp.getStatus() != HttpStatus.OK && resp.getStatus() != HttpStatus.CREATED) {
                throw new RuntimeException("Unexpected response:\n" + resp + "\nRequest json:\n" + json);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send message via skype server.\n" + json, e);
        }

        LOG.info("<< Sent text to '" + userName + "': '" + text + "'");
    }

    @Inject
    private SkypeTokenService tokenService;
    @Inject
    private ModuleManager moduleManager;
}
