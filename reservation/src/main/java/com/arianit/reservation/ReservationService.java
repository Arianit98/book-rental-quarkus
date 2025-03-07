package com.arianit.reservation;

import com.arianit.reservation.client.Book;
import com.arianit.reservation.client.BookClient;
import com.arianit.reservation.client.CostumerClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
@Transactional(REQUIRED)
public class ReservationService {

    @RestClient
    BookClient bookClient;

    @RestClient
    CostumerClient costumerClient;

    @Transactional(SUPPORTS)
    public List<Reservation> findAllReservations() {
        return Reservation.listAll();
    }

    @Transactional(SUPPORTS)
    public Reservation findReservationById(Long id) {
        return Reservation.findById(id);
    }

    public Reservation persistReservation(@Valid Reservation reservation) {
        if (!costumerExists(reservation.costumerId))
            return null;
        if (!isBookAvailable(reservation.bookId))
            return null;
        reservation.persist();
        increaseBookReservedNr(reservation.bookId);
        return reservation;
    }

    public Reservation updateReservation(@Valid Reservation reservation) {
        if (!costumerExists(reservation.costumerId))
            return null;
        if (!isBookAvailable(reservation.bookId))
            return null;
        Reservation entity = Reservation.findById(reservation.id);
        if (entity == null) {
            return null;
        }
        if (!entity.bookId.equals(reservation.bookId)) {
            decreaseBookReservedNr(entity.bookId);
            increaseBookReservedNr(reservation.bookId);
        }
        entity.costumerId = reservation.costumerId;
        entity.bookId = reservation.bookId;
        entity.createdDate = reservation.createdDate;
        entity.durationInDays = reservation.durationInDays;
        return entity;
    }

    public void deleteReservation(Long id) {
        Reservation entity = findReservationById(id);
        Reservation.deleteById(id);
        decreaseBookReservedNr(entity.bookId);
    }

    public void increaseBookReservedNr(Long bookId) {
        Book book = bookClient.getBookById(bookId).getEntity();
        assert book != null;
        book.setReservedNr(book.getReservedNr() + 1);
        bookClient.updateBook(book);
    }

    public void decreaseBookReservedNr(Long bookId) {
        Book book = bookClient.getBookById(bookId).getEntity();
        assert book != null;
        book.setReservedNr(book.getReservedNr() - 1);
        bookClient.updateBook(book);
    }

    public boolean isBookAvailable(Long bookId) {
        RestResponse<Boolean> bookResponse = bookClient.checkAvailability(bookId);
        if (bookResponse.getStatus() != 200) {
            return false;
        }
        return !Boolean.FALSE.equals(bookResponse.getEntity());
    }

    public boolean costumerExists(Long costumerId) {
        RestResponse<?> customerResponse = costumerClient.getCostumer(costumerId);
        return customerResponse.getStatus() == 200;
    }
}
