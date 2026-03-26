package cn.fsp.chatbridgevelocity.refactoring.command;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.StatusManager;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

public class CmdHandler {
    private final ChatBridgeVelocity chatBridgeVelocity;
    private final Logger logger;
    private final StatusManager statusManager;

    public CmdHandler(ChatBridgeVelocity chatBridgeVelocity) {
        this.chatBridgeVelocity = chatBridgeVelocity;
        this.logger = chatBridgeVelocity.logger;
        this.statusManager = chatBridgeVelocity.statusManager;
    }

    public int help(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        source.sendMessage(Component.text("FSP-ChatBridgeVelocity 命令帮助"));
        source.sendMessage(Component.text("/cbv reload - 重新加载配置"));
        source.sendMessage(Component.text("/cbv status - 查看状态"));
        return 1;
    }

    public int reload(CommandContext<CommandSource> commandSourceCommandContext) {
        chatBridgeVelocity.reload();
        commandSourceCommandContext.getSource().sendMessage(Component.text("Reload done!"));
        return 1;
    }

    public int status(CommandContext<CommandSource> commandSourceCommandContext) {
        commandSourceCommandContext.getSource().sendMessage(Component.text(statusManager.getStatusString()));
        return 1;
    }
}
