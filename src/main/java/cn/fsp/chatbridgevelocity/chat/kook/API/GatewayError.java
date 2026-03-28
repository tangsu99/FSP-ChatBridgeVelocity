package cn.fsp.chatbridgevelocity.chat.kook.API;

public class GatewayError extends RuntimeException {
    public GatewayError(String message) {
        super(message);
    }
}
