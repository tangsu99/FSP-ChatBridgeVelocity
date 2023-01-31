package cn.fsp.chatbridgevelocity.chat.qq.Event;

public class groupEvent {
    String post_type;
    String message_type;
    long time;
    long self_id;
    String sub_type;
    long message_id;
    public String group_id;
    String raw_message;
    public Sender sender;
    long user_id;
    String anonymous;
    int font;
    public String message;
    long message_seq;

    public class Sender {
        int age;
        long user_id;
        String area;
        public String card;
        String level;
        public String nickname;
        String role;
        String sex;
        String title;
    }
}
