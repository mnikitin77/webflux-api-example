package org.mvnikitin.accountdemo.repository;

import org.mvnikitin.accountdemo.model.Account;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {

    Mono<Account> findByNumberAndClosedIsNull(String number);

    Mono<Account> findByNumber(String number);

    @Modifying
    @Query("UPDATE account SET closed = LOCALTIMESTAMP where number = :number AND closed IS NULL")
    Mono<Integer> close(String number);
}
