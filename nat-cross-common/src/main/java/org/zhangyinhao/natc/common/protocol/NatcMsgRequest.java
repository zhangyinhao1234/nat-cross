package org.zhangyinhao.natc.common.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NatcMsgRequest {
    /**
     * 服务器上需要开放的端口,用于进行代理
     */
    private int openPort;
    /**
     * 代理协议
     */
    private String protocol;
    /**
     * 连接到服务器的令牌
     */
    private String token;



    private String channelId;

    public NatcMsgRequest(int openPort, String protocol, String token) {
        this.openPort = openPort;
        this.protocol = protocol;
        this.token = token;
    }


    public NatcMsgRequest(int openPort, String protocol, String token, String channelId) {
        this.openPort = openPort;
        this.protocol = protocol;
        this.token = token;
        this.channelId = channelId;
    }

    public NatcMsgRequest(String channelId) {
        this.channelId = channelId;
    }
}
