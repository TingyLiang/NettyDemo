package pri.robin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;

/**
 * @author liangty1
 * 这个地方将泛型设定为FullHttpRequest
 */
public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private AsciiString contentType = HttpHeaderValues.TEXT_PLAIN;

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        ByteBuf in = (ByteBuf) msg;
////        System.out.println( in.toString());
//        while (in.isReadable()) {
//            System.out.println((char) in.readByte());
//            System.out.flush();
//        }
//        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer("hello".getBytes()));
//        HttpHeaders headers = response.headers();
//        headers.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8")
//                //注意设置内容长度，否则会一直刷新
//                .add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes())
//                .add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//        ctx.write(response);
//    }

    /**
     * 该方法不生效
     *
     * @param channelHandlerContext
     * @param fullHttpRequest
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        System.out.println("class:" + fullHttpRequest.getClass().getName());
        System.out.println(fullHttpRequest.content().toString());
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer("hello".getBytes()));
        HttpHeaders headers = response.headers();
        headers.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8")
                //注意设置内容长度，否则会一直刷新
                .add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes())
                .add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        channelHandlerContext.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel read complete.");
        super.channelReadComplete(ctx);
        //读取完成后输出缓冲流，否则postman会一直刷新
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Exception caught:");
        if (null != cause) {
            cause.printStackTrace();
        } else {
            System.out.println("unknown exception.");
        }
        if (null != ctx) {
            ctx.close();
        }

    }


}
