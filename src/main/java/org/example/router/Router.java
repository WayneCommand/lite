package org.example.router;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Router {

    public static Map<String, Provider> routeTable;

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
        return routeTable.getOrDefault(path, null);
    }


}
