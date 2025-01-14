package org.zhangyinhao.natc.server.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
public class ServerToken {
    private static Map<String, Params> tokens = new HashMap<>();

    public static void addToken(String token, int maxCons) {
        tokens.put(token, new Params(token, maxCons));
    }

    public static boolean contains(String token) {
        return tokens.get(token) != null;
    }

    public static Params get(String token) {
        return tokens.get(token);
    }

    public static int incrementAndGet(String token) {
        return tokens.get(token).cons.incrementAndGet();
    }

    public static void decrementAndGet(String token) {
        tokens.get(token).cons.decrementAndGet();
    }

    public static boolean isFull(String token) {
        int i = incrementAndGet(token);
//        log.info("incrementAndGet i : {}",i);
        return i > tokens.get(token).maxCons;
    }

    public static int size() {
        return tokens.size();
    }

    @Getter
    public static class Params {
        private String token;
        private int maxCons;
        private AtomicInteger cons = new AtomicInteger(0);

        public Params(String token, int maxCons) {
            this.token = token;
            this.maxCons = maxCons;
        }
    }


}
