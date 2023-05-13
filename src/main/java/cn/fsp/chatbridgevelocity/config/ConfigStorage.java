package cn.fsp.chatbridgevelocity.config;

public class ConfigStorage {
    boolean chatForwardEnabled = true;
    String mcRespondPrefix = "!!qq";
    String QQRespondPrefix = "!!mc";
    String[] serverList = {"Survival", "Creative", "Mirror"};
    String messageFormat = "[{0}]<{1}> {2}";
    String joinFormat = "[{0}] {1} joined {0}";
    String leftFormat = "[{0}] {1} left {0}";
    boolean QQChatEnabled = true;
    String QQMessageFormat = "[{0}]<{1}> {2}";
    boolean QQJoinMessageEnabled = true;
    Integer CD = 30;
    boolean goCQHttp = true;
    String QQJoinFormat = "{0} joined game.";
    String BotQQ = "00000000";
    String QQGroup = "00000000";
    String host = "127.0.0.1";
    Integer port = 6700;
    String token = "TOKEN";
    Integer statusReceivedPort = 5700;
}
