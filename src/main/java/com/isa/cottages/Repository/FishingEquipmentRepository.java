package com.isa.cottages.Repository;

import com.isa.cottages.Model.FishingEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FishingEquipmentRepository extends JpaRepository<FishingEquipment, Long> {

    @Query(value = "SELECT * FROM fishing_equipment f WHERE f.boat_id = ?1", nativeQuery = true)
    List<FishingEquipment> findByBoat(@Param("id") Long id);
}
