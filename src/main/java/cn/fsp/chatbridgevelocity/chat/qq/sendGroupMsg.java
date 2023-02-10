package cn.fsp.chatbridgevelocity.chat.qq;

public class sendGroupMsg {
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
