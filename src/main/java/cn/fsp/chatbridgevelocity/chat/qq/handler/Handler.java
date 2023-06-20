package cn.fsp.chatbridgevelocity.chat.qq.handler;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.chat.message.Message;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import cn.fsp.chatbridgevelocity.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

public abstract class Handler {
    public final Gson gson = new GsonBuilder().create();
    public ChatForward chatForward;
    public ProxyServer server;
    public Logger logger;
    public Config config;
    public QQChat qqChat;
    public Message message;

    public Handler(ChatForward chatForward) {
        this.chatForward = chatForward;
        this.server = chatForward.server;
        this.logger = chatForward.logger;
        this.config = chatForward.config;
        this.message = new Message();
    }

    public abstract void setQQChat(QQChat qqChat);

    public abstract void exec(String s);
    public abstract String send(String group, String msg);
}
