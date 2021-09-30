package org.example.router.handler;

import io.netty.handler.codec.http.HttpMessage;
import org.example.router.ProviderHandler;

/**
 * for test
 */
public class HelloHandler implements ProviderHandler {
    @Override
    public String handle(HttpMessage message) {


        return "hello";
    }
}
