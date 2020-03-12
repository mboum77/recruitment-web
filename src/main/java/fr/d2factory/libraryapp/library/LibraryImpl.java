package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.exception.HasLateBooksException;
import fr.d2factory.libraryapp.exception.HasWalletException;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.repository.BookRepository;

import java.time.LocalDate;

public class LibraryImpl implements Library {

    BookRepository bookRepository;

    @Override
    public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {
        if (member.isLate())
            throw new HasLateBooksException();

        if (member.getWallet()<=0)
            throw new HasWalletException();

        Book book = bookRepository.findBook(isbnCode);
        if (book != null) {
            bookRepository.addMemberBook(member, book, borrowedAt);
        }
        return book;
    }

    @Override
    public void returnBook(Book book, Member member) {
        int borrowedBookDuration = bookRepository.getBorrowedBookDuration(book);

         /*
        Compute the tariff and charge when necessary the member for this returned book
          */
        member.payBook(borrowedBookDuration);

        bookRepository.returnBook(book, member);

        /* considered as late if has other books and the book is returned after the day limit */
        member.setLate(borrowedBookDuration, bookRepository.countBooks(member) > 0);

    }

    /**
     * library bookRepository Accessor
     *
     * @return bookRepository
     */
    @Override
    public BookRepository getBookRepository() {
        return bookRepository;
    }

    /**
     * library bookRepository Mutator
     *
     * @param bookRepository library book repository
     */
    @Override
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}


