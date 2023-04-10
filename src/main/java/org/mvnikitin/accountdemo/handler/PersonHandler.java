package org.mvnikitin.accountdemo.handler;

import lombok.AllArgsConstructor;
import org.mvnikitin.accountdemo.model.Person;
import org.mvnikitin.accountdemo.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@AllArgsConstructor
public class PersonHandler {

    private final PersonService service;

    public Mono<ServerResponse> get(ServerRequest request) {
        return service.getOne(Integer.valueOf(request.pathVariable("id")))
                .flatMap(person -> ServerResponse.ok().body(Mono.just(person), Person.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAll(), Person.class);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Person.class)
                .flatMap(service::crteateOrModify)
                .flatMap(created -> ServerResponse.created(URI.create(request.uri() + "/" + created.id())).build())
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())));
    }

    public Mono<ServerResponse> modify(ServerRequest request) {
        return request.bodyToMono(Person.class)
                .flatMap(service::crteateOrModify)
                .flatMap(updated ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(updated), Person.class))
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        return service.delete(Integer.valueOf(request.pathVariable("id")))
                .flatMap(isDeleted -> {
                    if (isDeleted) {
                        return ServerResponse.noContent().build();
                    } else {
                        return ServerResponse.notFound().build();
                    }
                });
    }
}
