package cn.fsp.chatbridgevelocity.chat.util;

/**
 * 平台发送者接口
 * 用于向平台回复消息
 */
public interface PlatformSender {
    void reply(String message);
    PlatformSender getSender();
}
