package org.zhangyinhao.natc.client.load;

import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.client.cache.ClientParams;
import org.zhangyinhao.natc.common.load.AbstractLoadProperties;
import java.util.ArrayList;
import java.util.List;
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
public class LoadProperties extends AbstractLoadProperties {

    public LoadProperties() {
        super("conf/client.properties");
    }

    @Override
    public void parse2ConfBean() {
        int index = 0;
        List<ClientParams.Connect> connects = new ArrayList<>();
        while (true) {
            String serverAddrKey = "connect[" + index + "].server.addr";
            String serverAddr = properties.getProperty(serverAddrKey);
            String serverPortKey = "connect[" + index + "].server.port";
            String serverPortStr = properties.getProperty(serverPortKey);
            String serverTokenKey = "connect[" + index + "].server.token";
            String serverToken = properties.getProperty(serverTokenKey);
            String serverProxyPortKey = "connect[" + index + "].server.proxy.port";
            String serverProxyPortStr = properties.getProperty(serverProxyPortKey);
            String serverProxyProtocolKey = "connect[" + index + "].server.proxy.protocol";
            String serverProxyProtocol = properties.getProperty(serverProxyProtocolKey);
            String localProxyAddrKey = "connect[" + index + "].local.proxy.address";
            String localProxyAddr = properties.getProperty(localProxyAddrKey);
            String localProxyPortKey = "connect[" + index + "].local.proxy.port";
            String localProxyPortStr = properties.getProperty(localProxyPortKey);
            if (serverAddr == null || serverPortStr == null || serverToken == null || serverProxyPortStr == null
                    || serverProxyProtocol == null || localProxyAddr == null || localProxyPortStr == null) {
                break;
            }
            ClientParams.Connect connect = new ClientParams.Connect(serverAddr, Integer.parseInt(serverPortStr), serverToken, Integer.parseInt(serverProxyPortStr),
                    serverProxyProtocol, localProxyAddr, Integer.parseInt(localProxyPortStr));
            connects.add(connect);
            index++;
        }
        ClientParams.connects = connects;
    }


}
