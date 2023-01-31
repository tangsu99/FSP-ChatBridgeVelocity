package cn.fsp.chatbridgevelocity.command;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

public class CmdBuilder {
    public BrigadierCommand register(ChatBridgeVelocity chatBridgeVelocity) {
        final CmdHandler cmdHandler = new CmdHandler(chatBridgeVelocity);
        LiteralCommandNode<CommandSource> cmdNode = LiteralArgumentBuilder
                .<CommandSource>literal("cbv").executes(cmdHandler::help)
                .build();
        return new BrigadierCommand(cmdNode);
    }
}
