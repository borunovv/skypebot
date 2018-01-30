package com.borunovv.skypebot.core.service.modules;

/**
 * @author borunovv
 */
public interface IModule {
    public void process(Request request) throws ModuleException;
}
