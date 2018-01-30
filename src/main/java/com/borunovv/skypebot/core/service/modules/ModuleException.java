package com.borunovv.skypebot.core.service.modules;

/**
 * @author borunovv
 */
public class ModuleException extends RuntimeException {

    public ModuleException(String message) {
        super(message);
    }

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
