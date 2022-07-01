package com.isa.cottages.Service;

import com.isa.cottages.Model.*;
import com.isa.cottages.Model.Client;
import com.isa.cottages.Model.InstructorReservation;

import java.util.List;

public interface InstructorReservationsService {

    List<InstructorReservation> getPastReservations() throws Exception;
    List<InstructorReservation> getUpcomingReservations() throws Exception;
    List<InstructorReservation> findAllByClient(Client client);
    List<InstructorReservation> getAllUpcoming();

    List<InstructorReservation> findByOrderByStartTimeAsc() throws Exception;
    List<InstructorReservation> findByOrderByStartTimeDesc() throws Exception;
    List<InstructorReservation> findByOrderByDurationAsc() throws Exception;
    List<InstructorReservation> findByOrderByDurationDesc() throws Exception;
    List<InstructorReservation> findByOrderByPriceAsc() throws Exception;

    Boolean canCancel(Long id);

    void cancel(Long id);

    InstructorReservation update(InstructorReservation reservation);

    InstructorReservation getOne(Long id);

    InstructorReservation save(InstructorReservation instructorReservation);

    void sendReservationMail(InstructorReservation reservation);

    List<InstructorReservation> findByOrderByPriceDesc() throws Exception;

    void setDate(Reservation reservation);

    InstructorReservation makeReservation(InstructorReservation reservation, FishingInstructorAdventure instructor) throws Exception;

    List<InstructorReservation> getAllWithDiscount(Long instructorId);

    InstructorReservation makeReservationOnDiscount(Long id) throws Exception;
}
