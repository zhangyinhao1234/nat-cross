package org.zhangyinhao.natc.client.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
public class ClientParams {

    public static List<Connect> connects = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Connect {

        /**
         * 服务端地址
         */
        private String serverAddr = "154.224.13.32";
        /**
         * 服务端端口
         */
        private int serverPort = 10060;
        /**
         * 认证token
         */
        private String serverToken = "123456";
        /**
         * 服务端开放的代理端口
         */
        private int serverProxyPort = 35001;

        /**
         * 服务端代理协议
         */
        private String serverProxyProtocol = "http";

        /**
         * 本地代理端口
         */
        private String localProxyAddr = "localhost";
        /**
         * 本地代理端口
         */
        private int localProxyPort = 8080;
    }
}
