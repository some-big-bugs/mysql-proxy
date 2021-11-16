package com.gg.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyDumpSendClient {

  private int serverPort;

  private String serverIp;

  Bootstrap b = new Bootstrap();

  public NettyDumpSendClient(String ip, int port) {
    this.serverPort = port;
    this.serverIp = ip;
  }

  public void runClient() {
    EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

    try {
      b.group(workerLoopGroup);
      b.channel(NioSocketChannel.class);
      b.remoteAddress(serverIp, serverPort);
      b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

      b.handler(new ChannelInitializer<SocketChannel>() {
        protected void initChannel(SocketChannel ch) throws Exception {
          ch.pipeline().addLast(NettyEchoClientHandler.INSTANCE);
        }
      });
      ChannelFuture f = b.connect();
      f.addListener((ChannelFuture futureListener) ->
      {
        if (futureListener.isSuccess()) {
          log.info("EchoClient客户端连接成功!");

        } else {
          log.info("EchoClient客户端连接失败!");
        }

      });

      f.sync();
      Channel channel = f.channel();

      byte[] bytes = "哈哈哈!".getBytes();
      for (int i = 0; i < 1000; i++) {
        ByteBuf buffer = channel.alloc().buffer();
        buffer.writeBytes(bytes);
        channel.writeAndFlush(buffer);
      }

      ChannelFuture closeFuture = channel.closeFuture();
      closeFuture.sync();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      workerLoopGroup.shutdownGracefully();
    }

  }

  public static void main(String[] args) throws InterruptedException {
    int port = NettyConfig.PORT;
    String ip = NettyConfig.IP;
    new NettyDumpSendClient(ip, port).runClient();
  }
}