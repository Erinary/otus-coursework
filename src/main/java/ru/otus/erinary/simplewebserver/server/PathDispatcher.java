package ru.otus.erinary.simplewebserver.server;

import lombok.Data;
import ru.otus.erinary.simplewebserver.handler.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class PathDispatcher {

    private final List<Route> routes;

    public PathDispatcher() {
        this.routes = new ArrayList<>();
    }

    public void addHandler(String path, Handler handler) {
        routes.add(new Route(path, handler));
    }

    public Handler getHandler(String path) {
        Matcher matcher;
        for (Route route : routes) {
            matcher = route.getPathPattern().matcher(path);
            if (matcher.find()) {
                return route.getHandler();
            }
        }
        return null;
    }

    public Map<String, String> getPathParameters(String path) {
        for (Route route : routes) {
            Matcher matcher = route.getPathPattern().matcher(path);
            if (matcher.find()) {
                return route.getParameters().stream().collect(Collectors.toMap(parameter -> parameter, matcher::group));
            }
        }
        return null;
    }

    @Data
    private static class Route {
        private Handler handler;
        private Pattern pathPattern;
        private List<String> parameters;

        Route(String path, Handler handler) {
            this.handler = handler;
            this.parameters = new ArrayList<>();

            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            String[] pathParts = path.split("/");
            StringBuilder builder = new StringBuilder();
            builder.append("^");
            for (String part : pathParts) {
                if (part.startsWith("{") && part.endsWith("}")) {
                    String parameterName = part.substring(1, part.length() - 1);
                    parameters.add(parameterName);
                    builder.append(String.format("/(?<%s>[^/]*)", parameterName));
                } else {
                    builder.append("/").append(part);
                }
            }
            builder.append("$");
            this.pathPattern = Pattern.compile(builder.toString());
        }
    }

}
