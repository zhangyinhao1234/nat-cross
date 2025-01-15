package org.zhangyinhao.natc.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.client.cache.ClientParams;
import org.zhangyinhao.natc.client.proxy.ProxyLocalClient;
import org.zhangyinhao.natc.common.protocol.NatcMsg;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private static ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private ProxyLocalClient proxyLocalClient;

    private ChannelHandlerContext clientProxyCtx;

    private ChannelHandlerContext clientCtx;

    private ClientParams.Connect connect;

    public NatcClientHandler(ClientParams.Connect connect) {
        this.connect = connect;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        register(ctx);
        this.clientCtx = ctx;
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("ClientHandler channel inactive, system exit");
        System.exit(-1);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NatcMsg natcMsg = (NatcMsg) msg;
        log.debug("client receive msg: action is : {}", natcMsg.getAction());
        switch (natcMsg.getAction()) {
            case CONNECT:
                connect(ctx);
                break;
            case DATA:
                crossData(natcMsg);
                break;
            case ERROR:
                error(natcMsg);
                break;
        }
        super.channelRead(ctx, msg);
    }

    public void writeAndFlush(NatcMsg natcMsg) {
        clientCtx.writeAndFlush(natcMsg);
    }

    private void crossData(NatcMsg natcMsg) {
        clientProxyCtx.writeAndFlush(natcMsg.getCrossData());
    }

    private void register(ChannelHandlerContext ctx) {
        log.info("Begin Register,ServerProxyPort : {}", connect.getServerProxyPort());
        NatcMsg natcMsg = NatcMsg.registerReq(connect.getServerProxyPort(),connect.getServerProxyProtocol(), connect.getServerToken());
        ctx.writeAndFlush(natcMsg);
    }

    private void error(NatcMsg natcMsg) {
        log.error("Server Close Channel By : {}", natcMsg.getResponse().getRemark());
    }


    private void connect(ChannelHandlerContext ctx) {
        proxyLocalClient = new ProxyLocalClient(this);
        try {
            proxyLocalClient.start();
            log.info("ProxyClient Connect Success,LocalProxyAddr : {}, LocalProxyPort : {}", connect.getLocalProxyAddr(), connect.getLocalProxyPort());
            ctx.writeAndFlush(NatcMsg.connectSuccess());
        } catch (Exception e) {
            log.error("ProxyLocalClient Run Error, Close Channel", e);
            proxyLocalClient.stop();
            ctx.writeAndFlush(NatcMsg.error("ProxyLocalClient Run Error"));
            ctx.close();
        }
    }

    public void restartProxy() {
        proxyLocalClient = new ProxyLocalClient(this);
        try {
            proxyLocalClient.start();
        } catch (Exception e) {
            log.error("RestartProxy Error,Try Again After 10S", e);
            proxyLocalClient.stop();
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                restartProxy();
            }, 5, 5, TimeUnit.SECONDS);
        }
    }

    public void setClientProxyCtx(ChannelHandlerContext clientProxyCtx) {
        this.clientProxyCtx = clientProxyCtx;
    }

    public ClientParams.Connect getConnect() {
        return connect;
    }
}
