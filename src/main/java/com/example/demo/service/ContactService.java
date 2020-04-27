package com.example.demo.service;

import static java.util.Objects.requireNonNull;

import com.example.demo.storage.Contact;
import com.example.demo.storage.ContactRepository;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ContactService {

  private final ContactRepository contactRepository;

  public ContactService(final ContactRepository contactRepository) {
    this.contactRepository = contactRepository;
  }

  /**
   * Retrieve all contact with names that matching with given filter
   *
   * @param filter
   * @return contacts
   */
  public Flux<Contact> getAll(final Pattern filter) {
    requireNonNull(filter, "Filter couldn't be null!");

    return contactRepository.findAll()
        .filter(contact -> filter.matcher(contact.getName()).matches());
  }
}
