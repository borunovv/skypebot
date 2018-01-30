package com.borunovv.skypebot.core.service.modules;

import com.borunovv.skypebot.core.service.AbstractService;
import com.borunovv.skypebot.core.service.modules.notifier.NotifierModule;
import com.borunovv.skypebot.core.service.skype.SkypeChatService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author borunovv
 */
@Service
public class ModuleManager extends AbstractService {

    public void processRequest(Request request) {
        try {
            notifierModule.process(request);
        } catch (ModuleException e) {
            LOG.error("Module error processing request.\n" + request, e);
            String errorMsg = "ERROR: " + e.getMessage();
            skype.sendTextMessage(request.userId, request.userName, errorMsg);
        } catch (Exception e) {
            LOG.error("Fatal error processing request.\n" + request, e);
            String errorMsg = "FATAL ERROR";
            skype.sendTextMessage(request.userId, request.userName, errorMsg);
        }
    }

    @Inject
    private NotifierModule notifierModule;
    @Inject
    private SkypeChatService skype;
}
