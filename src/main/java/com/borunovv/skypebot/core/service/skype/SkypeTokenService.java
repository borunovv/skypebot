package com.borunovv.skypebot.core.service.skype;

import com.borunovv.skypebot.core.service.AbstractService;
import com.borunovv.skypebot.core.util.JsonUtils;
import com.borunovv.skypebot.core.util.StringUtils;
import com.borunovv.skypebot.core.util.UrlReader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author borunovv
 */
@Service
public class SkypeTokenService extends AbstractService {

    private static final String GET_TOKEN_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";

    private static final String APP_ID = "96bbc1d7-9879-4779-aef0-60ebdfead872";
    private static final String APP_SECRET = "31mQKhcMtmLOAhNsaGPEPqT";

    private static volatile String LAST_TOKEN_JSON;
    private static volatile String LAST_TOKEN;
    private static volatile long lastUpdate = 0;
    private static volatile long lifeTimeSeconds = 0;


    public String getOauthToken() {
        updateTokenIfNeed();
        return LAST_TOKEN;
    }

    private void updateTokenIfNeed() {
        if (LAST_TOKEN != null && System.currentTimeMillis() - lastUpdate < lifeTimeSeconds * 1000 / 2) {
            return;
        }

        // client_id=f&client_secret=&grant_type=client_credentials&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default
        StringBuilder content = new StringBuilder();
        content.append("client_id=").append(StringUtils.urlEncode(APP_ID)).append("&");
        content.append("client_secret=").append(StringUtils.urlEncode(APP_SECRET)).append("&");
        content.append("grant_type=client_credentials&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default");

        byte[] postData = StringUtils.getBytesUtf8(content.toString());

        UrlReader.Request req = new UrlReader.Request(
                UrlReader.Method.POST,
                GET_TOKEN_URL,
                postData,
                "application/x-www-form-urlencoded");

        try {
            UrlReader.Response resp = UrlReader.send(req);
            if (resp.getStatus() != HttpStatus.OK) {
                throw new RuntimeException("Unexpected OAuth response: \n" + resp);
            }
            LAST_TOKEN_JSON = resp.getBodyAsString();
            lastUpdate = System.currentTimeMillis();

            onTokenUpdated();
        } catch (IOException e) {
            throw new RuntimeException("Failed to obtain OAuth token", e);
        }

        lastUpdate = System.currentTimeMillis();
    }

    @SuppressWarnings("unchecked")
    private void onTokenUpdated() {
        Map<String, Object> params = (Map<String, Object>) JsonUtils.fromJson(LAST_TOKEN_JSON, Map.class);
        lifeTimeSeconds = Integer.parseInt(params.get("expires_in").toString());
        LAST_TOKEN = params.get("access_token").toString();

        LOG.info("New OAuth token obtained. Lifetime: " + lifeTimeSeconds + " sec");
    }
}
