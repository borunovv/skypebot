package com.borunovv.skypebot.core.web.controller;

import com.borunovv.skypebot.core.service.AbstractService;
import com.borunovv.skypebot.core.util.HttpRequestDump;
import com.borunovv.skypebot.core.util.TimeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Controller
public class DebugController extends AbstractService {

    private static final String DELIM = "\n";
    private static final Date startDate = new Date();

    @RequestMapping(value = "/debug_stats/", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void debugStats(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dump = HttpRequestDump.requestToString(request);
        LOG.info(dump);

        System.out.println("\nRequest come\n" + dump + "\n\n");

        StringBuilder sb = new StringBuilder();
        sb.append("Start time: ").append(TimeUtils.toString(startDate)).append(DELIM);
        sb.append("\n\n");
        sb.append(dump);

        response.setContentType("text/plain; charset=UTF-8");
        response.getOutputStream().write(sb.toString().getBytes());
    }
}
