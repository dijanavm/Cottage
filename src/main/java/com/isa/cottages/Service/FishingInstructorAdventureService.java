package com.isa.cottages.Service;

import com.isa.cottages.Model.AdditionalService;
import com.isa.cottages.Model.FishingInstructorAdventure;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface FishingInstructorAdventureService {
    FishingInstructorAdventure findById(Long id) throws Exception;
    List<FishingInstructorAdventure> findAll();
    List<FishingInstructorAdventure> findByKeyword(String keyword);

    Set<FishingInstructorAdventure> findAllAvailable(LocalDate startDate, LocalDate endDate, int numOfPersons) throws Exception;
    List<FishingInstructorAdventure> findAllAvailableSorted(LocalDate startDate, LocalDate endDate, int numOfPersons, Boolean asc, Boolean price, Boolean rating) throws Exception;

    Boolean InstructorAvailable(LocalDate startDate, LocalDate endDate, FishingInstructorAdventure instructor, int numPersons);
    Boolean canUpdateOrDelete(Long id) throws Exception;

    List<AdditionalService> findServicesByAdventure(FishingInstructorAdventure adventure);

    AdditionalService saveService(AdditionalService additionalService);

    FishingInstructorAdventure updateAdventure(FishingInstructorAdventure adventure) throws Exception;
    FishingInstructorAdventure saveAdventure(FishingInstructorAdventure FishingInstructorAdventure);

    List<FishingInstructorAdventure> sortByInstructorInfo(Boolean asc);
    List<FishingInstructorAdventure> findByOrderByAdventureNameAsc();
    List<FishingInstructorAdventure> findByOrderByAdventureNameDesc();
    List<FishingInstructorAdventure> findByOrderByRatingAsc();
    List<FishingInstructorAdventure> findByOrderByRatingDesc();
    List<FishingInstructorAdventure> findByOrderByAddressAsc();
    List<FishingInstructorAdventure> findByOrderByAddressDesc();

    List<FishingInstructorAdventure> findByInstructor(Long id) throws Exception;
}
