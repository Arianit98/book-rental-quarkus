package com.arianit.reservation.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@RegisterRestClient(configKey = "book-api")
@Path("/api/v1/books")
public interface BookClient {

    @GET
    @Path("/{id}")
    RestResponse<Book> getBookById(@RestPath Long id);

    @PUT
    RestResponse<Book> updateBook(Book book);

    @GET
    @Path("/{id}/checkAvailability")
    RestResponse<Boolean> checkAvailability(@RestPath Long id);
}
