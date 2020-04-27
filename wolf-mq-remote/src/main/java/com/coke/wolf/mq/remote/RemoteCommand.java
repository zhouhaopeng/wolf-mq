package com.coke.wolf.mq.remote;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/21 10:02 下午
 */
public class RemoteCommand {

    private static final int FIX_SIZE = 12;

    private static final AtomicInteger uniqueId = new AtomicInteger(0);

    private int requestId = uniqueId.getAndIncrement();

    private int type;

    private byte[] body;

    public RemoteCommand() {
    }

    public RemoteCommand(int type, byte[] body) {
        this.type = type;
        this.body = body;
    }

    public RemoteCommand(int requestId, int type, byte[] body) {
        this.requestId = requestId;
        this.type = type;
        this.body = body;
    }

    public RemoteCommand(RemoteCommand remoteCommand) {
        this(remoteCommand.getType(), remoteCommand.getBody());
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public static byte[] encode(RemoteCommand command) {
        ByteBuf byteBuf = Unpooled.buffer(FIX_SIZE + command.getBody().length);
        byteBuf.writeInt(command.getRequestId());
        byteBuf.writeInt(command.getType());
        byteBuf.writeInt(command.getBody().length);
        byteBuf.writeBytes(command.getBody());

        return byteBuf.array();
    }

    public static RemoteCommand decode(ByteBuf byteBuf) {
        RemoteCommand command = new RemoteCommand();

        int requestId = byteBuf.readInt();
        int type = byteBuf.readInt();
        int size = byteBuf.readInt();
        byte[] body = new byte[size];
        byteBuf.readBytes(body);

        command.setRequestId(requestId);
        command.setType(type);
        command.setBody(body);

        return command;
    }

    public static RemoteCommand build(int type, byte[] body) {
        return new RemoteCommand(type, body);
    }

    public static RemoteCommand build(int requestId,int type, byte[] body) {
        return new RemoteCommand(requestId,type, body);
    }

    public static RemoteCommand copy(RemoteCommand remoteCommand) {
        return new RemoteCommand(remoteCommand);
    }
}
