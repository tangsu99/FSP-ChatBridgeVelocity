package cn.fsp.chatbridgevelocity.chat.qq.handler;

import cn.fsp.chatbridgevelocity.chat.message.Message;
import cn.fsp.chatbridgevelocity.chat.qq.QQChat;
import cn.fsp.chatbridgevelocity.refactoring.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

/**
 * QQ处理器基类
 * 负责解析和处理来自不同QQ机器人框架的消息
 */
public abstract class Handler {
    protected final Gson gson = new GsonBuilder().create();
    protected final ProxyServer server;
    protected final Logger logger;
    protected final Config config;
    protected final Message message;
    protected QQChat qqChat;

    public Handler(ProxyServer server, Logger logger, Config config) {
        this.server = server;
        this.logger = logger;
        this.config = config;
        this.message = new Message();
    }

    public void setQQChat(QQChat qqChat) {
        this.qqChat = qqChat;
    }

    /**
     * 处理接收到的消息
     */
    public abstract void exec(String json);

    /**
     * 生成发送消息的JSON
     */
    public abstract String send(String group, String msg);

    /**
     * 构建QQ消息事件
     */
    protected abstract void fireMessageEvent(String group, String sender, String message);
}
