package com.isa.cottages.Repository;

import com.isa.cottages.Model.Boat;
import com.isa.cottages.Model.FishingInstructorAdventure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FishingInstructorAdventureRepository extends JpaRepository<FishingInstructorAdventure, Long> {

    Optional<FishingInstructorAdventure> findById(Long id);

    @Query(value = "SELECT * FROM fishing_instructor_adventure c where lower(c.adventure_name) like lower(concat('%', ?1, '%')) "
            + "or lower(c.instructor_info) like lower(concat('%', ?1, '%'))"
            + "or lower(c.adventure_state) like lower(concat('%', ?1, '%'))"
            + "or lower(c.adventure_city) like lower(concat('%', ?1, '%'))"
            + "or lower(c.adventure_residence) like lower(concat('%', ?1, '%')) "
            + "or lower(c.adventure_description) like lower(concat('%', ?1, '%')) "
            , nativeQuery = true)
    List<FishingInstructorAdventure> findByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM fishing_instructor_adventure fia WHERE fia.instructor_id = ?1", nativeQuery = true)
    List<FishingInstructorAdventure> findByInstructor(@Param("id") Long id);

    List<FishingInstructorAdventure> findByOrderByInstructorInfoAsc();

    List<FishingInstructorAdventure> findByOrderByInstructorInfoDesc();
    List<FishingInstructorAdventure> findByOrderByAdventureNameAsc();
    List<FishingInstructorAdventure> findByOrderByAdventureNameDesc();
    List<FishingInstructorAdventure> findByOrderByAverageRatingAsc();
    List<FishingInstructorAdventure> findByOrderByAverageRatingDesc();
    List<FishingInstructorAdventure> findByOrderByAdventureResidenceAscAdventureCityAscAdventureStateAsc();
    List<FishingInstructorAdventure> findByOrderByAdventureResidenceDescAdventureCityDescAdventureStateDesc();
}
