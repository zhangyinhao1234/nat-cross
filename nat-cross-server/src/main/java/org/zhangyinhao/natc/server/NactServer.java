package org.zhangyinhao.natc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.common.codec.NatcMsgFrameDecoder;
import org.zhangyinhao.natc.common.codec.NatcMsgFrameEncoder;
import org.zhangyinhao.natc.common.codec.NatcMsgProtocolDecoder;
import org.zhangyinhao.natc.common.codec.NatcMsgProtocolEncoder;
import org.zhangyinhao.natc.server.cache.ServerParams;
import org.zhangyinhao.natc.server.handler.AuthHandler;
import org.zhangyinhao.natc.server.handler.NatcServerDispatchHandler;
import org.zhangyinhao.natc.server.handler.NatcServerIdleCheckHandler;
/**
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
@Slf4j
public class NactServer {

    private ServerBootstrap serverBootstrap;

    private NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("boss-group"));
    private NioEventLoopGroup worker = new NioEventLoopGroup(2, new DefaultThreadFactory("worker-group"));


    public NactServer() {
        serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("idleCheck",new NatcServerIdleCheckHandler())
                                    .addLast("frameDecoder", new NatcMsgFrameDecoder())
                                    .addLast("frameEncoder", new NatcMsgFrameEncoder())
                                    .addLast("msgProtocolEncoder", new NatcMsgProtocolEncoder())
                                    .addLast("msgProtocolDecoder", new NatcMsgProtocolDecoder())
                                    .addLast("authHandler", new AuthHandler())

                                    .addLast("dispatchHandler", new NatcServerDispatchHandler());
                        }
                    });
        } catch (Exception e) {
            log.error("server init error", e);
        }
    }

    public void start() {
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(ServerParams.port).sync();
            log.info("Server Start Success Port Is : {}", ServerParams.port);
            channelFuture.channel().closeFuture().addListener(future -> {
                stop();
            });
        } catch (Exception e) {
            log.error("Server Start Error", e);
            stop();
        }

    }

    public void stop(){
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
