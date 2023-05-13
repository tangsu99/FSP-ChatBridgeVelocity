package cn.fsp.chatbridgevelocity.serverPacket;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.config.Config;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class SocketServer {
    private ChatBridgeVelocity plugin;
    private ProxyServer server;
    private Config config;
    private ServerSocket serverSocket;

    public SocketServer(ChatBridgeVelocity plugin) {
        this.plugin = plugin;
        this.server = plugin.server;
        this.config = plugin.config;
        try {
            serverSocket = new ServerSocket(plugin.config.getStatusReceivedPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startListener(ChatForward chatForward) throws IOException {
        plugin.server.getScheduler()
                .buildTask(plugin, () -> {
                    // do stuff here
                    try {
                        Socket s = serverSocket.accept();
                        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        //读取客户端发送来的消息
                        String mess = br.readLine();
                        chatForward.serverPacketEvent(mess.toCharArray()[0], mess.substring(1).trim());
                    } catch (IOException e) {
//                        throw new RuntimeException(e);
                    }
                })
                .repeat(1L, TimeUnit.SECONDS)
                .schedule();
        plugin.logger.info("Listener started");
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }
}
