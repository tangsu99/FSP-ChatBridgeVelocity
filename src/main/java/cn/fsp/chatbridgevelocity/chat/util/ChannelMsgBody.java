package cn.fsp.chatbridgevelocity.chat.util;

public class ChannelMsgBody {
    public int type = 0;
    public String target_id;
    public String content;

    public ChannelMsgBody(String target_id, String content) {
        this.target_id = target_id;
        this.content = content;
    }

    public static String msgBody(String id, String content) {
//        return "{\"type\": 1, \"target_id\": \"Channel\", \"content\":\"Message\"}"
//                .replaceAll("Channel", c)
//                .replaceAll("Message", s)
//                .replaceAll("\\n", "\\\\n");
        return JsonUtil.gson.toJson(new ChannelMsgBody(id, content));
    }
}
