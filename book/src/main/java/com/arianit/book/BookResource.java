package com.arianit.book;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/v1/books")
public class BookResource {

    @Inject
    Logger logger;

    @Inject
    BookService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<List<Book>> getBooks() {
        return RestResponse.ok(service.findAllBooks());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Book> getBookById(@RestPath Long id) {
        Book book = service.findBookById(id);
        if (book == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(book);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Book> createBook(@Valid Book book) {
        book = service.persistBook(book);
        UriBuilder uriBuilder = UriBuilder.fromResource(BookResource.class).path(Long.toString(book.id));
        return RestResponse.created(uriBuilder.build());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Book> updateBook(@Valid Book book) {
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
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Boolean> checkAvailability(@RestPath Long id) {
        boolean isAvailable = service.checkAvailability(id);
        return RestResponse.ok(isAvailable);
    }
}
