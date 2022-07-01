package com.isa.cottages.Repository;

import com.isa.cottages.Model.CottageReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CottageReservationRepository extends JpaRepository<CottageReservation, Long> {

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false " +
            "and res.cottage_id = ?1", nativeQuery = true)
    List<CottageReservation> findByCottage(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.reserved=true " +
            "and res.cottage_id = ?1", nativeQuery = true)
    List<CottageReservation> getAllReservedByCottage(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.cottage_owner_id = ?1", nativeQuery = true)
    List<CottageReservation> getAllReservedByOwner(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false " +
            "and res.cottage_owner_id = ?1", nativeQuery = true)
    List<CottageReservation> getAllOwnersReservations(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved = true " +
            "and res.cottage_owner_id = ?1", nativeQuery = true)
    List<CottageReservation> getAllOwnersReservedReservations(@Param("id") Long id);

    @Query(value = "SELECT * FROM RESERVATION RES WHERE RES.DELETED=FALSE AND RES.RESERVED=TRUE" +
            "AND RES.COTTAGE_ID IS NOT NULL AND RES.CLIENT_ID=?1", nativeQuery = true)
    List<CottageReservation> findAllByClient(@Param("client_id") Long clientId);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.cottage_id is not null", nativeQuery = true)
    List<CottageReservation> getAllReserved();

    @Query(value = "SELECT * FROM reservation c WHERE c.cottage_id = ?1 and " +
            "c.discount = true and c.deleted=false", nativeQuery = true)
    List<CottageReservation> findDiscountsByCottage(@Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res JOIN Users u ON res.client_id=u.id WHERE " +
            "(lower(u.first_name) like lower(concat('%', ?1, '%'))" +
            "or lower(u.last_name) like lower(concat('%', ?1, '%'))) " +
            "and reservation_type like 'cottage_reservation' and res.reserved=true " +
            "and res.deleted = false", nativeQuery = true)
    List<CottageReservation> findClientForHistory(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM reservation res JOIN Users u ON res.client_id=u.id WHERE " +
            "(lower(u.first_name) like lower(concat('%', ?1, '%'))" +
            "or lower(u.last_name) like lower(concat('%', ?1, '%'))) " +
            "and reservation_type like 'cottage_reservation' " +
            "and res.deleted = false", nativeQuery = true)
    List<CottageReservation> findClientForCalendar(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=false " +
            "and res.cottage_id=?1 and res.discount = true", nativeQuery = true)
    List<CottageReservation> findAllWithDiscount(Long cottageId);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.cottage_id is not null " +
            "and not (res.start_date < ?2 and res.end_date > ?1 )" +
            "and res.num_persons >= ?3 and res.cottage_owner_id=?4", nativeQuery = true)
    List<CottageReservation> findAllMyAvailable(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                             @Param("capacity") int capacity, @Param("id") Long id);

    @Query(value = "SELECT * FROM reservation res WHERE res.deleted=false and res.reserved=true " +
            "and res.cottage_id is not null " +
            "and res.start_date <= ?2 and res.end_date > ?1 and res.cottage_owner_id=?3", nativeQuery = true)
    List<CottageReservation> findAllMyUnavailable(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                               @Param("id") Long id);

}