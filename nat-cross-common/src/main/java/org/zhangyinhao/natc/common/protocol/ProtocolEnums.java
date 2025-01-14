package org.zhangyinhao.natc.common.protocol;

import lombok.Getter;

@Getter
public enum ProtocolEnums {
    http,
    https,
    tcp,
    udp,
    icmp,
    other;
}
