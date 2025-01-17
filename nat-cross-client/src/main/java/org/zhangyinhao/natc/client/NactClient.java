package org.zhangyinhao.natc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.client.cache.ClientParams;
import org.zhangyinhao.natc.client.handler.KeepaliveHandler;
import org.zhangyinhao.natc.client.handler.NatcClientHandler;
import org.zhangyinhao.natc.client.handler.NatcClientIdleCheckHandler;
import org.zhangyinhao.natc.client.handler.NatcLocalProxyHandler;
import org.zhangyinhao.natc.client.net.TcpConnection;
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
    private ClientParams.Connect connect;

    public NactClient(ClientParams.Connect connect) {
        this.connect = connect;
    }

    public void start() {
        TcpConnection localConnection = new TcpConnection();
        KeepaliveHandler keepaliveHandler = new KeepaliveHandler();
        try {
            localConnection.connect(connect.getServerAddr(), connect.getServerPort(), new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline
                            .addLast("clientIdleCheck", new NatcClientIdleCheckHandler())
                            .addLast("frameDecoder", new NatcMsgFrameDecoder())
                            .addLast("frameEncoder", new NatcMsgFrameEncoder())
                            .addLast("msgProtocolEncoder", new NatcMsgProtocolEncoder())
                            .addLast("msgProtocolDecoder", new NatcMsgProtocolDecoder())
                            .addLast("keepalive", keepaliveHandler)
                            .addLast("clientHandler", new NatcClientHandler(connect));
                }
            });
            //log.info("Client Connect Server Success,ServerAddr : {}, ServerPort : {}", connect.getServerAddr(), connect.getServerPort());
        } catch (Exception e) {
            log.error("Client Connect Error", e);
        }

    }
}
