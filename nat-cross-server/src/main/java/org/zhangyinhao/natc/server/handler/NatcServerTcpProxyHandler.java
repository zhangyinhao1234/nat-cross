package org.zhangyinhao.natc.server.handler;

import lombok.extern.slf4j.Slf4j;
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
public class NatcServerTcpProxyHandler extends NatcServerProxyHandler {

    public NatcServerTcpProxyHandler(NatcServerDispatchHandler dispatchHandler) {
        super(dispatchHandler);
    }

    @Override
    public void writeAndFlush(byte[] data) {
        proxyCtx.writeAndFlush(data);
    }
}