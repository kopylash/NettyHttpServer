package netty.example.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import netty.example.statistics.StatisticsController;

import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * Created by Владислав on 02.04.2015.
 * Class to handle /hello request
 * SimpleChannelInboundHandler used, cause it releases all the received messages
 */
public class HelloWorldHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static StatisticsController controller = new StatisticsController();
    private final StringBuilder buffer = new StringBuilder();
    private static ChannelGroup openChannelsSet =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE); // A thread-safe Set that contains open Channels

    //return number of current active channels
    public static int getConnectionsCount() {
        return openChannelsSet.size();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
        openChannelsSet.add(ctx.channel());
        if (httpRequest.getUri().equals("/hello") || httpRequest.getUri().equals("/hello/")) {
            buffer.setLength(0);
            buffer.append("<html>\n ").append("<header><title>HttpServer by Kopylash</title></header>\n ").append(
                    "<body>\n ").append("Hello world!\n ").append("</body>\n ").append("</html>");

            final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                    Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8)));
            response.headers().set(CONTENT_TYPE, "text/html");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            //delay for 10 sec
            //it's better practise to use executor().schedule()
            //than thread.sleep(), cause thread sleep() block whole thread
            //while executor().schedule() doesn't
            ctx.executor().schedule(new Runnable() {
                @Override
                public void run() {
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                }
            }, 10, TimeUnit.SECONDS);
            String url = httpRequest.getUri();
            controller.IncreaseCount();
            controller.addToIpMap(ctx);
            controller.addToConnectionDeque(ctx, url);

        } else {
            ctx.fireChannelRead(httpRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
