package org.zhangyinhao.natc.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.client.cache.ClientParams;
import org.zhangyinhao.natc.client.net.KeepConnection;
import org.zhangyinhao.natc.client.net.TcpConnection;
import org.zhangyinhao.natc.common.protocol.NatcMsg;
import org.zhangyinhao.natc.common.protocol.ProtocolEnums;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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
public class NatcClientHandler extends ChannelInboundHandlerAdapter {
    private ClientParams.Connect connect;
    private ChannelHandlerContext ctx;

    private ConcurrentHashMap<String, NatcLocalProxyHandler> channelHandlerMap = new ConcurrentHashMap<>();
    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    public NatcClientHandler(ClientParams.Connect connect) {
        this.connect = connect;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        register(ctx);
        this.ctx = ctx;
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.close();
        log.info("ClientHandler channel inactive, close channelGroup");
        KeepConnection.addLoseConnect(connect);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NatcMsg natcMsg = (NatcMsg) msg;
        log.debug("client receive msg: action is : {}", natcMsg.getAction());
        switch (natcMsg.getAction()) {
            case REGISTER_RESULT:
                registerResult(ctx, natcMsg);
                break;
            case CONNECT:
                connect(ctx, natcMsg);
                break;
            case DATA:
                crossData(natcMsg);
                break;
            case ERROR:
                error(ctx,natcMsg);
                break;
            case DISCONNECT:
                disconnect(ctx, natcMsg);
                break;
        }
        super.channelRead(ctx, msg);
    }

    private void register(ChannelHandlerContext ctx) {
        NatcMsg natcMsg = NatcMsg.registerReq(connect.getServerProxyPort(), connect.getServerProxyProtocol(), connect.getServerToken());
        ctx.writeAndFlush(natcMsg);
    }

    private void registerResult(ChannelHandlerContext ctx, NatcMsg natcMsg) {
        if (natcMsg.getResponse().isSuccess()) {
            if (ProtocolEnums.http.toString().equals(connect.getServerProxyProtocol())) {
                log.info("Register Success, WebSite Is http://{}:{}   ", connect.getServerAddr(), connect.getServerProxyPort());
            } else {
                log.info("Register Success, Use {}:{} Connect You Server ", connect.getServerAddr(), connect.getServerProxyPort());
            }
        } else {
            log.error("Register Fail,Server Close Channel By : {}", natcMsg.getResponse().getRemark());
            ctx.close();
        }
    }

    private void connect(ChannelHandlerContext ctx, NatcMsg natcMsg) throws IOException, InterruptedException {
        String serverProxyChannelId = natcMsg.getResponse().getChannelId();
        try {
            NatcClientHandler thisHandler = this;
            TcpConnection localConnection = new TcpConnection();
            localConnection.connect(connect.getLocalProxyAddr(), connect.getLocalProxyPort(), new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    NatcLocalProxyHandler localProxyHandler = new NatcLocalProxyHandler(thisHandler, serverProxyChannelId);
                    ch.pipeline().addLast(
                            new ByteArrayDecoder(),
                            new ByteArrayEncoder(),
                            localProxyHandler);
                    channelHandlerMap.put(serverProxyChannelId, localProxyHandler);
                    channelGroup.add(ch);
                }
            });
            //log.info("Start Local Proxy Server Success ,Addr:{} Port:{}", connect.getLocalProxyAddr(), connect.getLocalProxyPort());
        } catch (Exception e) {
            ctx.writeAndFlush(NatcMsg.disconnect(serverProxyChannelId));
            channelHandlerMap.remove(serverProxyChannelId);
            throw e;
        }
    }

    private void crossData(NatcMsg natcMsg) {
        String channelId = natcMsg.getResponse().getChannelId();
        NatcLocalProxyHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            ChannelHandlerContext ctx = handler.getCtx();
            ctx.writeAndFlush(natcMsg.getCrossData());
        }
    }

    private void disconnect(ChannelHandlerContext ctx, NatcMsg natcMsg){
        String channelId = natcMsg.getResponse().getChannelId();
        NatcLocalProxyHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            handler.getCtx().close();
            channelHandlerMap.remove(channelId);
        }
    }


    private void error(ChannelHandlerContext ctx,NatcMsg natcMsg) {
        log.error("Server Close Channel By : {}", natcMsg.getResponse().getRemark());
        ctx.close();
    }


    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public ClientParams.Connect getConnect() {
        return connect;
    }
}
