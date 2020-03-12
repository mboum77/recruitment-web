package fr.d2factory.libraryapp.book;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookDeserializer extends StdDeserializer<Book> {

    public BookDeserializer() {
        this(null);
    }

    public BookDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Book deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        List<Book> books = new ArrayList<>();

        JsonNode node = jp.getCodec().readTree(jp);
        node.forEach(n -> {
                    String title = n.get("title").asText();
                    String author = n.get("author").asText();
                    long isbn = n.get("isbn").longValue();
                    books.add(new Book(
                            n.get("title").asText(), n.get("author").asText(), new ISBN(isbn)));
                }
        );

        return books.get(0);
    }
}