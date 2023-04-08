package cn.fsp.chatbridgevelocity.chat.qq;

import java.util.ArrayList;
import java.util.List;

public class MiraiSendGroupMsg {
//    {
//        "syncId":123,
//        "command":"sendGroupMessage",
//        "subCommand":null,
//        "content":{
//            "sessionKey":"",
//            "target":276843084,
//            "messageChain":[
//                {
//                    "type":"Plain",
//                    "text":"awa"
//                }
//            ]
//        }
//    }
    public int syncId;
    public String command = "sendGroupMessage";
    public String sunCommand = "null";
    public Content content;

    public MiraiSendGroupMsg(String group, String msg){
        this.syncId = msg.length();
        this.content = new Content(group, msg);
    }

    public MiraiSendGroupMsg setMsg(String group, String msg, String sessionKey) {
        this.content = new Content(group, msg, sessionKey);
        return this;
    }

    class Content{
        String sessionKey;
        String target;
        List<MessageChain> messageChain = new ArrayList<>();
        public Content(String group, String msg){
            this.sessionKey = "";
            this.target = group;
            this.messageChain.add(new MessageChain("Plain", msg));
        }
        public Content(String group, String msg, String sessionKey){
            this.sessionKey = sessionKey;
            this.target = group;
            this.messageChain.add(new MessageChain("Plain", msg));
        }
    }

    class MessageChain{
        String type;
        String text;
        public MessageChain(String type, String text){
            this.type = type;
            this.text = text;
        }
    }
}
