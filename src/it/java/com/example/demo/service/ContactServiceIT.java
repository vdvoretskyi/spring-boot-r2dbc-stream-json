package com.example.demo.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import com.example.demo.AbstractIntegrationIT;
import com.example.demo.storage.Contact;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ContactServiceIT extends AbstractIntegrationIT {

  @Autowired
  ContactService contactService;

  @Test
  void getAllWithWildCardShouldReturnAllRecords() {
    final List<String> names = contactService
        .getAll(Pattern.compile(".*"))
        .map(Contact::getName)
        .collectList()
        .block();
    assertThat(names, containsInAnyOrder("one", "two"));
  }

  @Test
  void getAllShouldReturnNoRecords() {
    final List<String> contacts = contactService
        .getAll(Pattern.compile("NotExisting"))
        .map(Contact::getName)
        .collectList()
        .block();
    assertThat(contacts, is(empty()));
  }

}
