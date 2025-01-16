package org.zhangyinhao.natc.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.common.protocol.NatcMsg;
import org.zhangyinhao.natc.common.protocol.NatcMsgRequest;
import org.zhangyinhao.natc.common.protocol.ProtocolEnums;
import org.zhangyinhao.natc.server.net.*;

import java.net.BindException;

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
public class NatcServerDispatchHandler extends SimpleChannelInboundHandler<NatcMsg> {
    protected ChannelHandlerContext ctx;
    private TcpServer proxyServer = new TcpServer();
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NatcMsg natcMsg) throws Exception {
        log.debug("Action Val Is : {}", natcMsg.getAction());
        switch (natcMsg.getAction()) {
            case REGISTER:
                register(ctx, natcMsg);
                break;
            case DATA:
                crossData(natcMsg);
                break;
            case HEARTBEAT:
                break;
            case DISCONNECT:
                disconnect(natcMsg);
                break;
        }
    }

    private void register(ChannelHandlerContext ctx, NatcMsg natcMsg) throws InterruptedException {
        NatcMsgRequest request = natcMsg.getRequest();
        try {
            NatcServerDispatchHandler thisHandler = this;
            proxyServer.bind(request.getOpenPort(), new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new ByteArrayDecoder(),
                            new ByteArrayEncoder(),
                            getProxyHandler(thisHandler, request.getProtocol()));
                    channels.add(ch);
                }
            });
            ctx.writeAndFlush(NatcMsg.registerRes(true,"ProxyServer Start Success"));
        } catch (Exception e) {
            log.error("ProxyServer Start Error", e);
            if (e instanceof BindException) {
                ctx.writeAndFlush(NatcMsg.registerRes(false,"ProxyServer Start Error,Because The Port " + natcMsg.getRequest().getOpenPort() + " Is In Use"));
            } else {
                ctx.writeAndFlush(NatcMsg.registerRes(false,"ProxyServer Start Error"));
            }
        }
    }
    private NatcServerProxyHandler getProxyHandler(NatcServerDispatchHandler dispatchHandler,String protocol) {
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

    private void crossData(NatcMsg natcMsg) {
        String channelId = natcMsg.getRequest().getChannelId();
        channels.writeAndFlush(natcMsg.getCrossData(), channel -> channel.id().asLongText().equals(channelId));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    private void disconnect(NatcMsg natcMsg) {
        String channelId = natcMsg.getRequest().getChannelId();
        channels.close(channel -> channel.id().asLongText().equals(channelId));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("Channel Inactive, Stop Proxy Server");
        proxyServer.close();
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }
}
