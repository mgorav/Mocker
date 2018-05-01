package com.gm.virtualization.util;

import org.springframework.http.HttpHeaders;

abstract public class HttpHeadersUtil {

    public static HttpHeaders getAptRequestHttpHeaders(HttpHeaders httpHeaders) {
        HttpHeaders newHttpHeaders = new HttpHeaders();
        httpHeaders.entrySet().forEach(entrySet -> {
            if (!entrySet.getKey().equalsIgnoreCase("host")) {
                entrySet.getValue().forEach(value -> {
                    newHttpHeaders.add(entrySet.getKey(), value);
                });
            }
        });

        return newHttpHeaders;
    }
}
