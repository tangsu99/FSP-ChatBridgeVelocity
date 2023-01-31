package cn.fsp.chatbridgevelocity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    private ConfigStorage cfg;
    private Path path = Paths.get("./plugins/fsp-chatbridgevelocity/");
    private Path filePath = Paths.get("./plugins/fsp-chatbridgevelocity/config.json");

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public Config() {
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
        }
        try {
            Files.createFile(filePath);
            Files.write(filePath, gson.toJson(new ConfigStorage()).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
        }
        loadFile();
    }

    public boolean ChatForwardEnabled() {
        return cfg.chatForwardEnabled;
    }

    public String getMCRespondPrefix() {
        return cfg.mcRespondPrefix;
    }

    public String getQQRespondPrefix() {
        return cfg.QQRespondPrefix;
    }

    public String[] getServerList() {
        return cfg.serverList;
    }

    public String getMessageFormat() {
        return cfg.messageFormat;
    }

    public String getJoinFormat() {
        return cfg.joinFormat;
    }

    public String getLeftFormat() {
        return cfg.leftFormat;
    }

    public boolean getQQChatEnabled() {
        return cfg.QQChatEnabled;
    }

    public String getQQMessageFormat() {
        return cfg.QQMessageFormat;
    }

    public boolean getQQJoinMessageEnabled() {
        return cfg.QQJoinMessageEnabled;
    }

    public String getQQJoinFormat() {
        return cfg.QQJoinFormat;
    }

    public String getQQGroup() {
        return cfg.QQGroup;
    }

    public String getHost() {
        return cfg.host;
    }

    public String getPort() {
        return cfg.port;
    }

    public String getToken() {
        return cfg.token;
    }

    public Integer getCD() {
        return cfg.CD;
    }

    public void reLoadConfig() {
        loadFile();
    }

    private void loadFile() {
        String configStr;
        try {
            configStr = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cfg = gson.fromJson(configStr, ConfigStorage.class);
    }

    private void saveFile() {
        try {
            Files.write(filePath, gson.toJson(cfg).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
