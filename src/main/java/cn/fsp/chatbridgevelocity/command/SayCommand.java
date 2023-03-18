package cn.fsp.chatbridgevelocity.command;

import cn.fsp.chatbridgevelocity.chat.ChatForward;
import com.velocitypowered.api.command.RawCommand;

public final class SayCommand implements RawCommand {
    private ChatForward chatForward;

    public SayCommand(ChatForward chatForward) {
        this.chatForward = chatForward;
    }

    @Override
    public void execute(final Invocation invocation) {
        chatForward.sendMessageALL(invocation.arguments());
//        invocation.source().sendMessage(Component.text(invocation.arguments()));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("command.say");
    }
}