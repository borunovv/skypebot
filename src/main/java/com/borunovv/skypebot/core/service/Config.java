package com.borunovv.skypebot.core.service;

import com.borunovv.skypebot.core.util.IOUtils;
import com.borunovv.skypebot.core.util.JsonUtils;
import com.borunovv.skypebot.core.util.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author borunovv
 */
@Service
public class Config extends AbstractService implements DisposableBean {

    private static Map<String, Object> root = new HashMap<String, Object>();
    private volatile boolean loaded = false;

    @PostConstruct
    private synchronized void init() {
        if (!loaded) {
            try {
                loadAll();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config.", e);
            }
            loaded = true;
        }
    }

    @Override
    public void destroy() throws Exception {
        saveAll();
    }

    public synchronized void save(String key, Map<String, Object> params) {
        Assert.isTrue(!StringUtils.isNullOrEmpty(key), "key is empty");

        if (params == null) {
            root.remove(key);
        } else {
            root.put(key, new HashMap<String, Object>(params));
        }

        saveAll();
    }

    @SuppressWarnings("unchecked")
    public synchronized Map<String, Object> load(String key) {
        init();

        return root.containsKey(key) ?
                new HashMap<String, Object>((Map<String, Object>) root.get(key)) :
                null;
    }

    private synchronized void saveAll() {
        String json = JsonUtils.toJson(root);
        FileWriter writer = null;
        try {
            File config = new File(CONFIG_FILE_NAME);
            writer = new FileWriter(config);
            writer.write(json);
            writer.flush();
            LOG.info("Config saved (" + config.getAbsolutePath() + ")");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config.", e);
        } finally {
            IOUtils.close(writer);
        }
    }

    private synchronized void loadAll() throws IOException {
        root = new HashMap<String, Object>();
        InputStream reader = null;
        File config = new File(CONFIG_FILE_NAME);
        try {
            if (config.exists()) {
                reader = new FileInputStream(CONFIG_FILE_NAME);
                String content = IOUtils.inputStreamToString(reader);
                if (!content.isEmpty()) {
                    root = JsonUtils.fromJsonToMap(content);
                    LOG.info("Config loaded (" + config.getCanonicalPath() + ")");
                }
            }
        } finally {
            IOUtils.close(reader);
        }

        if (root.isEmpty()) {
            LOG.info("Config is empty or not exists (" + config.getCanonicalPath() + ")");
        }
    }

    @Value("${config.file}")
    private String CONFIG_FILE_NAME;

}
