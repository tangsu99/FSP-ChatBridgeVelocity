package cn.fsp.chatbridgevelocity.command;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;

public class CmdBuilder {
    public BrigadierCommand register(ChatBridgeVelocity chatBridgeVelocity) {
        final CmdHandler cmdHandler = new CmdHandler(chatBridgeVelocity);
        LiteralCommandNode<CommandSource> cmdNode = LiteralArgumentBuilder
                .<CommandSource>literal("cbv").executes(cmdHandler::help)
                .then(LiteralArgumentBuilder.<CommandSource>literal("reload").executes(cmdHandler::reload))
                .then(LiteralArgumentBuilder.<CommandSource>literal("status").executes(cmdHandler::status))
                .then(LiteralArgumentBuilder.<CommandSource>literal("say")
                    .then(RequiredArgumentBuilder.<CommandSource, String>argument("message", StringArgumentType.greedyString())
                        .executes(cmdHandler::say)))
                .build();
        return new BrigadierCommand(cmdNode);
    }
}
