package cn.fsp.chatbridgevelocity.event;

import cn.fsp.chatbridgevelocity.chat.util.PlatformSender;

/**
 * 平台命令事件
 * 当平台消息被识别为命令时触发
 */
public class PlatformCommandEvent {
    private final String platform;
    private final String command;
    private final PlatformSender sender;

    public PlatformCommandEvent(String platform, String command, PlatformSender sender) {
        this.platform = platform;
        this.command = command;
        this.sender = sender;
    }

    public String getPlatform() {
        return platform;
    }

    public String getCommand() {
        return command;
    }

    public PlatformSender getSender() {
        return sender;
    }
}
