package cn.fsp.chatbridgevelocity.command;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.RawCommand;

public class CommandReg {
    public static void regCommand(CommandManager commandManager, ChatBridgeVelocity chatBridgeVelocity) {
        CommandMeta commandMeta = commandManager.metaBuilder("say")
                .plugin(chatBridgeVelocity)
                .build();
        RawCommand sayCommand = new SayCommand(chatBridgeVelocity.chatForward);
        commandManager.register(commandMeta, sayCommand);
    }
}
