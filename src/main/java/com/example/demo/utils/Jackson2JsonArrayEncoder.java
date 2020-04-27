/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.json.Jackson2CodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

//TODO: refactor
public final class Jackson2JsonArrayEncoder {

  private static final byte[] ITEM_SEPARATOR = {','};

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final String collectionName;

  public Jackson2JsonArrayEncoder(final String collectionName) {
    this.collectionName = collectionName;
  }

  public Flux<DataBuffer> encode(Publisher<?> inputStream,
      DataBufferFactory bufferFactory,
      ResolvableType elementType, @Nullable MimeType mimeType,
      @Nullable Map<String, Object> hints) {

    Assert.notNull(inputStream, "'inputStream' must not be null");
    Assert.notNull(bufferFactory, "'bufferFactory' must not be null");
    Assert.notNull(elementType, "'elementType' must not be null");

    try {
      ObjectWriter writer = createObjectWriter(elementType, mimeType, hints);
      ByteArrayBuilder byteBuilder = new ByteArrayBuilder(writer.getFactory()._getBufferRecycler());
      JsonEncoding encoding = getJsonEncoding(mimeType);
      JsonGenerator generator = objectMapper.getFactory().createGenerator(byteBuilder, encoding);
      SequenceWriter sequenceWriter = writer.writeValues(generator);

      return Flux.concat(
          Flux.just(new DefaultDataBufferFactory()
              .wrap(("{\"" + collectionName + "\":[").getBytes(UTF_8))),
          Flux.from(inputStream)
              .skipLast(1)
              .map(v -> encodeStreamingValue(v, bufferFactory, hints, sequenceWriter,
                  byteBuilder, ITEM_SEPARATOR)),
          Flux.from(inputStream)
              .takeLast(1)
              .map(v -> encodeStreamingValue(v, bufferFactory, hints, sequenceWriter,
                  byteBuilder, new byte[]{})),
          Flux.just(new DefaultDataBufferFactory()
              .wrap(("]}").getBytes(UTF_8)))
      );
    } catch (IOException ex) {
      return Flux.error(ex);
    }
  }

  private DataBuffer encodeStreamingValue(Object value, DataBufferFactory bufferFactory,
      @Nullable Map<String, Object> hints,
      SequenceWriter sequenceWriter, ByteArrayBuilder byteArrayBuilder, byte[] separator) {

    try {
      sequenceWriter.write(value);
      sequenceWriter.flush();
    } catch (InvalidDefinitionException ex) {
      throw new CodecException("Type definition error: " + ex.getType(), ex);
    } catch (JsonProcessingException ex) {
      throw new EncodingException("JSON encoding error: " + ex.getOriginalMessage(), ex);
    } catch (IOException ex) {
      throw new IllegalStateException("Unexpected I/O error while writing to byte array builder",
          ex);
    }

    byte[] bytes = byteArrayBuilder.toByteArray();
    byteArrayBuilder.reset();

    int offset;
    int length;
    if (bytes.length > 0 && bytes[0] == ' ') {
      // SequenceWriter writes an unnecessary space in between values
      offset = 1;
      length = bytes.length - 1;
    } else {
      offset = 0;
      length = bytes.length;
    }
    DataBuffer buffer = bufferFactory.allocateBuffer(length + separator.length);
    buffer.write(bytes, offset, length);
    buffer.write(separator);

    return buffer;
  }

  private ObjectWriter createObjectWriter(ResolvableType valueType, @Nullable MimeType mimeType,
      @Nullable Map<String, Object> hints) {

    TypeFactory typeFactory = this.objectMapper.getTypeFactory();
    JavaType javaType = typeFactory
        .constructType(GenericTypeResolver.resolveType(valueType.getType(), (Class) null));
    Class<?> jsonView = (hints != null ? (Class<?>) hints.get(Jackson2CodecSupport.JSON_VIEW_HINT)
        : null);
    ObjectWriter writer = (jsonView != null ?
        objectMapper.writerWithView(jsonView) : objectMapper.writer());

    if (javaType.isContainerType()) {
      writer = writer.forType(javaType);
    }

    return customizeWriter(writer, mimeType, valueType, hints);
  }

  protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType,
      ResolvableType elementType, @Nullable Map<String, Object> hints) {

    return writer;
  }

  protected JsonEncoding getJsonEncoding(@Nullable MimeType mimeType) {
    if (mimeType != null && mimeType.getCharset() != null) {
      Charset charset = mimeType.getCharset();
      for (JsonEncoding encoding : JsonEncoding.values()) {
        if (charset.name().equals(encoding.getJavaName())) {
          return encoding;
        }
      }
    }
    return JsonEncoding.UTF8;
  }

}