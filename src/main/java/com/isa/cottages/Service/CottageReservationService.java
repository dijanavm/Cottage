package com.isa.cottages.Service;

import com.isa.cottages.Model.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CottageReservationService {

    CottageReservation findOne(Long id);
    List<CottageReservation> findByCottage(Long id) throws Exception;
    List<CottageReservation> getAllOwnersReservations(Long id) throws Exception;

    List<CottageReservation> findNowAndUpcomingByCottage(Long id) throws Exception;

    Boolean canCancel(Long id);

    void cancel(Long id);

    List<CottageReservation> getAllOwnersReservedReservations(Long id) throws Exception;
    List<CottageReservation> getAllOwnersNowAndUpcomingReservations(Long id) throws Exception;
    List<CottageReservation> getOwnersUpcomingReservations(Long id) throws Exception;
    List<CottageReservation> getOwnersPastReservations(Long id) throws Exception;
    List<CottageReservation> getUpcomingReservations() throws Exception;
    List<CottageReservation> getPastReservations() throws Exception;
    List<CottageReservation> getAllUpcoming();

    CottageReservation saveDiscount(CottageReservation cottageReservation) throws Exception;
    CottageReservation save(CottageReservation cottageReservation);
    CottageReservation makeReservation(CottageReservation reservation, Cottage cottage) throws Exception;

    List<CottageReservation> findDiscountsByCottage(Long id) throws Exception;

    List<CottageReservation> findAllByClient(Client client);
    List<CottageReservation> findClientForHistory(String keyword, Long id) throws Exception;
    List<CottageReservation> findClientForCalendar(String keyword, Long id) throws Exception;
    Set<CottageReservation> findByInterval(LocalDate startDate, LocalDate endDate, Long id) throws Exception;
    Set<CottageReservation> findByInterval2(LocalDate startDate, LocalDate endDate, Long id) throws Exception;

    List<CottageReservation> findByOrderByStartTimeAsc() throws Exception;
    List<CottageReservation> findByOrderByStartTimeDesc() throws Exception;
    List<CottageReservation> findByOrderByDurationAsc() throws Exception;
    List<CottageReservation> findByOrderByDurationDesc() throws Exception;
    List<CottageReservation> findByOrderByPriceAsc() throws Exception;
    List<CottageReservation> findByOrderByPriceDesc() throws Exception;

    List<CottageReservation> getAllMyUnavailable(LocalDate desiredStart, LocalDate desiredEnd, Long id) throws Exception;
    List<CottageReservation> getAllMyAvailable(LocalDate desiredStart, LocalDate desiredEnd, int capacity, Long id) throws Exception;
    void setDate(Reservation reservation);
    void sendReservationMail(CottageReservation reservation);

    List<CottageReservation> getAllWithDiscount(Long CottageId);

    CottageReservation update(CottageReservation reservation);
    CottageReservation makeReservationOnDiscount(Long reservationId) throws Exception;
    CottageReservation getOne(Long id);

    void setDate(CottageReservation reservation);

    CottageReservation makeReservationWithClient(CottageReservation reservation, Cottage cottage, Long clid) throws Exception;

    Double CalculatePrice(CottageReservation reservation) throws ParseException;
}
