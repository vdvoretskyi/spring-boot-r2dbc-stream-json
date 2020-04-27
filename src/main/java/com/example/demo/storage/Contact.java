package com.example.demo.storage;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("contacts")
public class Contact {

  @Id
  private Long id;
  private String name;

  public Contact() {
  }

  public Contact(final Long id, final String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Contact)) {
      return false;
    }
    Contact contact = (Contact) o;
    return id.equals(contact.id) &&
        name.equals(contact.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    return "Contact{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
