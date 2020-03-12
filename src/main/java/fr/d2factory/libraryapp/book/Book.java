package fr.d2factory.libraryapp.book;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;

/**
 * A simple representation of a book
 */
@AllArgsConstructor
@JsonDeserialize(using = BookDeserializer.class)
public class Book {
    String title;
    String author;
    ISBN isbn;

    public Book() {
    }

    public ISBN getISBN() {
        return isbn;
    }

    public long getIsbnCode() {
        return isbn.isbnCode;
    }
}
