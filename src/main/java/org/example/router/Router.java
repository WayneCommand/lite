package org.example.router;

import org.example.matcher.AntPathMatcher;
import org.example.matcher.PathMatcher;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Router {

    public static Map<String, Provider> routeTable;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    public Router() {
        initial();
    }


    void initial() {
        final List<Provider> providers = List.of(new Provider("/hello", msg -> {
            return "hello....";
        }));

        routeTable = providers.stream()
                .collect(Collectors.toMap(Provider::getPath, Function.identity()));

    }

    /**
     * 根据路径匹配服务
     *
     * @param path      路径
     * @return          服务提供
     */
    public Provider match(String path) {
        final Set<Map.Entry<String, Provider>> entries = routeTable.entrySet();
        for (Map.Entry<String, Provider> entry : entries) {
            if (pathMatcher.match(entry.getKey(), path))
                return entry.getValue();
        }
        return null;
    }


}
