package com.example.demo.storage;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ContactRepository extends ReactiveCrudRepository<Contact, Long> {

}
