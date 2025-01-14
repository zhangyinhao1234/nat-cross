package org.zhangyinhao.natc.server.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.common.protocol.ProtocolEnums;
import org.zhangyinhao.natc.server.handler.NatcServerDispatchHandler;
import org.zhangyinhao.natc.server.handler.NatcServerHttpProxyHandler;
import org.zhangyinhao.natc.server.handler.NatcServerProxyHandler;
import org.zhangyinhao.natc.server.handler.NatcServerTcpProxyHandler;
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
public class ProxyServer {
    private ServerBootstrap serverBootstrap;

    private String token;
    private int port;
    private String protocol;

    private NioEventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("proxy-boss-group"));
    private NioEventLoopGroup worker = new NioEventLoopGroup(2, new DefaultThreadFactory("proxy-worker-group"));

    public ProxyServer(String token,int port,String protocol, NatcServerDispatchHandler dispatchHandler) {
        this.token = token;
        this.port = port;
        this.protocol = protocol;
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new ByteArrayDecoder(), new ByteArrayEncoder())
                                .addLast("proxyHandler", getProxyHandler(dispatchHandler));
                    }
                });
    }


    private NatcServerProxyHandler getProxyHandler(NatcServerDispatchHandler dispatchHandler) {
        NatcServerProxyHandler proxyHandler = null;
        if (ProtocolEnums.http.toString().equals(protocol)) {
            proxyHandler = new NatcServerHttpProxyHandler(dispatchHandler);
        }
        if (ProtocolEnums.https.toString().equals(protocol)) {

        }
        if (ProtocolEnums.tcp.toString().equals(protocol)) {
            proxyHandler = new NatcServerTcpProxyHandler(dispatchHandler);
        }
        return proxyHandler;
    }

    public void start() throws InterruptedException {
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        log.info("ProxyServer Start Success Port Is : {} ", port);
        channelFuture.channel().closeFuture().addListener(future -> {
            stop();
        });
    }

    public void stop() {
        log.info("ProxyServer Stop Port Is : {} ", port);
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }

    public String getToken() {
        return token;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }
}
