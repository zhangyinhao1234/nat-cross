package org.zhangyinhao.natc.common.load;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
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
public abstract class AbstractLoadProperties implements Load {
    protected Properties properties = new Properties();
    private String fileName;

    public AbstractLoadProperties(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void load() {
        readProperties();
        parse2ConfBean();
    }

    private void readProperties() {
        try {
            String path = getJarLocation() + "/" + fileName;
            InputStream inputStream = new BufferedInputStream(new FileInputStream(path));
            properties.load(inputStream);
        } catch (Exception e) {
            log.error("LoadProperties Error, Exit...", e);
        }
    }


    private String getJarLocation() {
        String path = AbstractLoadProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path.replace("/nat-cross-common/target/classes/", "");
    }

    public abstract void parse2ConfBean();

}
