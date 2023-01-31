package cn.fsp.chatbridgevelocity.chat.qq.API;

public class sendGroupMsg {
    //        {
//        "action": "终结点名称, 例如 'send_group_msg'",
//        "params": {
//                "参数名": "参数值",
//                "参数名2": "参数值"
//        },
//        "echo": "'回声', 如果指定了 echo 字段, 那么响应包也会同时包含一个 echo 字段, 它们会有相同的值"
//    }
    String action = "send_group_msg";
    Params params;
    String echo;

    public sendGroupMsg(String group_id, String msg, String echo) {
        this.echo = echo;
        params = new Params(group_id, msg);
    }

    class Params {
        String group_id;
        String message;

        public Params(String group_id, String message) {
            this.message = message;
            this.group_id = group_id;
        }
    }
}
