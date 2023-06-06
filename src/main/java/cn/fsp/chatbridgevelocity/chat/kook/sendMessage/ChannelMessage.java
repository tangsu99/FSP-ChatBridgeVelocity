package cn.tangsu99.kookBot.sendMessage;

import cn.tangsu99.kookBot.util.JsonUtil;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChannelMessage {
    private final HttpClient httpClient;
    //    private final Gson gson;
    private final HttpRequest request;
    //    https://www.kookapp.cn/api/v3/message/create?compress=0
    public ChannelMessage(String apiURl, String token, int compress){
        this.httpClient = HttpClient.newHttpClient();
        this.request = HttpRequest.newBuilder()
                .uri(URI.create(apiURl + "?compress=" + (compress == 0 ? 0 : 1)))
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"target_id\": \"6270368475419721\",\"content\": \"Test\"}"))
                .build();
    }

    public String sendMessage() {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonObject jsonObject = JsonUtil.getJsonObject(response.body());
        return jsonObject.toString();
    }

    public static void main(String[] args){
        ChannelMessage channelMessage = new ChannelMessage("https://www.kookapp.cn/api/v3/message/create", "Bot 1/MTIwMTA=/bsK7tIANmEetiv55Iy6hzQ==", 0);
        System.out.println(channelMessage.sendMessage());
    }
}
