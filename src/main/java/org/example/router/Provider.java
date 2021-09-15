package org.example.router;

public class Provider {

    private final String path;
    private final ProviderHandler handler;

    public String getPath() {
        return path;
    }

    public ProviderHandler getHandler() {
        return handler;
    }

    public Provider(String path, ProviderHandler handler) {
        this.path = path;
        this.handler = handler;
    }
}
