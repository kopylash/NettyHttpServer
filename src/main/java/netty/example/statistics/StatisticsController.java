package netty.example.statistics;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

import java.net.InetSocketAddress;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controller that gets info about all request from context
 * Info is stored in thread-safe collections
 * Created by Владислав on 02.04.2015.
 */
public class StatisticsController {
    private static Map<String, IpData> ipMap = new ConcurrentHashMap<>(); //counts every request
    private static Map<String, Integer> urlMap = new ConcurrentHashMap<>(); //counts redirection to each url
    private static Deque<RequestData> logRequestQue = new ConcurrentLinkedDeque<>(); // Deque of last 16 requests
    private static AtomicInteger uniqueIpCount = new AtomicInteger(); // number of the unique IP requests
    private static AtomicInteger count = new AtomicInteger(); // total request number

    public void addToConnectionDeque(ChannelHandlerContext ctx, String url) {
        //getting an instance of ChannelTrafficShapingHandler for bandwidth monitoring
        ChannelTrafficShapingHandler ch = (ChannelTrafficShapingHandler) ctx.channel().pipeline().get("shaping-handler");
        ch.trafficCounter().stop();  //Stop the monitoring process

        if (logRequestQue.size() < 16) {
            logRequestQue.addLast(new RequestData(getClientIp(ctx), url, ch.trafficCounter().cumulativeWrittenBytes(),
                    ch.trafficCounter().cumulativeReadBytes(), ch.trafficCounter().lastWriteThroughput()));
        } else {
            logRequestQue.removeFirst();
            logRequestQue.addLast(new RequestData(getClientIp(ctx),url, ch.trafficCounter().cumulativeWrittenBytes(),
                    ch.trafficCounter().cumulativeReadBytes(), ch.trafficCounter().lastWriteThroughput()));
        }
        ch.trafficCounter().resetCumulativeTime(); //Reset both read and written cumulative bytes counters and the associated time for channel counter
    }

    //atomic incrementation for total connections count
    public void IncreaseCount() {
        count.incrementAndGet();
    }
    //this method is called in order to count every request
    public void addToIpMap(ChannelHandlerContext ctx) {

        String clientIP = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
        synchronized (ipMap){
            if (!ipMap.containsKey(clientIP)) {//if IP is new, put it in the map with default count 1 and current time
                ipMap.put(clientIP, new IpData());
                uniqueIpCount.incrementAndGet();
            } else { // if IP is not new, update time and increment count
                ipMap.get(clientIP).incrementCount();
                ipMap.get(clientIP).updateTime(); // in order to know the time of last request
            }
        }
    }
    //this method is called in RedirectHandler to count redirection to each url
    public static void processRedirectRequest(String redirectedUrl) {
        if (!urlMap.containsKey(redirectedUrl)) {
            urlMap.put(redirectedUrl, 1);
        } else {
            urlMap.put(redirectedUrl, urlMap.get(redirectedUrl) + 1);
        }
    }

    private String getClientIp(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
    }


    public Map<String, IpData> getIpMap() {
        return ipMap;
    }

    public static Deque<RequestData> getLogRequestQue() {
        return logRequestQue;
    }

    public Map<String, Integer> getUrlMap() {
        return urlMap;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public AtomicInteger getUniqueIpCount() {
        return uniqueIpCount;
    }
}
