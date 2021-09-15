package org.example.router;

import io.netty.handler.codec.http.HttpMessage;

public interface ProviderHandler {

    String handle(HttpMessage message);

}
