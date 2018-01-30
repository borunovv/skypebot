package com.borunovv.skypebot.core.service.modules;

import com.borunovv.skypebot.core.service.AbstractService;
import com.borunovv.skypebot.core.service.Config;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author borunovv
 */
@Service
public abstract class AbstractModule extends AbstractService implements IModule {

    private String moduleName;

    protected AbstractModule() {
        this.moduleName = this.getClass().getSimpleName();
    }

    @Override
    protected void onInit() {
        Map<String, Object> config = loadConfig();
        if (config != null) {
            setConfig(config);
        }
    }

    @Override
    public void onDestroy() {
        saveConfig(getConfig());
        super.onDestroy();
    }

    protected abstract Map<String, Object> getConfig();
    protected abstract void setConfig(Map<String, Object> params);


    protected void saveConfig() {
        config.save(moduleName, getConfig());
    }

    protected void saveConfig(Map<String, Object> params) {
        config.save(moduleName, params);
    }

    protected Map<String, Object> loadConfig() {
        return config.load(moduleName);
    }

    @Inject
    private Config config;
}
