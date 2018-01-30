package com.borunovv.skypebot.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author borunovv
 */
@Service
public abstract class AbstractService implements DisposableBean {

    protected Logger LOG;

    @PostConstruct
    private void init() {
        LOG = LoggerFactory.getLogger(this.getClass());
        onInit();
    }

    @Override
    public void destroy() throws Exception {
        onDestroy();
    }

    protected void onInit() {}
    protected void onDestroy() {}
}
