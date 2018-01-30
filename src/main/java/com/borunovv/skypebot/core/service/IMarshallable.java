package com.borunovv.skypebot.core.service;

import java.util.Map;

/**
 * @author borunovv
 */
public interface IMarshallable {
    public Map<String, Object> marshall();
    public void unmarshall(Map<String, Object> params);
}
