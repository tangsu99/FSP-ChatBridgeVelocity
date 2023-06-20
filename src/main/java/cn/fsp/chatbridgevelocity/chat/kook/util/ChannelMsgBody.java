package cn.fsp.chatbridgevelocity.chat.kook.util;

public class ChannelMsgBody {
    public int type = 0;
    public String target_id;
    public String content;

    public ChannelMsgBody() {

    }

    public static String msgBody(String c, String s) {
        return "{\"type\": 1, \"target_id\": \"Channel\", \"content\":\"Message\"}"
                .replaceAll("Channel", c)
                .replaceAll("Message", s)
                .replaceAll("\\n", "\\\\n");
//        return JsonUtil.gson.toJson(new ChannelMsgBody());
    }
}
