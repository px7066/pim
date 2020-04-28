package com.github.pim.server.core;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

/**
 * <p></p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/4/28 13:26
 * @since 1.0
 */
@Slf4j
public class SocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) throws Exception {
        WebSocketServerHandshaker handshaker = ServerContext.getCurrentHandshaker();
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
            return;
        }
        if (webSocketFrame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {
            log.info("数据帧类型不支持!");
            throw new UnsupportedOperationException(String.format("%s frame types not supported", webSocketFrame.getClass().getName()));
        }

        // Send the uppercase string back.
        String request = ((TextWebSocketFrame) webSocketFrame).text();
        log.info("Netty服务器接收到的信息: " + request);

        JSONObject jsonData = JSONObject.parseObject(request);
        String eventType = jsonData.getString("eventType");
        String apiToken = jsonData.getString("apiToken");
        if (Const.NEW.name().equals(eventType)) {
            log.info("new event");
            ChannelSupervise.addChannel(apiToken, ctx.channel());
            JSONObject jsonMsg = new JSONObject();
            jsonMsg.put("code", 0);
            jsonMsg.put("message", "success");
            ChannelSupervise.sendToSimple(apiToken,  new TextWebSocketFrame(jsonMsg.toJSONString()));
        } else if (Const.UPDATE.name().equals(eventType)){
            log.info("update event");
            ChannelSupervise.updateChannel(apiToken, ctx.channel());
            JSONObject jsonMsg = new JSONObject();
            jsonMsg.put("code", 0);
            jsonMsg.put("message", "success");
            ChannelSupervise.sendToSimple(apiToken,  new TextWebSocketFrame(jsonMsg.toJSONString()));
        } else if (Const.BEHIND.name().equals(eventType)) {
            log.info("behind event");
            Channel chan = ChannelSupervise.findChannel(apiToken);
            if (null == chan) {
                log.info("目标用户不存在");
            } else {
                JSONObject jsonMsg = new JSONObject();
                jsonMsg.put("code", 1);
                jsonMsg.put("message", jsonData.get("message"));
                ChannelSupervise.sendToSimple(apiToken, new TextWebSocketFrame(jsonMsg.toString()));
                System.out.println("向目标用户发送成功");
            }
        } else {
            System.out.println("event type error");
        }
    }
}
