package netty.example.handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Владислав on 02.04.2015.
 * Class to handle "/redirect?url=" requests
 */
public class RedirectHandler extends SimpleChannelInboundHandler<HttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        String requestHTTP = req.getUri();
        if (requestHTTP.contains("redirect?url=")) { //check if contains required characters
            String[] requestArray = requestHTTP.split("/?url=");
            String urlToRedirect = requestArray[1];
            if (!urlToRedirect.startsWith("http")) {
                urlToRedirect = "http://" + urlToRedirect;
            }
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            fullHttpResponse.headers().set(LOCATION, urlToRedirect);
            ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.fireChannelRead(req);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
