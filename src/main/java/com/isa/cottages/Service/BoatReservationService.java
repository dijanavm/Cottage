package com.isa.cottages.Service;

import com.isa.cottages.Model.Boat;
import com.isa.cottages.Model.BoatReservation;
import com.isa.cottages.Model.Client;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BoatReservationService {

    BoatReservation findById(Long id) throws Exception;
    List<BoatReservation> findNowAndUpcomingByBoat(Long id) throws Exception;
    List<BoatReservation> getAllOwnersReservations(Long id) throws Exception;
    List<BoatReservation> getAllOwnersReservedReservations(Long id) throws Exception;
    List<BoatReservation> getOwnersUpcomingReservations(Long id) throws Exception;
    List<BoatReservation> getAllOwnersNowAndUpcomingReservations(Long id) throws Exception;
    List<BoatReservation> getOwnersPastReservations(Long id) throws Exception;
    List<BoatReservation> getOwnersFreeReservations(Long id) throws Exception;
    List<BoatReservation> getPastReservations() throws Exception;
    List<BoatReservation> getUpcomingReservations() throws Exception;
    List<BoatReservation> getAllUpcoming();
    List<BoatReservation> findAllByClient(Client client);
    List<BoatReservation> findClientForHistory(String keyword, Long id) throws Exception;
    List<BoatReservation> findClientForCalendar(String keyword, Long id) throws Exception;
    Set<BoatReservation> findByInterval(LocalDate startDate, LocalDate endDate, Long id) throws Exception;
    Set<BoatReservation> findByInterval2(LocalDate startDate, LocalDate endDate, Long id) throws Exception;

    BoatReservation saveDiscount(BoatReservation boatReservation) throws Exception;
    BoatReservation saveReservation(BoatReservation boatReservation);
    List<BoatReservation> findDiscountsByBoat(Long id) throws Exception;

    BoatReservation save(BoatReservation reservation);
    BoatReservation makeReservation(BoatReservation reservation, Boat boat) throws Exception;
    BoatReservation makeReservationOnDiscount(Long reservationId) throws Exception;

    Boolean canCancel(Long id);

    void cancel(Long id);
    void deleteById(Long id);
    List<BoatReservation> getAllMyUnavailable(LocalDate desiredStart, LocalDate desiredEnd, Long id) throws Exception;
    List<BoatReservation> getAllMyAvailable(LocalDate desiredStart, LocalDate desiredEnd, int capacity, Long id) throws Exception;

    void setDate(BoatReservation boatReservation);
    void sendReservationMail(BoatReservation reservation);

    List<BoatReservation> findByOrderByStartTimeAsc() throws Exception;
    List<BoatReservation> findByOrderByStartTimeDesc() throws Exception;
    List<BoatReservation> findByOrderByDurationAsc() throws Exception;
    List<BoatReservation> findByOrderByDurationDesc() throws Exception;
    List<BoatReservation> findByOrderByPriceAsc() throws Exception;
    List<BoatReservation> findByOrderByPriceDesc() throws Exception;
    List<BoatReservation> findAllAvailable(LocalDate startTime, LocalDate endTime);

    List<BoatReservation> getAllWithDiscount(Long boatId);

    BoatReservation update(BoatReservation reservation);
    BoatReservation getOne(Long id);

    BoatReservation makeReservationWithClient(BoatReservation reservation, Boat boat, Long clid) throws Exception;

    Double CalculatePrice(BoatReservation reservation) throws ParseException;

}
