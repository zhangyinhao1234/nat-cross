package org.zhangyinhao.natc.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.common.protocol.NatcActionEnums;
import org.zhangyinhao.natc.common.protocol.NatcMsg;
import org.zhangyinhao.natc.server.cache.ServerToken;

import java.util.concurrent.atomic.AtomicInteger;

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
public class AuthHandler extends SimpleChannelInboundHandler<NatcMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NatcMsg natcMsg) throws Exception {
        try {
            if (natcMsg.getAction().equals(NatcActionEnums.REGISTER)) {
                if (ServerToken.contains(natcMsg.getRequest().getToken())) {
                    checkMaxCons(ctx, natcMsg);
                } else {
                    log.error("Token NotFind");
                    ctx.writeAndFlush(NatcMsg.error("Token NotFind"));
                    ctx.close();
                }
            } else {
                log.error("Expect First Msg Is Auth");
                ctx.writeAndFlush(NatcMsg.error("Expect First Msg Is Auth"));
                ctx.close();
            }
        } catch (Exception e) {
            log.error("Server Error , Token Valid Error", e);
            ctx.writeAndFlush(NatcMsg.error("Token Valid Error"));
            ctx.close();
        } finally {
            ctx.pipeline().remove(this);
        }
    }

    private void checkMaxCons(ChannelHandlerContext ctx, NatcMsg natcMsg) {
        String token = natcMsg.getRequest().getToken();
        boolean full = ServerToken.isFull(token);
        if (full) {
            log.error("Token Cons Is Gone");
            ctx.writeAndFlush(NatcMsg.error("Token Cons Is Gone"));
            ServerToken.decrementAndGet(token);
            ctx.close();
        } else {
            log.info("Token Valid Success");
            ctx.fireChannelRead(natcMsg);
        }
    }
}
