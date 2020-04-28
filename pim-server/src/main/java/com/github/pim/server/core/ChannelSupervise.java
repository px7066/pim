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
    private static ChannelGroup GLOBAL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static ConcurrentMap<String, ChannelId> CHANNEL_MAP = new ConcurrentHashMap<>();


    public synchronized static void addChannel(String apiToken, Channel channel) {
        GLOBAL_GROUP.add(channel);
        if (null != apiToken) {
            CHANNEL_MAP.put(apiToken, channel.id());
        }
    }

    public synchronized static void updateChannel(String apiToken, Channel channel) {
        Channel chan = GLOBAL_GROUP.find(channel.id());
        if (null == chan) {
            addChannel(apiToken, channel);
        } else {
            CHANNEL_MAP.put(apiToken, channel.id());
        }
    }

    public synchronized static void removeChannel(Channel channel) {
        GLOBAL_GROUP.remove(channel);
        Collection<ChannelId> values = CHANNEL_MAP.values();
        values.remove(channel.id());
    }

    public synchronized static Channel findChannel(String apiToken) {
        ChannelId chanId = CHANNEL_MAP.get(apiToken);
        if (null == chanId) {
            return null;
        }

        return GLOBAL_GROUP.find(CHANNEL_MAP.get(apiToken));
    }

    public static void sendToAll(TextWebSocketFrame tws) {
        GLOBAL_GROUP.writeAndFlush(tws);
    }

    public static void sendToSimple(String apiToken, TextWebSocketFrame tws) {
        GLOBAL_GROUP.find(CHANNEL_MAP.get(apiToken)).writeAndFlush(tws);
    }
}
