package org.mvnikitin.accountdemo.configuration;

import org.mvnikitin.accountdemo.handler.AccountHandler;
import org.mvnikitin.accountdemo.handler.PersonHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration(proxyBeanMethods = false)
public class Routers {

    private final String API_PATH = "/api/v1";

    @Bean
    public RouterFunction<ServerResponse> personRoutes(PersonHandler personHandler) {
        var routes = RouterFunctions
                .route(GET("/person/{id:[0-9]+}"), personHandler::get)
                .andRoute(GET("/persons"), personHandler::getAll)
                .andRoute(POST("/person").and(accept(MediaType.APPLICATION_JSON)), personHandler::create)
                .andRoute(PUT("/person").and(accept(MediaType.APPLICATION_JSON)), personHandler::modify)
                .andRoute(DELETE("/person/{id:[0-9]+}"), personHandler::delete);

        return RouterFunctions.nest(path(API_PATH), routes);
    }

    @Bean
    public RouterFunction<ServerResponse> accountRoutes(AccountHandler accountHandler) {
        var routes = RouterFunctions
                .route(GET("/account/{number:[0-9]{20}}"), accountHandler::getOne)
                .andRoute(GET("/accounts"), accountHandler::getAll)
                .andRoute(POST("/account").and(accept(MediaType.APPLICATION_JSON)), accountHandler::create)
                .andRoute(PUT("/account").and(accept(MediaType.APPLICATION_JSON)), accountHandler::modify)
                .andRoute(DELETE("/account/{number:[0-9]{20}}"), accountHandler::close)
                .andRoute(GET("/account/{number:[0-9]{20}}/history"), accountHandler::history)
                .andRoute(PUT("/account/{number:[0-9]{20}}/deposit"), accountHandler::deposit)
                .andRoute(PUT("/account/{number:[0-9]{20}}/withdraw"), accountHandler::withdraw);

        return RouterFunctions.nest(path(API_PATH), routes);
    }
}
