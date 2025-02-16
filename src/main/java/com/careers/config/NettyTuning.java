package com.careers.config;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.LoopResources;

@Configuration
public class NettyTuning {

    @Bean
    public NettyReactiveWebServerFactory nettyFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(builder -> 
            builder.option(ChannelOption.SO_BACKLOG, 1024)
                  .childOption(ChannelOption.TCP_NODELAY, true)
                  .childOption(ChannelOption.SO_KEEPALIVE, true)
                  .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        );
        return factory;
    }

    @Bean
    public LoopResources loopResources() {
        return LoopResources.create("reactor-netty", 1, 8, true);
    }
}