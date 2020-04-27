package com.example.demo.api;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/hello/contact")
public class ContactController {

  private final ContactService contactService;

  private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

  public ContactController(final ContactService contactService) {
    this.contactService = contactService;
  }

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public Flux<DataBuffer> findAll(
      @NotBlank @RequestParam(name = "nameFilter") final String nameFilter) {

    logger.debug("Trying to load all contacts that match with: {} pattern", nameFilter);

    try {
      final Pattern filter = Pattern.compile(nameFilter);

      final Jackson2JsonArrayEncoder encoder = new Jackson2JsonArrayEncoder("contacts");

      //TODO: handle exceptions
      return encoder.encode(contactService.getAll(filter),
          new DefaultDataBufferFactory(),
          ResolvableType.forClass(Contact.class),
          MimeType.valueOf("application/json"),
          null);
    } catch (final PatternSyntaxException e) {
      throw new ResponseStatusException(BAD_REQUEST, format("Invalid name filter: %s", nameFilter));
    }
  }
}
