package netty.example;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import netty.example.handlers.HelloWorldHandler;
import netty.example.handlers.RedirectHandler;
import netty.example.handlers.StatusHandler;

/**
 * Class for ChannelPipeline initialization
 * Created by Владислав on 02.04.2015.
 */
public class HttpServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();  //store the list of channel handlers (Intercepting Filter)
        //to monitor bandwidth and set a delay between each check
        pipeline.addLast("shaping-handler", new ChannelTrafficShapingHandler(1000));
        pipeline.addLast("server-codec", new HttpServerCodec());
        pipeline.addLast("hello-handler", new HelloWorldHandler());
        pipeline.addLast("redirect-handler", new RedirectHandler());
        pipeline.addLast("status-handler", new StatusHandler());
    }
}
