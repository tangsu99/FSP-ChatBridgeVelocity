package cn.fsp.chatbridgevelocity.chat;

import cn.fsp.chatbridgevelocity.refactoring.config.Config;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.text.MessageFormat;

public class MessageFormatter {
    private final Config config;

    public MessageFormatter(Config config) {
        this.config = config;
    }

    public Component formatChatMessage(String serverName, String playerName, String message) {
        MessageFormat formatter = new MessageFormat(config.getMessageFormat());
        return Component.text(formatter.format(new String[]{serverName, playerName, message}));
    }

    public Component formatJoinMessage(String serverName, String playerName) {
        MessageFormat formatter = new MessageFormat(config.getJoinFormat());
        return Component.text(formatter.format(new String[]{serverName, playerName})).color(NamedTextColor.GRAY);
    }

    public Component formatLeaveMessage(String serverName, String playerName) {
        MessageFormat formatter = new MessageFormat(config.getLeftFormat());
        return Component.text(formatter.format(new String[]{serverName, playerName})).color(NamedTextColor.GRAY);
    }

    public Component formatServerStartedMessage(String serverName) {
        return Component.text(serverName + " started!").color(NamedTextColor.GRAY);
    }

    public Component formatServerStoppedMessage(String serverName) {
        return Component.text(serverName + " stopped!").color(NamedTextColor.GRAY);
    }

    public String formatQQMessage(String serverName, String playerName, String message) {
        MessageFormat formatter = new MessageFormat(config.getQQMessageFormat());
        return formatter.format(new String[]{serverName, playerName, message});
    }

    public String formatQQJoinMessage(String playerName) {
        MessageFormat formatter = new MessageFormat(config.getQQJoinFormat());
        return formatter.format(new String[]{playerName});
    }
}
