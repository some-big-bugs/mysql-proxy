package com.gg.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;


@ChannelHandler.Sharable
@Slf4j
public class NettyEchoServerHandler extends ChannelInboundHandlerAdapter {

  public static final NettyEchoServerHandler INSTANCE = new NettyEchoServerHandler();

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf in = (ByteBuf) msg;
    int len = in.readableBytes();
    byte[] arr = new byte[len];
    in.getBytes(0, arr);
    log.info("server received: " + new String(arr, StandardCharsets.UTF_8));
    ctx.writeAndFlush(msg);
  }
}
