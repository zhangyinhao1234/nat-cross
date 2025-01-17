package org.zhangyinhao.natc.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.common.protocol.NatcMsg;

import java.util.Arrays;

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
public abstract class NatcServerProxyHandler extends ChannelInboundHandlerAdapter {
    protected NatcServerDispatchHandler dispatch;
    protected ChannelHandlerContext ctx;

    public NatcServerProxyHandler(NatcServerDispatchHandler dispatchHandler) {
        this.dispatch = dispatchHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("proxy channel active");
        this.ctx = ctx;
        dispatch.getCtx().writeAndFlush(NatcMsg.connect(ctx.channel().id().asLongText()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        dispatch.getCtx().writeAndFlush(NatcMsg.connect(ctx.channel().id().asLongText()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        dispatch.getCtx().writeAndFlush(NatcMsg.createCrossData(data, ctx.channel().id().asLongText()));
    }

}
