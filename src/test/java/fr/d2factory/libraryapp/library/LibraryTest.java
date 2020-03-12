package fr.d2factory.libraryapp.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookDeserializer;
import fr.d2factory.libraryapp.enumeration.ChargeAmount;
import fr.d2factory.libraryapp.enumeration.DaysLimit;
import fr.d2factory.libraryapp.exception.HasDaysException;
import fr.d2factory.libraryapp.exception.HasLateBooksException;
import fr.d2factory.libraryapp.exception.HasRegistrationDateException;
import fr.d2factory.libraryapp.exception.HasWalletException;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import fr.d2factory.libraryapp.repository.BookRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Do not forget to consult the README.md :)
 */
public class LibraryTest {
    private Library library;
    private BookRepository bookRepository;
    private static List<Book> books;

    float initialWallet = 10.8f;

    @BeforeEach
    void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Book.class, new BookDeserializer());
        mapper.registerModule(module);

        String fileName = "src/test/resources/books.json";
        Path path = Paths.get(fileName);

        /* gson matcher to get the book list*/
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();

            books = gson.fromJson(reader,
                    new TypeToken<List<Book>>() {
                    }.getType());
        }

        initLibrary();
    }

    /**
     * Init library
     */
    private void initLibrary() {
        /* */
        bookRepository = new BookRepository();
        library = new LibraryImpl();
        library.setBookRepository(bookRepository);
        bookRepository.addBooks(books);
    }

    @Test
    public void member_can_borrow_a_book_if_book_is_available() {
        LocalDate date = LocalDate.now();
        Student student = new Student();
        student.setRegistrationDate(date.minusYears(2));
        student.setWallet(initialWallet);
        Book book = books.get(0);

        assertFalse("borrowed book first check", bookRepository.getBorrowedBooks().containsKey(book));

        library.borrowBook(book.getIsbnCode(), student, LocalDate.now());

        assertTrue("borrowed book second check ", bookRepository.getBorrowedBooks().containsKey(book));
    }

    @Test
    void borrowed_book_is_no_longer_available() {
        LocalDate date = LocalDate.now();
        Student student = new Student();
        student.setRegistrationDate(date.minusYears(2));
        student.setWallet(initialWallet);
        Book book = books.get(0);

        assertTrue("available book first check", bookRepository.getAvailableBooks().containsValue(book));

        library.borrowBook(book.getIsbnCode(), student, LocalDate.now());

        assertFalse("available book second check ", bookRepository.getAvailableBooks().containsValue(book));
    }

    @Test
    void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {
        Resident resident = new Resident();
        resident.setWallet(initialWallet);

        resident.payBook(10);

        Assert.assertThat("not matching " + initialWallet, resident.getWallet(), is(initialWallet - 1));
    }

    @Test
    void students_pay_10_cents_the_first_30days() {
        LocalDate date = LocalDate.now();
        Student student = new Student();
        student.setRegistrationDate(date.minusYears(2));
        student.setWallet(initialWallet);

        student.payBook(10);

        Assert.assertThat("not matching 9.8f", student.getWallet(), is(initialWallet - 1));
    }

    @Test
    void students_in_1st_year_are_not_taxed_for_the_first_15days() {
        Student student = new Student();
        student.setRegistrationDate(LocalDate.now());
        student.setWallet(initialWallet);

        IntStream.range(1, 16).forEach(days -> {
                    student.payBook(days);
                    Assert.assertThat("wallet differs from initial  wallet " + days, student.getWallet(), is(initialWallet));
                }
        );
    }

    @Test
    void students_cannot_pay_before_1_day() {
        Student student = new Student();
        student.setRegistrationDate(LocalDate.now());
        student.setWallet(initialWallet);

        IntStream.range(0, 16).forEach(days -> assertThrows(HasDaysException.class, () -> student.payBook(-1*days))
        );
    }

    @Test
    void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days() {
        Resident resident = new Resident();
        resident.setWallet(initialWallet);

        resident.payBook(61);

        float expectedWallet = initialWallet - (DaysLimit.DAYS_LIMIT_60.getLimit() * ChargeAmount.CHARGE_AMOUNT_0_1.getAmount() + 1 * ChargeAmount.CHARGE_AMOUNT_0_2.getAmount());

        Assert.assertThat("not paying 20 after 60 days", resident.getWallet(), is(expectedWallet));
    }

    @Test
    void student_cannot_borrow_book_if_they_have_late_books() {
        Student student = new Student();
        student.setLate(true);

        assertThrows(HasLateBooksException.class, () -> library.borrowBook(books.get(0).getIsbnCode(), student, LocalDate.now()));
    }

    @Test
    void resident_cannot_borrow_book_if_they_have_late_books() {
        Resident resident = new Resident();
        resident.setLate(true);

        assertThrows(HasLateBooksException.class, () -> library.borrowBook(books.get(0).getIsbnCode(), resident, LocalDate.now()));
    }

    @Test
    void resident_cannot_borrow_book_if_the_member_wallet_is_empty() {
        assertThrows(HasWalletException.class, () -> library.borrowBook(books.get(0).getIsbnCode(), new Resident(), LocalDate.now()));
    }

    @Test
    void student_cannot_borrow_book_if_the_student_wallet_is_empty() {
        assertThrows(HasWalletException.class, () -> library.borrowBook(books.get(0).getIsbnCode(), new Student(), LocalDate.now()));
    }

    @Test
    void student_cannot_have_null_registrationDate() {
        Student student = new Student();
        student.setWallet(initialWallet);

        assertThrows(HasRegistrationDateException.class, () -> student.payBook(1));
    }

    @Test
    void student_not_late_when_book_is_returned() {
        Student student = new Student();
        student.setWallet(initialWallet);
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        student.setRegistrationDate(yesterday);

        library.borrowBook(books.get(0).getIsbnCode(), student, yesterday);
        student.setLate(true);

        /* init book borrowed*/
        assertThat("", bookRepository.getBorrowedBooks().get(books.get(0)), is(yesterday));
        library.returnBook(books.get(0), student);

        /* book return and student not late*/
        assertThat("", bookRepository.getBorrowedBooks().get(books.get(0)), is(nullValue()));
        assertFalse("",student.isLate());
    }
}
