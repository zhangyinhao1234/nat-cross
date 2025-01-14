package org.zhangyinhao.natc.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.common.protocol.NatcMsg;
import org.zhangyinhao.natc.server.cache.ServerToken;
import org.zhangyinhao.natc.server.proxy.ProxyServer;

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
    private ProxyServer proxyServer = null;
    private NatcServerProxyHandler proxyHandler;

    private ChannelHandlerContext crossCtx;


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
        }
    }

    public void writeAndFlush(byte[] data) {
        crossCtx.writeAndFlush(NatcMsg.createCrossData(data));
    }


    private void register(ChannelHandlerContext ctx, NatcMsg natcMsg) throws InterruptedException {
        try {
            proxyServer = new ProxyServer(natcMsg.getRequest().getToken(), natcMsg.getRequest().getOpenPort(),
                    natcMsg.getRequest().getProtocol(), this);
            proxyServer.start();
            ctx.writeAndFlush(NatcMsg.registerSuccess());
        } catch (Exception e) {
            log.error("ProxyServer Start Error", e);
            if (proxyServer != null) {
                proxyServer.stop();
            }
            if (e instanceof BindException) {
                ctx.writeAndFlush(NatcMsg.error("ProxyServer Start Error,Because The Port " + natcMsg.getRequest().getOpenPort() + " Is In Use"));
            } else {
                ctx.writeAndFlush(NatcMsg.error("ProxyServer Start Error"));
            }

            ctx.close();
        }
    }

    private void crossData(NatcMsg natcMsg) {
        proxyHandler.writeAndFlush(natcMsg.getCrossData());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.crossCtx = ctx;
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("Channel Inactive, Stop Proxy Server");
        if (proxyServer != null) {
            ServerToken.decrementAndGet(proxyServer.getToken());
            proxyServer.stop();
        }
        super.channelInactive(ctx);
    }

    public void setProxyHandler(NatcServerProxyHandler proxyHandler) {
        this.proxyHandler = proxyHandler;
    }

}
