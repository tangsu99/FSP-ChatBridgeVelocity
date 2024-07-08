package cn.fsp.chatbridgevelocity.serverPacket;

import cn.fsp.chatbridgevelocity.ChatBridgeVelocity;
import cn.fsp.chatbridgevelocity.chat.ChatForward;
import cn.fsp.chatbridgevelocity.config.Config;
import cn.fsp.chatbridgevelocity.event.SocketEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements Runnable {
    private ChatBridgeVelocity plugin;
    private ProxyServer server;
    private Config config;
    private ServerSocket serverSocket;
    ChatForward chatForward;
    private boolean start;

    public SocketServer(ChatBridgeVelocity plugin) {
        this.plugin = plugin;
        this.server = plugin.server;
        this.config = plugin.config;
        this.chatForward = plugin.chatForward;
        this.start = true;
        try {
            serverSocket = new ServerSocket(config.getStatusReceivedPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startListener() throws IOException {
        server.getScheduler().buildTask(plugin, this).schedule();
        plugin.logger.info("Listener started");
    }

    public void close() {
        start = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        while (start) {
            try {
                Socket s = serverSocket.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String mess = br.readLine();
                server.getEventManager().fire(new SocketEvent(mess.toCharArray()[0], mess.substring(1).trim()));
//                plugin.logger.info(mess);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        }
    }
}
