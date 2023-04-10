package org.mvnikitin.accountdemo.repository;

import org.mvnikitin.accountdemo.model.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {

    Flux<Transaction> findAllByAccountIdOrderByCreatedDesc(Long accountId);
}
