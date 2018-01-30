package com.borunovv.skypebot.core.web.controller;

import com.borunovv.skypebot.core.service.skype.SkypeInputMessage;
import com.borunovv.skypebot.core.service.AbstractService;
import com.borunovv.skypebot.core.service.skype.SkypeChatService;
import com.borunovv.skypebot.core.util.HttpRequestDump;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class SkypeController extends AbstractService {

    @Override
    public void onInit() {
        LOG.info("Skype bot started.");
    }

    @RequestMapping(value = "/inbound/", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void inbound(HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestBody String json) throws IOException {
        try {
            SkypeInputMessage msg = new SkypeInputMessage(json);

            String dump = HttpRequestDump.requestToString(request, false);
            String message = "REQUEST COME -------------------------------\n" + dump
                    + "\nJSON:\n" + json
                    + "\nParsed message:\n" + msg
                    + "\n------------------------------";

            LOG.info(message);
            System.out.println(message);

            response.setContentType("text/plain; charset=UTF-8");
            response.getOutputStream().write(StringUtils.getBytesUtf8(json));

            chatService.process(msg);
        } catch (Exception e) {
            LOG.error("Failed to process inbound request.", e);
            response.setContentType("text/plain; charset=UTF-8");
            response.getOutputStream().write(StringUtils.getBytesUtf8("ERROR"));
        }
    }

    @Inject
    private SkypeChatService chatService;
}
