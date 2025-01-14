package org.zhangyinhao.natc.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
/**
 * 连接指令类型枚举
 */
@AllArgsConstructor
@Getter
public enum NatcActionEnums {

    /**
     * 注册
     */
    REGISTER(1),
    /**
     * 连接
     */
    CONNECT(2),
    /**
     * 断开连接
     */
    DISCONNECT(3),
    /**
     * 心跳
     */
    HEARTBEAT(4),
    /**
     * 错误
     */
    ERROR(5),
    /**
     * 数据
     */
    DATA(6),
    NONE(7)
    ;


    private int action;

    public static NatcActionEnums getAction(int val) {
        for (NatcActionEnums action : NatcActionEnums.values()) {
            if (action.getAction() == val) {
                return action;
            }
        }
        return null;
    }
}
