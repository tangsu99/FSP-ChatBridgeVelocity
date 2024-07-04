package cn.fsp.chatbridgevelocity.chat.kook.API;

import cn.fsp.chatbridgevelocity.chat.util.JsonUtil;
import cn.fsp.chatbridgevelocity.chat.util.URIUtil;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChannelMessage {
    private final HttpClient httpClient;
    private final URI uri;
    private final String token;

    public ChannelMessage(String token) {
        this.httpClient = HttpClient.newHttpClient();
        this.uri = URIUtil.createURI("https://www.kookapp.cn/api/v3/message/create");
        this.token = token;
    }

    public int sendMessage(String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bot " + token)
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
            return 1000;
        }
        if (response.statusCode() != 200) {
            return 1000;
        }
        JsonObject jsonObject = JsonUtil.getJsonObject(response.body());
        return jsonObject.get("code").getAsInt();
    }
}
