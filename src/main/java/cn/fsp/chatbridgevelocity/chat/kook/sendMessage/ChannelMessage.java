package cn.fsp.chatbridgevelocity.chat.kook.sendMessage;

import cn.fsp.chatbridgevelocity.chat.kook.util.JsonUtil;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChannelMessage {
    private final HttpClient httpClient;
    private HttpRequest request;
    private final String token;
    private final URI uri;
    //    https://www.kookapp.cn/api/v3/message/create?compress=0

    public ChannelMessage(String apiURL,String token, int compress){
        this.uri = URI.create(apiURL + "?compress=" + (compress == 0 ? 0 : 1));
        this.httpClient = HttpClient.newHttpClient();
        this.token = token;
    }

    public ChannelMessage(URI uri,String token){
        this.uri = uri;
        this.httpClient = HttpClient.newHttpClient();
        this.token = token;
    }

    public String sendMessage(String body) {
        request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response.statusCode() != 200) {
            return null;
        }
        JsonObject jsonObject = JsonUtil.getJsonObject(response.body());
        return jsonObject.toString();
    }
}
