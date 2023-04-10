package org.mvnikitin.accountdemo.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvnikitin.accountdemo.model.Account;
import org.mvnikitin.accountdemo.model.Transaction;
import org.mvnikitin.accountdemo.repository.AccountRepository;
import org.mvnikitin.accountdemo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final TransactionalOperator operator;

    public Mono<Account> create(Account account) {
        return accountRepository.save(account)
                .flatMap(newAccount ->
                        transactionRepository
                                .save(newTransaction(newAccount.id(), newAccount.amount()))
                                .flatMap(t -> Mono.just(newAccount)))
                .as(operator::transactional)
                .doOnNext(created -> log.info("Account created: {}", created))
                .doOnError(e -> log.error(e.getMessage()));
    }

    public Mono<Account> getOne(String number) {
        return accountRepository.findByNumber(number);
    }

    public Flux<Account> getAll() {
        return accountRepository.findAll();
    }

    public Mono<Account> modify(Account account) {
        return accountRepository.findById(account.id())
                .doOnNext(stored -> {
                    if (!Objects.equals(stored.amount(), account.amount())) {
                        throw new IllegalArgumentException("Changing the amount value when modifying the account's data is strictly prohibited!");
                    }
                })
                .flatMap(s -> accountRepository.save(account))
                .doOnNext(saved -> log.info("Account modified: {}", saved))
                .doOnError(e -> log.error(e.getMessage()));
    }

    public Mono<Integer> close(String number) {
        return accountRepository.close(number)
                .doOnNext(result -> {
                    if (result > 0) {
                        log.info("Account with number={} closed", number);
                    } else {
                        log.info("Invalid account number={}", number);
                    }
                })
                .doOnError(e -> log.error(e.getMessage()));
    }

    public Mono<Account> changeAmount(String number, BigDecimal changeValue) {
        return accountRepository.findByNumber(number)
                .flatMap(account -> changeAccountsAmount(account, changeValue))
                .flatMap(accountToSave ->
                        accountRepository.save(accountToSave)
                                .flatMap(savedAccount ->
                                        transactionRepository
                                                .save(newTransaction(savedAccount.id(), changeValue))
                                                .flatMap(t -> Mono.just(savedAccount))
                                )
                                .as(operator::transactional)
                )
                .doOnNext(updated -> log.info("Amount on account with number={} is changed by {}", number, changeValue))
                .doOnError(e -> log.error(e.getMessage()));
    }

    public Flux<Transaction> getHistory(String number) {
        return accountRepository.findByNumber(number)
                .flatMapMany(account -> transactionRepository.findAllByAccountIdOrderByCreatedDesc(account.id()));
    }

    private Mono<Account> changeAccountsAmount(Account account, BigDecimal changeValue) {
        return Mono.just(
                new Account(
                        account.id(),
                        account.personId(),
                        account.number(),
                        account.amount().add(changeValue),
                        account.created(),
                        account.closed()
                )
        );
    };

    private Transaction newTransaction(Long accountId, BigDecimal transactionValue) {
        return  new Transaction(null, accountId, transactionValue, null);
    }
}
