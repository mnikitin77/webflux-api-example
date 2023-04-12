package org.mvnikitin.accountdemo.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvnikitin.accountdemo.model.Account;
import org.mvnikitin.accountdemo.model.Transaction;
import org.mvnikitin.accountdemo.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;

@Component
@Slf4j
@AllArgsConstructor
public class AccountHandler {

    private final AccountService service;

    public Mono<ServerResponse> getOne(ServerRequest request) {
        return service.getOne(request.pathVariable("number"))
                .flatMap(account -> ServerResponse.ok().body(Mono.just(account), Account.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAll(), Account.class);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Account.class)
                .flatMap(service::create)
                .flatMap(created -> ServerResponse.created(URI.create(request.uri() + "/" + created.id())).build())
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())));
    }

    public Mono<ServerResponse> modify(ServerRequest request) {
        return request.bodyToMono(Account.class)
                .flatMap(service::modify)
                .flatMap(updated ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Mono.just(updated), Account.class))
                .onErrorResume(e ->Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())));
    }

    public Mono<ServerResponse> deposit(ServerRequest request) {
        return changeAccountAmount(request, true);
    }

    public Mono<ServerResponse> withdraw(ServerRequest request) {
        return changeAccountAmount(request, false);
    }

    public Mono<ServerResponse> history(ServerRequest request) {
        return service.getHistory(request.pathVariable("number"))
                .collectList()
                .flatMap(hystory -> ServerResponse.ok().body(Mono.just(hystory), Transaction.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> close(ServerRequest request) {
        return service.close(request.pathVariable("number"))
                .flatMap(updtedCount -> ServerResponse.ok().body(Mono.just(updtedCount), Integer.class))
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())));
    }

    private Mono<ServerResponse> changeAccountAmount(ServerRequest request, boolean isDeposit) {
        return extractTransactionAmount(request, isDeposit)
                .flatMap(amount ->
                        service.changeAmount(request.pathVariable("number"), amount)
                )
                .flatMap(changed -> ServerResponse.ok().build())
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())));
    }

    private Mono<BigDecimal> extractTransactionAmount(ServerRequest request, boolean isDeposit) {
        return request.bodyToMono(String.class)
                .flatMap(s -> Mono.just(new BigDecimal(s).abs()))
                .flatMap(abs -> Mono.just(isDeposit ? abs : abs.negate()));
    }
}
