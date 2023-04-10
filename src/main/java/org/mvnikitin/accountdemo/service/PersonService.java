package org.mvnikitin.accountdemo.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvnikitin.accountdemo.model.Person;
import org.mvnikitin.accountdemo.repository.PersonRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class PersonService {

    private final PersonRepository repository;

    public Mono<Person> getOne(Integer id) {
        return repository.findById(id);
    }

    public Flux<Person> getAll() {
        return repository.findAll();
    }

    public Mono<Person> crteateOrModify(Person person) {
        return repository.save(person)
                .doOnNext(saved -> log.info("Person created: {}", saved))
                .doOnError(e -> log.error(e.getMessage()));
    }

    public Mono<Boolean> delete(Integer id) {
        return repository.deletePersonById(id)
                .doOnNext(isDeleted -> {
                    if (isDeleted) {
                        log.info("Person with id={} deleted", id);
                    } else {
                        log.info("Invalid person id={}", id);
                    }
                });
    }
}
