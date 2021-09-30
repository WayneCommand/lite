package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestUtils {

    public static Map<String, String> formatParams(HttpRequest request) {
        Map<String, String> paramsMap = new HashMap<>();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            for (Map.Entry<String, List<String>> p : params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                for (String val : vals) {
                    paramsMap.put(key, val);
                }
            }
        }
        return paramsMap;
    }

    public static StringBuilder formatBody(HttpContent httpContent) {
        StringBuilder responseData = new StringBuilder();
        ByteBuf content = httpContent.content();
        if (content.isReadable()) {
            responseData.append(content.toString(CharsetUtil.UTF_8)
                    .toUpperCase());
            responseData.append("\r\n");
        }
        return responseData;
    }

    public static StringBuilder evaluateDecoderResult(HttpObject o) {
        StringBuilder responseData = new StringBuilder();
        DecoderResult result = o.decoderResult();

        if (!result.isSuccess()) {
            responseData.append("..Decoder Failure: ");
            responseData.append(result.cause());
            responseData.append("\r\n");
        }

        return responseData;
    }

    public static StringBuilder prepareLastResponse(HttpRequest request, LastHttpContent trailer) {
        StringBuilder responseData = new StringBuilder();
        responseData.append("Good Bye!\r\n");

        if (!trailer.trailingHeaders()
                .isEmpty()) {
            responseData.append("\r\n");
            for (CharSequence name : trailer.trailingHeaders()
                    .names()) {
                for (CharSequence value : trailer.trailingHeaders()
                        .getAll(name)) {
                    responseData.append("P.S. Trailing Header: ");
                    responseData.append(name)
                            .append(" = ")
                            .append(value)
                            .append("\r\n");
                }
            }
            responseData.append("\r\n");
        }
        return responseData;
    }
}
