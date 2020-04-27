package com.example.demo.service;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.demo.storage.Contact;
import com.example.demo.storage.ContactRepository;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

  private final ContactRepository contactRepository;

  private ContactService contactService;

  public ContactServiceTest(@Mock final ContactRepository contactRepository) {
    this.contactRepository = contactRepository;
  }

  @BeforeEach
  void beforeEach() {
    contactService = new ContactService(contactRepository);
  }

  @Test
  void shouldReturnAllContactsWithWildcard() {
    when(contactRepository.findAll()).thenReturn(Flux.create(sink -> {
      sink.next(new Contact(1L, "First"));
      sink.next(new Contact(2L, "Second"));
      sink.complete();
    }));

    final List<String> names = getNames(contactService.getAll(Pattern.compile(".*")));
    assertThat(names, containsInAnyOrder("First", "Second"));
  }

  @Test
  void shouldReturnSingleContact() {
    when(contactRepository.findAll()).thenReturn(Flux.create(sink -> {
      sink.next(new Contact(1L, "First"));
      sink.next(new Contact(2L, "Second"));
      sink.complete();
    }));

    final List<String> names = getNames(contactService.getAll(Pattern.compile("^F.*")));
//    assertThat(names, is(equalTo(List.of("First"))));
    assertThat(names, is(equalTo(singletonList("First"))));
  }

  @Test
  void noContactsShouldReturnEmptyList() {
    when(contactRepository.findAll()).thenReturn(Flux.just());

    final List<String> names = getNames(contactService.getAll(Pattern.compile(".*")));
    assertThat(names, is(empty()));
  }

  @Test
  void nullFilterShouldThrowException() {
    assertThrows(NullPointerException.class, () -> contactService.getAll(null));
  }

  private static List<String> getNames(final Flux<Contact> contacts) {
    return contacts
        .map(Contact::getName)
        .collectList()
        .block();
  }
}