package org.zhangyinhao.natc.server.load;

import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.common.load.AbstractLoadProperties;
import org.zhangyinhao.natc.common.load.Load;
import org.zhangyinhao.natc.server.cache.ServerToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
public class LoadToken extends AbstractLoadProperties {

    public LoadToken() {
        super("conf/token.properties");
    }

    @Override
    public void parse2ConfBean() {
        int index = 0;
        while (true) {
            String tokenKey = "tokens[" + index + "].token";
            String maxConsKey = "tokens[" + index + "].maxCons";
            if (properties.getProperty(tokenKey) == null || properties.getProperty(maxConsKey) == null) {
                break;
            }
            ServerToken.addToken(properties.getProperty(tokenKey), Integer.valueOf(properties.getProperty(maxConsKey)));
            index++;
        }
        if(ServerToken.size() == 0){
            log.error("Server Token Is 0, exit ...");
            System.exit(-1);
        }
    }
}
