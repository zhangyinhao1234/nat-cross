package org.zhangyinhao.natc.client.net;

import lombok.extern.slf4j.Slf4j;
import org.zhangyinhao.natc.client.cache.ClientParams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Slf4j
public class KeepConnection {
    private static Set<ClientParams.Connect> loseConnects = new HashSet<>();
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


    public static void start() {
        for (ClientParams.Connect connect : ClientParams.connects) {
            connect(connect);
        }
        executor.scheduleAtFixedRate(new KeepTask(), 10, 30, TimeUnit.SECONDS);
    }

    private static void connect(ClientParams.Connect connect) {
        new NactClient(connect).start();
    }

    public static void rmLoseConnect(ClientParams.Connect connect) {
        synchronized (connect) {
            loseConnects.remove(connect);
        }
    }

    public static void addLoseConnect(ClientParams.Connect connect) {
        synchronized (connect) {
            loseConnects.add(connect);
        }
        if (loseConnects.size() * 2 >= ClientParams.connects.size()) {
            loseConnects.forEach(c -> {
                connect(c);
            });
        }
    }

    static class KeepTask implements Runnable {
        @Override
        public void run() {
            //log.info("run KeepTask ......");
            for (ClientParams.Connect connect : loseConnects) {
                log.info("run KeepTask reconnect ....... LocalProxyAddr:{},LocalProxyPort:{}", connect.getLocalProxyAddr(), connect.getLocalProxyPort());
                connect(connect);
            }
        }
    }
}
