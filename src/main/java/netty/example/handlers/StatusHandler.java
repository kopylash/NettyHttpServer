package netty.example.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import netty.example.statistics.IpData;
import netty.example.statistics.RequestData;
import netty.example.statistics.StatisticsController;

import java.util.Deque;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Class that handles /status request and builds a response with
 * appropriate info.
 * Created by Владислав on 02.04.2015.
 */
public class StatusHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private StringBuilder buffer = new StringBuilder();
    private StatisticsController controller = new StatisticsController();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (req.getUri().equals("/status") || req.getUri().equals("/status/")) {
            String url = req.getUri();
            controller.IncreaseCount();
            controller.addToIpMap(ctx);
            buffer.setLength(0);
            getResponseContent();
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                    Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(buffer.toString(), CharsetUtil.UTF_8)));
            response.headers().set(CONTENT_TYPE, "text/html");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            controller.addToConnectionDeque(ctx, url);
        } else {
            ctx.fireChannelRead(req);
        }
    }

    private void getResponseContent() {
        buffer.append("<html>\n");
        buffer.append("<head>\n");
        buffer.append("<title></title>\n");
        buffer.append("<body>\n");
        buffer.append("<h3> Total requests number:   " + controller.getCount() + "</h3>");
        buffer.append("<h3> Number of unique requests:   " + controller.getUniqueIpCount() + "</h3>");
        buffer.append("<h3> Request counter for each IP</h3>");
        buffer.append("<p><table border=\"2\">\n");
        buffer.append("<tr>\n");
        buffer.append("<th> IP</th>\n");
        buffer.append("<th> Request quantity</th>\n");
        buffer.append("<th> Time of last request</th> \n");
        buffer.append("</tr>\n");

        Map<String, IpData> src_ip = controller.getIpMap();
        for (String key : src_ip.keySet()) {
            buffer.append("<tr>\n");
            buffer.append("<td>" + key + "</td>\n");
            buffer.append("<td>" + src_ip.get(key).getCount() + "</td>\n");
            buffer.append("<td>" + src_ip.get(key).getTime() + "</td>\n");
            buffer.append("</tr>\n");
        }
        buffer.append("</table>  " + "</p>\n");
        buffer.append("<h3>Number of redirections to url</h3>");
        buffer.append("<table border=\"2\">\n");
        buffer.append("<tr>\n");
        buffer.append("<th> URL</th>\n");
        buffer.append("<th> Redirection quantity</th>\n");
        buffer.append("</tr>\n");
        Map<String, Integer> urlMap = controller.getUrlMap();
        for (String key : urlMap.keySet()) {
            buffer.append("<tr>\n");
            buffer.append("<td>" + key + "</td>\n");
            buffer.append("<td>" + urlMap.get(key) + "</td>\n");
            buffer.append("</tr>\n");
        }
        buffer.append("</table>  " + "</p>\n");

        buffer.append("<h3> Number of opened connections:   " + HelloWorldHandler.getConnectionsCount() + "</h3>");
        buffer.append("<h3>Log of the last 16 processed connections</h3>");
        buffer.append("<table border=\"2\">\n");
        buffer.append("<tr>\n");
        buffer.append("<th>src_ip</th>\n");
        buffer.append("<th>URI</th>\n");
        buffer.append("<th>timestamp</th>\n");
        buffer.append("<th>sent_bytes</th>\n");
        buffer.append("<th>received_bytes</th>\n");
        buffer.append("<th>speed (bytes/sec)</th>\n");
        buffer.append("</tr>\n");
        Deque<RequestData> lastConnections = StatisticsController.getLogRequestQue();
        for (RequestData d : lastConnections) {
            buffer.append("<tr>\n");
            buffer.append("<td>" + d.getIp() + "</td>\n");
            buffer.append("<td>" + d.getUrl() + "</td>\n");
            buffer.append("<td>" + d.getTime() + "</td>\n");
            buffer.append("<td>" + d.getSentBytes() + "</td>\n");
            buffer.append("<td>" + d.getReceivedBytes() + "</td>\n");
            buffer.append("<td>" + d.getSpeed() + "</td>\n");
            buffer.append("</tr>\n");
        }
        buffer.append("</table>  ").append("</p>\n").append("</body>\n").append("</html>");
    }

}
