package org.zhangyinhao.natc.common.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import org.zhangyinhao.natc.common.util.JsonUtil;

/**
 * 数据通行协议
 */
@Data
public class NatcMsg {

    private NatcActionEnums action;

    private NatcMsgRequest request;

    private NatcMsgResponse response;

    private byte[] crossData;

    public void encode(ByteBuf byteBuf) {
        byteBuf.writeInt(action.getAction());
        if (request == null) {
            byteBuf.writeInt(0);
        } else {
            String json = JsonUtil.toJson(request);
            byte[] bytes = json.getBytes();
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }

        if (response == null) {
            byteBuf.writeInt(0);
        } else {
            String json = JsonUtil.toJson(response);
            byte[] bytes = json.getBytes();
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
        if (crossData == null) {
            byteBuf.writeInt(0);
        } else {
            byteBuf.writeInt(crossData.length);
            byteBuf.writeBytes(crossData);
        }
    }


    public void decode(ByteBuf byteBuf) {
        int actionVal = byteBuf.readInt();
        this.action = NatcActionEnums.getAction(actionVal);
        int reqLen = byteBuf.readInt();
        if (reqLen > 0) {
            byte[] bytes = new byte[reqLen];
            byteBuf.readBytes(bytes);
            this.request = JsonUtil.fromJson(new String(bytes), NatcMsgRequest.class);
        }
        int respLen = byteBuf.readInt();
        if (respLen > 0) {
            byte[] bytes = new byte[respLen];
            byteBuf.readBytes(bytes);
            this.response = JsonUtil.fromJson(new String(bytes), NatcMsgResponse.class);
        }
        int crossDataLen = byteBuf.readInt();
        if (crossDataLen > 0) {
            byte[] bytes = new byte[crossDataLen];
            byteBuf.readBytes(bytes);
            this.crossData = bytes;
        }
    }


    public static NatcMsg keepalive() {
        return create(NatcActionEnums.HEARTBEAT, null, null);
    }


    public static NatcMsg registerReq(int openPort, String protocol, String token) {
        return createRequest(NatcActionEnums.REGISTER, new NatcMsgRequest(openPort, protocol, token));
    }

    public static NatcMsg registerSuccess() {
        return createResponse(NatcActionEnums.CONNECT, new NatcMsgResponse(true, null));
    }

    public static NatcMsg connectSuccess() {
        return createResponse(NatcActionEnums.CONNECT, new NatcMsgResponse(true, null));
    }

    public static NatcMsg error(String msg) {
        return createResponse(NatcActionEnums.ERROR, new NatcMsgResponse(false, msg));
    }

    public static NatcMsg disconnect() {
        return create(NatcActionEnums.DISCONNECT, null, null);
    }


    public static NatcMsg none() {
        return create(NatcActionEnums.NONE, null, null);
    }

    public static NatcMsg createCrossData(byte[] crossData) {
        NatcMsg natcMsg = create(NatcActionEnums.DATA, null, null);
        natcMsg.setCrossData(crossData);
        return natcMsg;
    }

    public static NatcMsg createRequest(NatcActionEnums action, NatcMsgRequest request) {
        return create(action, request, null);
    }

    public static NatcMsg createResponse(NatcActionEnums action, NatcMsgResponse response) {
        return create(action, null, response);
    }

    public static NatcMsg create(NatcActionEnums action, NatcMsgRequest request, NatcMsgResponse response) {
        NatcMsg natcMsg = new NatcMsg();
        natcMsg.setAction(action);
        natcMsg.setRequest(request);
        natcMsg.setResponse(response);
        natcMsg.setCrossData(new byte[0]);
        return natcMsg;
    }
}
