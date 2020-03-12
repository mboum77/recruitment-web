package fr.d2factory.libraryapp.repository;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {

    private Map<ISBN, Book> availableBooks = new HashMap<>();
    private Map<Book, LocalDate> borrowedBooks = new HashMap<>();
    private Map<Member, List<Book>> memberBooks = new HashMap<>();

    public void addBooks(List<Book> books) {
        // add to the map of available books
        books.forEach(book -> getAvailableBooks().put(book.getISBN(), book));
    }

    protected static Predicate<Book> isBookWithISBNCode(long isbnCode) {
        return book -> book.getISBN().getIsbnCode() == isbnCode;
    }

    private static Book findBookInBookListWithISBNCode(Set<Book> bookList, long isbnCode) {
        Optional<Book> b;
        b = bookList.stream().filter(isBookWithISBNCode(isbnCode)).findFirst();
        return b.orElse(null);

    }

    public Book findBook(long isbnCode) {
        // find in available books
        Book book = findBookInBookListWithISBNCode(new HashSet<>(getAvailableBooks().values()), isbnCode);

        // find in borrowed books
        return book!=null ? book : findBookInBookListWithISBNCode(getBorrowedBooks().keySet(), isbnCode);
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
        getBorrowedBooks().put(book, borrowedAt);
        getAvailableBooks().remove(book.getISBN());
    }

    /**
     * Retrieve the borrowed book date
     * @param book book for which the date is requested
     * @return LocalDate date when the book has been borrowed
     */
    public LocalDate findBorrowedBookDate(Book book) {
        return getBorrowedBooks().get(book);
    }

    /**
     * Computes the number of days the member kept the book
     * @param book book borrowed
     * @return borrowed  duration
     */
    public int getBorrowedBookDuration(Book book) {
        return Math.toIntExact(DAYS.between(findBorrowedBookDate(book), LocalDate.now()));
    }

    public int countBooks(Member member) {
        List<Book> o = getMemberBooks().get(member);
        return o!=null ? o.size() : 0;
    }

    public void addMemberBook(Member member, Book book, LocalDate borrowedAt) {
        getMemberBooks().putIfAbsent(member, List.of(book));
        saveBookBorrow(book, borrowedAt);
    }

    public void returnBook(Book book, Member member) {
        getBorrowedBooks().remove(book); // book is not borrowed
        getAvailableBooks().put(book.getISBN(), book); // books becomes available

        List<Book> books = getMemberBooks().get(member); // exclude book from member map
        if(books!=null && books.size()>1) {
            getMemberBooks().put(member, books.stream().filter(book1 -> !book1.equals(book)).collect(Collectors.toList()));
        } else if(books!=null && books.contains(book)) {
            getMemberBooks().remove(member);
        }

    }

    public Map<ISBN, Book> getAvailableBooks() {
        return availableBooks;
    }

    public Map<Book, LocalDate> getBorrowedBooks() {
        return borrowedBooks;
    }

    public Map<Member, List<Book>> getMemberBooks() {
        return memberBooks;
    }
}
