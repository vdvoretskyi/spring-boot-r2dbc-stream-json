package com.example.demo.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.storage.Contact;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

class Jackson2JsonArrayEncoderTest {

  @Test
  void encodeMultipleFlux() {
    final Flux<Contact> contacts = Flux.just(
        new Contact(1L, "First"),
        new Contact(2L, "Second")
    );
    final String encoded = encode(contacts);

    assertEquals("{\"contacts\":[{\"id\":1,\"name\":\"First\"},{\"id\":2,\"name\":\"Second\"}]}",
        encoded);
  }

  @Test
  void encodeSingleFlux() {
    final Flux<Contact> contacts = Flux.just(
        new Contact(1L, "First")
    );
    final String encoded = encode(contacts);

    assertEquals("{\"contacts\":[{\"id\":1,\"name\":\"First\"}]}",
        encoded);
  }

  @Test
  void encodeEmptyFlux() {
    final Flux<Contact> contacts = Flux.empty();
    final String encoded = encode(contacts);

    assertEquals("{\"contacts\":[]}", encoded);
  }

  private static String encode(final Flux<Contact> contacts) {
    final Jackson2JsonArrayEncoder encoder = new Jackson2JsonArrayEncoder("contacts");

    final Flux<DataBuffer> dataBufferFlux = encoder.encode(
        contacts, new DefaultDataBufferFactory(),
        ResolvableType.forClass(Contact.class),
        MimeType.valueOf("application/json"),
        null);

    return DataBufferUtils
        .join(dataBufferFlux)
        .block()
        .toString(StandardCharsets.UTF_8);
  }
}