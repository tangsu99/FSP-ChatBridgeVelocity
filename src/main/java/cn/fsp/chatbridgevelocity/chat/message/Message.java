package cn.fsp.chatbridgevelocity.chat.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Message {
    private MessageStorage msg;
    private final Path filePath = Paths.get("./plugins/fsp-chatbridgevelocity/message.json");

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public Message() {
        try {
            Path path = Paths.get("./plugins/fsp-chatbridgevelocity/");
            Files.createDirectory(path);
        } catch (IOException ignored) {
        }
        try {
            Files.createFile(filePath);
            Files.write(filePath, gson.toJson(new MessageStorage()).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
        loadFile();
    }

    public String getNoPermission() {
        return msg.noPermission;
    }

    public String getOnState() {
        return msg.onState;
    }

    public String getOffState() {
        return msg.offState;
    }

    public String getOn() {
        return msg.on;
    }

    public String getOff() {
        return msg.off;
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
        msg = gson.fromJson(configStr, MessageStorage.class);
        saveFile();
    }

    private void saveFile() {
        try {
            Files.write(filePath, gson.toJson(msg).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
