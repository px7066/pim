package com.github.pim.server.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * <p>初始化</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/4/28 11:14
 * @since 1.0
 */
public class ServerInitializer  extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                //11 秒没有向客户端发送消息就发生心跳
                .addLast(new IdleStateHandler(11, 0, 0))
//                .addLast(new ProtobufVarint32FrameDecoder())
//                .addLast(new ProtobufDecoder(new ReqProtocol()))
//                .addLast(new ProtobufVarint32LengthFieldPrepender())
//                .addLast(new ProtobufEncoder())
//                .addLast("decoder", new HttpRequestDecoder())
//                .addLast("encoder", new HttpResponseEncoder())
//                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                .addLast("codec-http", new HttpServerCodec())
                .addLast("aggregator", new HttpObjectAggregator(65536))
                .addLast(new SocketFrameHandler())
                .addLast(new HttpHandler())
                ;

    }
}
