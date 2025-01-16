package org.zhangyinhao.natc.common.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NatcMsgResponse {
    private boolean success;
    private String remark;
    private String channelId;

    public NatcMsgResponse(boolean success, String remark) {
        this.success = success;
        this.remark = remark;
    }

    public NatcMsgResponse(String channelId) {
        this.channelId = channelId;
        this.success = true;
    }

    public NatcMsgResponse(boolean success, String remark, String channelId) {
        this.success = success;
        this.remark = remark;
        this.channelId = channelId;
    }
}
