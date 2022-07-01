package com.isa.cottages.Repository;

import com.isa.cottages.Model.FishingInstructorAdventure;
import com.isa.cottages.Model.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalServiceRepository extends JpaRepository<AdditionalService, Long> {

    List<AdditionalService> findAllByAdventure(FishingInstructorAdventure fishingInstructorAdventure);
    @Query(value = "SELECT * FROM additional_service a WHERE a.cottage_id = ?1", nativeQuery = true)
    List<AdditionalService> findByCottage(@Param("id") Long id);

    @Query(value = "SELECT * FROM additional_service a WHERE a.boat_id = ?1", nativeQuery = true)
    List<AdditionalService> findByBoat(@Param("id") Long id);
}
