package org.mvnikitin.accountdemo.repository;

import org.mvnikitin.accountdemo.model.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PersonRepository extends ReactiveCrudRepository<Person, Integer> {

    Mono<Boolean> deletePersonById(Integer id);
}
