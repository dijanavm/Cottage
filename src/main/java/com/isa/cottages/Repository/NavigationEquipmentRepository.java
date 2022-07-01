package com.isa.cottages.Repository;

import com.isa.cottages.Model.NavigationEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NavigationEquipmentRepository extends JpaRepository<NavigationEquipment, Long> {

    @Query(value = "SELECT * FROM navigation_equipment n WHERE n.boat_id = ?1", nativeQuery = true)
    List<NavigationEquipment> findByBoat(@Param("id") Long id);
}
