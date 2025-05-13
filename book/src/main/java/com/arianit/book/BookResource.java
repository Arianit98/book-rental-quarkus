package com.arianit.book;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/v1/books")
public class BookResource {

    @Inject
    BookService service;

    @GET
    public RestResponse<List<Book>> getBooks() {
        List<Book> books = service.findAllBooks();
        return RestResponse.ok(books);
    }

    @GET
    @Path("/{id}")
    public RestResponse<Book> getBookById(@RestPath Long id) {
        Book book = service.findBookById(id);
        if (book == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(book);
    }

    @POST
    public RestResponse<Book> createBook(Book book) {
        book = service.persistBook(book);
        UriBuilder uriBuilder = UriBuilder.fromResource(BookResource.class).path(Long.toString(book.id));
        return RestResponse.created(uriBuilder.build());
    }

    @PUT
    public RestResponse<Book> updateBook(Book book) {
        book = service.updateBook(book);
        if (book == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(book);
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<?> deleteBook(@RestPath Long id) {
        service.deleteBook(id);
        return RestResponse.noContent();
    }

    @GET
    @Path("/{id}/checkAvailability")
    public RestResponse<Boolean> checkAvailability(@RestPath Long id) {
        boolean isAvailable = service.checkAvailability(id);
        return RestResponse.ok(isAvailable);
    }
}