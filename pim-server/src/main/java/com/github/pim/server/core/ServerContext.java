package com.github.pim.server.core;

import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * <p></p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/4/28 14:02
 * @since 1.0
 */
public class ServerContext {
    private static final ThreadLocal<WebSocketServerHandshaker> handshaker = new ThreadLocal<>();


    public static WebSocketServerHandshaker getCurrentHandshaker(){
        return handshaker.get();
    }

    public static void setCurrentHandshaker(WebSocketServerHandshaker webSocketServerHandshaker){
        handshaker.set(webSocketServerHandshaker);
    }
}
