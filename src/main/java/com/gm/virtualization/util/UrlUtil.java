package com.gm.virtualization.util;

public abstract class UrlUtil {

    public static String getCorrectUrl(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.lastIndexOf("/"));
        }

        return url;
    }
}
