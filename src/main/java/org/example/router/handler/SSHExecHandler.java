package org.example.router.handler;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import org.example.RequestUtils;
import org.example.router.ProviderHandler;

import java.io.IOException;
import java.util.Map;

/**
 * ssh 执行
 */
public class SSHExecHandler implements ProviderHandler {

    @Override
    public String handle(HttpMessage message) {
        HttpRequest request = (HttpRequest) message;
        final Map<String, String> params = RequestUtils.formatParams(request);

        final String host = params.get("host");
        final String port = params.getOrDefault("port", "22");
        final String account = params.getOrDefault("account", "root");
        final String password = params.get("password");

        String cmd[] = {"ssh", account + "@" + host, "-p", port};

        try {

            System.out.println(String.join(" ", cmd));

            final Process exec = Runtime.getRuntime()
                    .exec(cmd);
            exec.destroy();
            return "exec ok.";
        } catch (IOException e) {
            e.printStackTrace();
            return "exec err: " + e.getLocalizedMessage();
        }
    }
}
