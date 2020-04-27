package com.example.demo.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.example.demo.service.ContactService;
import com.example.demo.storage.Contact;
import java.awt.image.DataBuffer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@WebFluxTest(controllers = ContactController.class)
@Import(ContactService.class)
public class ContactControllerIT {

  @MockBean
  ContactService contactService;

  private static final Logger logger = LoggerFactory.getLogger(ContactControllerIT.class);

  @Autowired
  WebTestClient webClient;

  private static final String MATCH_ALL = ".*";

  @Test
  void findAll() {
    Mockito.when(contactService.getAll(any()))
        .thenReturn(Flux.just(new Contact(1L, "First"), new Contact(2L, "Second")));

    final FluxExchangeResult<DataBuffer> result = webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/hello/contact")
            .queryParam("nameFilter", MATCH_ALL)
            .build())
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(DataBuffer.class);

    assertEquals("{\"contacts\":[{\"id\":1,\"name\":\"First\"},{\"id\":2,\"name\":\"Second\"}]}",
        new String(result.getResponseBodyContent()));
  }

}
