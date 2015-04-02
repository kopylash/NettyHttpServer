package netty.example;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import netty.example.handlers.HelloWorldHandler;
import netty.example.handlers.RedirectHandler;

/**
 * Class for ChannelPipeline initialization
 * Created by Владислав on 02.04.2015.
 */
public class HttpServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();  //store the list of channel handlers (Intercepting Filter)
        pipeline.addLast("shaping-handler", new ChannelTrafficShapingHandler(1000)); //delay between each check
        pipeline.addLast("server-codec", new HttpServerCodec());
        pipeline.addLast(new HelloWorldHandler());
        pipeline.addLast(new RedirectHandler());
    }
}
