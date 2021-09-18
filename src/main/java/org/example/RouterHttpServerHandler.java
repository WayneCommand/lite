package org.example;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.example.router.Provider;
import org.example.router.ProviderHandler;
import org.example.router.Router;

import java.util.Objects;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RouterHttpServerHandler extends SimpleChannelInboundHandler<HttpMessage> {

    private HttpRequest request;
    StringBuilder responseData = new StringBuilder();

    private final Router router;

    public RouterHttpServerHandler(Router router) {
        this.router = router;
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) {

        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            final Provider provider = router.match(request.uri());

            if (Objects.isNull(provider)){
                writeResponse(ctx, NOT_FOUND, "url not found.");
                return;
            }

            final ProviderHandler handler = provider.getHandler();

            final String content = handler.handle(msg);

            if (HttpUtil.is100ContinueExpected(request)) {
                writeResponse(ctx);
            }

            writeResponse(ctx, OK, content);
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            responseData.append(RequestUtils.formatBody(httpContent));
            responseData.append(RequestUtils.evaluateDecoderResult(request));

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;
                responseData.append(RequestUtils.prepareLastResponse(request, trailer));
                writeResponse(ctx, OK, responseData.toString());
            }
        }
    }

    private void writeResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }

    private void writeResponse(ChannelHandlerContext ctx, HttpResponseStatus status, String responseData) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(responseData, CharsetUtil.UTF_8));

        httpResponse.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            httpResponse.headers()
                    .setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content()
                            .readableBytes());
            httpResponse.headers()
                    .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(httpResponse);

        if (!keepAlive) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
