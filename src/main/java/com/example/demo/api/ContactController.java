package com.example.demo.api;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON_VALUE;

import com.example.demo.service.ContactService;
import com.example.demo.storage.Contact;
import com.example.demo.utils.Jackson2JsonArrayEncoder;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hello/contact")
public class ContactController {

  private final ContactService contactService;

  private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

  final Jackson2JsonArrayEncoder encoder = new Jackson2JsonArrayEncoder("contacts");

  public ContactController(final ContactService contactService) {
    this.contactService = contactService;
  }

  @GetMapping(path = "/1", produces = APPLICATION_JSON_VALUE)
  public Mono<Void> findAll1(
      @NotBlank @RequestParam(name = "nameFilter") final String nameFilter,
      final ServerHttpResponse response) {

    logger.debug("Trying to load all contacts that match with: {} pattern", nameFilter);

    try {
      final Pattern filter = Pattern.compile(nameFilter);

      //TODO: handle exceptions
      final Flux<DataBuffer> body = encoder.encode(
          contactService.getAll(filter),
          response.bufferFactory(),
          ResolvableType.forClass(Contact.class),
          MimeType.valueOf("application/json"),
          null
      );

      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      return response.writeAndFlushWith(body.map(buffer ->
          Mono.just(buffer).doOnDiscard(PooledDataBuffer.class, PooledDataBuffer::release)));
    } catch (final PatternSyntaxException e) {
      throw new ResponseStatusException(BAD_REQUEST, format("Invalid name filter: %s", nameFilter));
    }
  }

  @GetMapping(produces = APPLICATION_STREAM_JSON_VALUE)
  public Flux<Contact> findAll(
      @NotBlank @RequestParam(name = "nameFilter") final String nameFilter) {

    logger.debug("Trying to load all contacts that match with: {} pattern", nameFilter);

    try {
      final Pattern filter = Pattern.compile(nameFilter);

      return contactService.getAll(filter);
    } catch (final PatternSyntaxException e) {
      throw new ResponseStatusException(BAD_REQUEST, format("Invalid name filter: %s", nameFilter));
    }
  }
}
