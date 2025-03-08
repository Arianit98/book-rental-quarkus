package com.arianit.book;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
@Transactional(REQUIRED)
public class BookService {

    @Transactional(SUPPORTS)
    public List<Book> findAllBooks() {
        return Book.listAll();
    }

    @Transactional(SUPPORTS)
    public Book findBookById(Long id) {
        return Book.findById(id);
    }

    public Book persistBook(@Valid Book book) {
        book.persist();
        return book;
    }

    public Book updateBook(@Valid Book book) {
        Book entity = Book.findById(book.id);
        if (entity == null) {
            return null;
        }
        entity.title = book.title;
        entity.author = book.author;
        entity.year = book.year;
        entity.stockNr = book.stockNr;
        entity.reservedNr = book.reservedNr;
        return entity;
    }

    public void deleteBook(Long id) {
        Book.deleteById(id);
    }

    public boolean checkAvailability(Long id) {
        Book book = findBookById(id);
        if (book == null) {
            return false;
        }
        return book.stockNr > book.reservedNr;
    }
}
