package org.zhangyinhao.natc.common.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NatcMsgResponse {
    private boolean success;
    private String remark;

    public NatcMsgResponse(boolean success, String remark) {
        this.success = success;
        this.remark = remark;
    }
}
