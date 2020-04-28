package com.github.pim.server.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p></p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/4/28 13:33
 * @since 1.0
 */
public class ChannelSupervise {
    private static ChannelGroup GlobalGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static ConcurrentMap<String, ChannelId> ChannelMap = new ConcurrentHashMap<>();

    public synchronized static void addChannel(String apiToken, Channel channel) {
        GlobalGroup.add(channel);
        if (null != apiToken) {
            ChannelMap.put(apiToken, channel.id());
        }
    }

    public synchronized static void updateChannel(String apiToken, Channel channel) {
        Channel chan = GlobalGroup.find(channel.id());
        if (null == chan) {
            addChannel(apiToken, channel);
        } else {
            ChannelMap.put(apiToken, channel.id());
        }
    }

    public synchronized static void removeChannel(Channel channel) {
        GlobalGroup.remove(channel);
        Collection<ChannelId> values = ChannelMap.values();
        values.remove(channel.id());
    }

    public synchronized static Channel findChannel(String apiToken) {
        ChannelId chanId = ChannelMap.get(apiToken);
        if (null == chanId) {
            return null;
        }

        return GlobalGroup.find(ChannelMap.get(apiToken));
    }

    public static void sendToAll(TextWebSocketFrame tws) {
        GlobalGroup.writeAndFlush(tws);
    }

    public static void sendToSimple(String apiToken, TextWebSocketFrame tws) {
        GlobalGroup.find(ChannelMap.get(apiToken)).writeAndFlush(tws);
    }
}
