package netty.example.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * Created by Владислав on 02.04.2015.
 */
public class HelloWorldHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
        if (httpRequest.getUri().equals("/hello") || httpRequest.getUri().equals("/hello/")) {
            String responseContent = "<html>\n " + "<header><title>HttpServer by Kopylash</title></header>\n " +
                    "<body>\n " + "Hello world\n " + "</body>\n " + "</html>";

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                    Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(responseContent, CharsetUtil.UTF_8)));
            response.headers().set(CONTENT_TYPE, "text/html");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
        ctx.fireChannelRead(httpRequest);
    }
    }
}
