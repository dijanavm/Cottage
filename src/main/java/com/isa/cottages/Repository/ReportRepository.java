package com.isa.cottages.Repository;

import com.isa.cottages.Model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "SELECT * FROM report r WHERE r.boat_owner_id = ?1", nativeQuery = true)
    List<Report> findBoatOwnersReports(@Param("id") Long id);

    @Query(value = "SELECT * FROM report r WHERE r.cottage_owner_id = ?1", nativeQuery = true)
    List<Report> findCottageOwnersReports(@Param("id") Long id);
}
