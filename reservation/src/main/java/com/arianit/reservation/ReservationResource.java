package com.arianit.reservation;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/v1/reservations")
public class ReservationResource {

    @Inject
    Logger logger;

    @Inject
    ReservationService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<List<Reservation>> getReservations() {
        return RestResponse.ok(service.findAllReservations());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Reservation> getReservationById(@RestPath Long id) {
        Reservation reservation = service.findReservationById(id);
        if (reservation == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(reservation);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Reservation> createReservation(Reservation reservation) {
        reservation = service.persistReservation(reservation);
        if (reservation == null) {
            return RestResponse.status(400,"Costumer not found or book is not available");
        }
        UriBuilder uriBuilder = UriBuilder.fromResource(ReservationResource.class).path(Long.toString(reservation.id));
        return RestResponse.created(uriBuilder.build());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Reservation> updateReservation(Reservation reservation) {
        reservation = service.updateReservation(reservation);
        if (reservation == null) {
            return RestResponse.status(400,"Reservation not found or costumer/book is not available");
        }
        return RestResponse.ok(reservation);
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<?> deleteReservation(@RestPath Long id) {
        service.deleteReservation(id);
        return RestResponse.noContent();
    }
}
