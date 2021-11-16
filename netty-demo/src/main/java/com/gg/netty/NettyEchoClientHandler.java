package com.gg.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class NettyEchoClientHandler extends ChannelInboundHandlerAdapter {

  public static final NettyEchoClientHandler INSTANCE = new NettyEchoClientHandler();

  private static final Logger logger = LoggerFactory.getLogger(NettyEchoClientHandler.class);

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf in = (ByteBuf) msg;
    int len = in.readableBytes();
    byte[] arr = new byte[len];
    in.getBytes(0, arr);
    logger.info("client received: {}", new String(arr));
    in.release();
  }
}
