package cn.fsp.chatbridgevelocity.command;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

public class CmdHandler {
//    private Config config;
    private ChatBridgeVelocity chatBridgeVelocity;
    private Logger logger;

    public CmdHandler(ChatBridgeVelocity chatBridgeVelocity) {
        this.chatBridgeVelocity = chatBridgeVelocity;
        this.logger = chatBridgeVelocity.logger;
    }

    public int help(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        source.sendMessage(Component.text("FSP-ChatBridgeVelocity 命令帮助"));
        return 1;
    }

    public int reload(CommandContext<CommandSource> commandSourceCommandContext) {
        chatBridgeVelocity.reload();
        commandSourceCommandContext.getSource().sendMessage(Component.text("Reload done!"));
        return 1;
    }
}