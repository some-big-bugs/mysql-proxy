package com.gg.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyEchoServer {

  private final int serverPort;
  ServerBootstrap b = new ServerBootstrap();

  public NettyEchoServer(int port) {
    this.serverPort = port;
  }

  public void runServer() {
    EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

    try {
      b.group(bossLoopGroup, workerLoopGroup);
      b.channel(NioServerSocketChannel.class);
      b.localAddress(serverPort);
      b.option(ChannelOption.SO_KEEPALIVE, true);
      b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
      b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
      b.childHandler(new ChannelInitializer<SocketChannel>() {
        protected void initChannel(SocketChannel ch) throws Exception {
          ch.pipeline().addLast(NettyEchoServerHandler.INSTANCE);
        }
      });
      ChannelFuture channelFuture = b.bind().sync();
      log.info("服务器启动成功，监听端口: {}", channelFuture.channel().localAddress());

      ChannelFuture closeFuture = channelFuture.channel().closeFuture();
      closeFuture.sync();
    } catch (Exception e) {
      log.error("异常", e);
    } finally {
      workerLoopGroup.shutdownGracefully();
      bossLoopGroup.shutdownGracefully();
    }

  }

  public static void main(String[] args) throws InterruptedException {
    int port = NettyConfig.PORT;
    new NettyEchoServer(port).runServer();
  }
}