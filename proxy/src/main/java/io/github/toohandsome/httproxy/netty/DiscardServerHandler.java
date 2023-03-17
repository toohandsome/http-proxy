package io.github.toohandsome.httproxy.netty;

import com.alibaba.fastjson.JSON;
import io.github.toohandsome.httproxy.core.TrafficQueueProcess;
import io.github.toohandsome.httproxy.entity.AgentEntity;
import io.github.toohandsome.httproxy.entity.Traffic;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("netty receive : " + body);
        AgentEntity agentEntity = JSON.parseObject(body, AgentEntity.class);
        if (Traffic.class.getSimpleName().equals(agentEntity.getBussType())) {
            Traffic traffic = JSON.parseObject(body, Traffic.class);
            TrafficQueueProcess.trafficQueue.offer(traffic);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}