package com.isa.cottages.Repository;

import com.isa.cottages.Model.Cottage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CottageRepository extends JpaRepository<Cottage, Long> {

    @Query(value="SELECT * FROM Cottage c where lower(c.name) like lower(concat('%', ?1, '%')) " +
            "or lower(c.state) like lower(concat('%', ?1, '%'))" +
            "or lower(c.city) like lower(concat('%', ?1, '%'))" +
            "or lower(c.residence) like lower(concat('%', ?1, '%'))"
           , nativeQuery = true)
    List<Cottage> findByKeyword(@Param("keyword") String keyword);

    @Query(value="SELECT c.* FROM Cottage c JOIN USERS co ON co.id=c.cottage_owner_id WHERE " +
            "(lower(c.name) like lower(concat('%', ?1, '%'))"  +
            "or lower(co.first_name) like lower(concat('%', ?1, '%'))" +
            "or lower(co.last_name) like lower(concat('%', ?1, '%'))" +
            "or lower(c.residence) like lower(concat('%', ?1, '%'))"+
            "or lower(c.state) like lower(concat('%', ?1, '%'))"+
            "or lower(c.city) like lower(concat('%', ?1, '%')))" +
            "and c.cottage_owner_id=?2"
            , nativeQuery = true)
    List<Cottage> findMyByKeyword(@Param("keyword") String keyword, @Param("id") Long id);

    @Query(value="SELECT * FROM Cottage co WHERE lower(co.name) like lower(concat('%', ?1, '%')) " +
            "or lower(co.residence) like lower(concat('%', ?1, '%')) " +
            "and co.cottage_owner_id = ?1",
            nativeQuery = true)
    List<Cottage> findByKeywordAndCottageOwner(@Param("keyword") String keyword, @Param("id") Long id);

    @Query(value = "SELECT * FROM Cottage co WHERE co.cottage_owner_id = ?1", nativeQuery = true)
    List<Cottage> findByCottageOwner(@Param("id") Long id);

    List<Cottage> findByOrderByNameDesc();
    List<Cottage> findByOrderByNameAsc();
    List<Cottage> findByOrderByAverageRatingAsc();
    List<Cottage> findByOrderByAverageRatingDesc();
    List<Cottage> findByOrderByResidenceAscCityAscStateAsc();
    List<Cottage> findByOrderByResidenceDescCityDescStateDesc();
}
