package org.zhangyinhao.natc.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.common.codec.NatcMsgFrameDecoder;
import org.zhangyinhao.natc.common.codec.NatcMsgFrameEncoder;
import org.zhangyinhao.natc.common.codec.NatcMsgProtocolDecoder;
import org.zhangyinhao.natc.common.codec.NatcMsgProtocolEncoder;
import org.zhangyinhao.natc.server.cache.ServerParams;
import org.zhangyinhao.natc.server.handler.AuthHandler;
import org.zhangyinhao.natc.server.handler.NatcServerDispatchHandler;
import org.zhangyinhao.natc.server.handler.NatcServerIdleCheckHandler;
import org.zhangyinhao.natc.server.net.*;
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
    public void start() throws InterruptedException {
        TcpServer server = new TcpServer();
        server.bind(ServerParams.port, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast("idleCheck", new NatcServerIdleCheckHandler())
                        .addLast("frameDecoder", new NatcMsgFrameDecoder())
                        .addLast("frameEncoder", new NatcMsgFrameEncoder())
                        .addLast("msgProtocolEncoder", new NatcMsgProtocolEncoder())
                        .addLast("msgProtocolDecoder", new NatcMsgProtocolDecoder())
                        .addLast("authHandler", new AuthHandler())

                        .addLast("dispatchHandler", new NatcServerDispatchHandler());

            }
        });
        log.info("Server Start Success Port Is : {}", ServerParams.port);
    }
}
