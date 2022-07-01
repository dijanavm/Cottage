package com.isa.cottages.Repository;

import com.isa.cottages.Model.BoatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BoatReservationRepository extends JpaRepository<BoatReservation, Long> {

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false " +
            "and res.boat_id = ?1", nativeQuery = true)
    List<BoatReservation> findByBoat(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.reserved=true " +
            "and res.boat_id = ?1", nativeQuery = true)
    List<BoatReservation> getAllReservedByBoat(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.boat_owner_id = ?1", nativeQuery = true)
    List<BoatReservation> getAllReservedByOwner(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false " +
            "and res.boat_owner_id = ?1", nativeQuery = true)
    List<BoatReservation> getAllOwnersReservations(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved = true " +
            "and res.boat_owner_id = ?1", nativeQuery = true)
    List<BoatReservation> getAllOwnersReservedReservations(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=false " +
            "and res.boat_owner_id = ?1", nativeQuery = true)
    List<BoatReservation> getAllFreeReservationsByOwner(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.boat_id is not null", nativeQuery = true)
    List<BoatReservation> getAllReservations();

    @Query(value = "SELECT * FROM RESERVATION RES WHERE RES.DELETED=FALSE AND RES.RESERVED=TRUE" +
            "AND RES.BOAT_ID IS NOT NULL AND RES.CLIENT_ID=?1", nativeQuery = true)
    List<BoatReservation> findAllByClient(@Param("client_id") Long clientId);

    @Query(value = "SELECT * FROM reservation b WHERE b.boat_id = ?1 and " +
            "b.discount = true and b.deleted=false", nativeQuery = true)
    List<BoatReservation> findDiscountsByBoat(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res JOIN Users u ON res.client_id=u.id WHERE" +
            "(lower(u.first_name) like lower(concat('%', ?1, '%'))" +
            "or lower(u.last_name) like lower(concat('%', ?1, '%')))" +
            "and reservation_type like 'boat_reservation' " +
            "and res.reserved=true and res.deleted = false" , nativeQuery = true)
    List<BoatReservation> findClientForHistory(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM reservation res JOIN Users u ON res.client_id=u.id WHERE" +
            "(lower(u.first_name) like lower(concat('%', ?1, '%'))" +
            "or lower(u.last_name) like lower(concat('%', ?1, '%')))" +
            "and reservation_type like 'boat_reservation' " +
            "and res.deleted = false", nativeQuery = true)
    List<BoatReservation> findClientForCalendar(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=false " +
            "and res.boat_id=?1 and res.discount = true", nativeQuery = true)
    List<BoatReservation> findAllWithDiscount(@Param("boatId") Long boatId);

    void deleteById(Long id);

    @Query(value = "SELECT * FROM reservation b WHERE b.deleted=false and " +
            "b.reserved = true and b.num_persons >= ?1 and boat_id is not null", nativeQuery = true)
    List<BoatReservation> findAllByCapacity(@Param("capacity") int numOfPersons);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.boat_id is not null " +
            "and not (res.start_date < ?2 and res.end_date > ?1 )" +
            "and res.num_persons >= ?3 ", nativeQuery = true)
    List<BoatReservation> findAllAvailable(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                           @Param("capacity") int capacity);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.boat_id is not null " +
            "and res.start_date < ?2 and res.end_date > ?1 ", nativeQuery = true)
    List<BoatReservation> findAllUnavailable(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.boat_id is not null " +
            "and not (res.start_date < ?2 and res.end_date > ?1 )" +
            "and res.num_persons >= ?3 and res.boat_owner_id=?4", nativeQuery = true)
    List<BoatReservation> findAllMyAvailable(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                           @Param("capacity") int capacity, @Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.boat_id is not null " +
            "and res.start_date <= ?2 and res.end_date > ?1 and res.boat_owner_id=?3", nativeQuery = true)
    List<BoatReservation> findAllMyUnavailable(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                               @Param("id") Long id);

}

