package cn.fsp.chatbridgevelocity.chat.kook.API;

import cn.fsp.chatbridgevelocity.chat.util.JsonUtil;
import cn.fsp.chatbridgevelocity.chat.util.URIUtil;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Gateway {
    private final HttpClient httpClient;
    private final HttpRequest request;

    public Gateway(String token, int compress) {
        this.httpClient = HttpClient.newHttpClient();
        this.request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.kookapp.cn/api/v3/gateway/index?compress=" + (compress == 0 ? 0 : 1)))
                .GET()
                .header("Authorization", "Bot " + token)
                .build();
    }

    public URI getGatewayURL() throws IOException, InterruptedException {
        HttpResponse<String> response;
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = JsonUtil.getJsonObject(response.body());
        int code = jsonObject.get("code").getAsInt();
        if (response.statusCode() != 200 || code != 0) {
            throw new  GatewayError(response.body());
        }
        return URIUtil.createURI(jsonObject.getAsJsonObject("data").get("url").getAsString());
    }
}
