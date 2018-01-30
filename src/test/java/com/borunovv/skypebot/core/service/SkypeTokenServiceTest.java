package com.borunovv.skypebot.core.service;

import com.borunovv.skypebot.core.AbstractTest;
import com.borunovv.skypebot.core.service.skype.SkypeTokenService;
import org.junit.Test;

import javax.inject.Inject;

/**
 * @author borunovv
 */
public class SkypeTokenServiceTest extends AbstractTest {

    @Test
    public void testGetOauthToken() throws Exception {
        System.out.println(tokenService.getOauthToken());
    }

    @Inject
    private SkypeTokenService tokenService;
}
