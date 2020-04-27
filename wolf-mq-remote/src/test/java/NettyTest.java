import com.coke.wolf.mq.remote.netty.NettyRemoteClient;
import com.coke.wolf.mq.remote.netty.NettyRemoteServer;
import com.coke.wolf.mq.remote.RemoteCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/21 11:00 下午
 */
public class NettyTest {

    private NettyRemoteServer nettyRemoteServer;

    private NettyRemoteClient nettyRemoteClient;

    private void print(RemoteCommand msg) {
        int type = msg.getType();
        //int size = msg.getSize();

        byte[] body = msg.getBody();

        String str = new String(body, Charset.forName("UTF-8"));

        System.out.println("receive msg = [ type = " + type + " ,size = " + body.length + ",body = " + str + "]");
    }

    @Test
    public void bind() throws InterruptedException {

        int type = 1;
        String str = "I am wolf mq";
        byte[] body = str.getBytes(Charset.forName("UTF-8"));

        RemoteCommand command = new RemoteCommand(type, body);

        nettyRemoteServer = new NettyRemoteServer(new SimpleChannelInboundHandler<RemoteCommand>() {
            @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {

                print(msg);
                ctx.writeAndFlush(command);
            }
        });

        nettyRemoteServer.bind(8088);

        Thread.sleep(8000);

    }

    @Test
    public void connect() throws IOException, InterruptedException {

        int type = 1;
        String str = "hello wolf mq";

        byte[] body = str.getBytes(Charset.forName("UTF-8"));

        RemoteCommand remoteCommand = new RemoteCommand(type, body);

        nettyRemoteClient = new NettyRemoteClient(new SimpleChannelInboundHandler<RemoteCommand>() {

            @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
                ctx.writeAndFlush(remoteCommand);
            }

            @Override protected void channelRead0(ChannelHandlerContext ctx, RemoteCommand msg) throws Exception {

                print(msg);
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                ctx.channel().close();
            }
        });

        nettyRemoteClient.connect("127.0.0.1", 8088);

        Thread.sleep(5000);
    }
}
