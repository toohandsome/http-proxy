package io.github.toohandsome.httproxy.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
  import io.netty.channel.ChannelInitializer;
  import io.netty.channel.ChannelPipeline;
  import io.netty.channel.EventLoopGroup;
  import io.netty.channel.nio.NioEventLoopGroup;
  import io.netty.channel.socket.SocketChannel;
  import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
  import io.netty.handler.logging.LoggingHandler;
  import io.netty.handler.ssl.SslContext;
/**
 * @author toohandsome
 */
  public final class DiscardServer {

  
      public void run(int port) throws Exception {
  
          EventLoopGroup bossGroup = new NioEventLoopGroup(1);
          EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
          try {
              ServerBootstrap b = new ServerBootstrap();
              b.group(bossGroup, workerGroup)
               .channel(NioServerSocketChannel.class)
               .handler(new LoggingHandler(LogLevel.INFO))
               .childHandler(new ChannelInitializer<SocketChannel>() {
                   @Override
                   public void initChannel(SocketChannel ch) {
                       ChannelPipeline p = ch.pipeline();
                       ByteBuf buf = Unpooled.copiedBuffer("$_httpProxy_$".getBytes());
                       //1MB
                       p.addLast(new DelimiterBasedFrameDecoder(1048576,true,false, buf));
                       p.addLast(new DiscardServerHandler());
                   }
               });
  
              // Bind and start to accept incoming connections.
              ChannelFuture f = b.bind(port).sync();
  
              // Wait until the server socket is closed.
              // In this example, this does not happen, but you can do that to gracefully
              // shut down your server.
              f.channel().closeFuture().sync();
          } finally {
              workerGroup.shutdownGracefully();
              bossGroup.shutdownGracefully();
          }
      }
  }