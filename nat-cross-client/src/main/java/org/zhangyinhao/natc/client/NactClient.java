package org.zhangyinhao.natc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.client.cache.ClientParams;
import org.zhangyinhao.natc.client.handler.KeepaliveHandler;
import org.zhangyinhao.natc.client.handler.NatcClientHandler;
import org.zhangyinhao.natc.client.handler.NatcClientIdleCheckHandler;
import org.zhangyinhao.natc.common.codec.NatcMsgFrameDecoder;
import org.zhangyinhao.natc.common.codec.NatcMsgFrameEncoder;
import org.zhangyinhao.natc.common.codec.NatcMsgProtocolDecoder;
import org.zhangyinhao.natc.common.codec.NatcMsgProtocolEncoder;
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
public class NactClient {
    private Bootstrap bootstrap;
    private EventLoopGroup group = new NioEventLoopGroup();

    private ClientParams.Connect connect;
    public NactClient(ClientParams.Connect connect) {
        this.connect = connect;
        bootstrap = new Bootstrap();
        KeepaliveHandler keepaliveHandler = new KeepaliveHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline
                                .addLast("clientIdleCheck",new NatcClientIdleCheckHandler() )
                                .addLast("frameDecoder", new NatcMsgFrameDecoder())
                                .addLast("frameEncoder", new NatcMsgFrameEncoder())
                                .addLast("msgProtocolEncoder", new NatcMsgProtocolEncoder())
                                .addLast("msgProtocolDecoder", new NatcMsgProtocolDecoder())

                                .addLast("keepalive", keepaliveHandler)

                                .addLast("clientHandler", new NatcClientHandler(connect));


                    }
                });

    }

    public void start() {
        try{
            ChannelFuture channelFuture = bootstrap.connect(connect.getServerAddr(), connect.getServerPort()).sync();
            log.info("Client Connect Server Success,ServerAddr : {}, ServerPort : {}", connect.getServerAddr(), connect.getServerPort());
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            group.shutdownGracefully();
            log.error("Client Connect Error",e);
        }
    }
}
