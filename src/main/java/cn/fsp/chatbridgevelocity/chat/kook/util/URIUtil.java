package cn.fsp.chatbridgevelocity.chat.kook.util;

import java.net.URI;

public class URIUtil {
    public static URI createURI(String url) {
        return URI.create(url);
    }

    public static URI createURI(String url, int compress) {
        return URI.create(url + "?compress=" + (compress == 0 ? 0 : 1));
    }
}
