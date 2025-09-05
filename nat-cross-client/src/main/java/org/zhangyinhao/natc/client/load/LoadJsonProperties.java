package org.zhangyinhao.natc.client.load;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.client.cache.ClientParams;
import org.zhangyinhao.natc.common.load.AbstractLoadProperties;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class LoadJsonProperties extends AbstractLoadProperties {

    public LoadJsonProperties() {
        super("conf/client.json");
    }
    private String connectJsonStr = "";



    @Override
    protected void readProperties() {
        readProperties(getJarLocation());
    }

    @Override
    protected void readProperties(String confDir) {
        String confPath = confDir +File.separator+ fileName;
        Path path = Paths.get(confPath);
        try{
            byte[] data = Files.readAllBytes(path);
            connectJsonStr = new String(data, "utf-8");
        }catch (Exception e){
            log.error("LoadJsonProperties Error, Exit...", e);
        }
    }

    @Override
    public void parse2ConfBean() {
        Gson gson = new Gson();
        Type typeOfT = new TypeToken<List<ClientParams.Connect>>() {}.getType();
        ClientParams.connects = gson.fromJson(connectJsonStr, typeOfT);
    }



}
