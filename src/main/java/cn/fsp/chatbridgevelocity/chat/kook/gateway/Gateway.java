package cn.fsp.chatbridgevelocity.chat.kook.gateway;

import cn.fsp.chatbridgevelocity.chat.kook.util.JsonUtil;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Gateway {
    private final HttpClient httpClient;
//    private final Gson gson;
    private final HttpRequest request;
    private int i;
//    https://www.kookapp.cn/api/v3/gateway/index
    public Gateway(String apiURl, String token, int compress) {
        this.httpClient = HttpClient.newHttpClient();
//        this.gson = new GsonBuilder().create();
        this.request = HttpRequest.newBuilder()
                .uri(URI.create(apiURl + "?compress=" + (compress == 0 ? 0 : 1)))
                .GET()
                .header("Authorization", token)
                .build();
        this.i = 0;
    }

    public String getGatewayURL() {
        if (i == 2) {
            i = 0;
            return null;
        }
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonObject jsonObject = JsonUtil.getJsonObject(response.body());
        int code = jsonObject.get("code").getAsInt();
        String message = jsonObject.get("message").getAsString();
        if (response.statusCode() != 200 || code != 0) {
            i++;
            System.out.println(message);
            return getGatewayURL();
        }
        return jsonObject.getAsJsonObject("data").get("url").getAsString();
    }

    public static String getGatewayURL(String apiURl, String token) {
        return new Gateway(apiURl, token, 0).getGatewayURL();
    }
}
