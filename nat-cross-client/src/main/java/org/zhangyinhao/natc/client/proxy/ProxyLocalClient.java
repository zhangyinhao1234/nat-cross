package org.zhangyinhao.natc.client.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.client.cache.ClientParams;
import org.zhangyinhao.natc.client.handler.NatcClientHandler;
import org.zhangyinhao.natc.client.handler.NatcLocalProxyHandler;
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
public class ProxyLocalClient {
    private Bootstrap bootstrap;
    private EventLoopGroup group = new NioEventLoopGroup();

    private ClientParams.Connect connect;

    public ProxyLocalClient(NatcClientHandler clientHandler) {
        this.connect = clientHandler.getConnect();
        init(clientHandler);
    }

    public void init(NatcClientHandler clientHandler) {
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder())
                                .addLast("proxyHandler", new NatcLocalProxyHandler(clientHandler));
                    }
                });
    }


    public void start() throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(connect.getLocalProxyAddr(), connect.getLocalProxyPort()).sync();
        log.info("ProxyClient Connect Success,LocalProxyAddr : {}, LocalProxyPort : {}", connect.getLocalProxyAddr(), connect.getLocalProxyPort());
        channelFuture.channel().closeFuture().addListener(future -> {
            stop();
        });
    }

    public void stop() {
        group.shutdownGracefully();
    }
}
