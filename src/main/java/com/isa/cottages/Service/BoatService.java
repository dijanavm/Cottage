package com.isa.cottages.Service;

import com.isa.cottages.Model.Boat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface BoatService {

    Collection<Boat> getAll();
    Boat findById(Long id) throws Exception;
    List<Boat> findByBoatOwner(Long id) throws Exception;
    Boolean isByBoatOwner(Long id, Boat boat) throws Exception;
    List<Boat> findByKeyword(String keyword);
    List<Boat> findMyByKeyword(String keyword, Long id) throws Exception;

    Boat saveBoat(Boat boat);
    Boat updateAdditionalServices(Boat boat) throws Exception;
    Boat updateFishingEquipments(Boat boat) throws Exception;
    Boat updateNavigationEquipments(Boat boat) throws Exception;
    Boat updateBoat(Boat boat) throws Exception;
    void removeBoat(Boat boat, Long id) throws Exception;
    Boolean canUpdateOrDelete(Long id) throws Exception;

    Boat defineAvailability(Boat boat) throws Exception;
    Boolean myBoatAvailable(LocalDate startDate, LocalDate endDate, Boat boat, Long id) throws Exception;
    Set<Boat> findAllMyAvailable(LocalDate startDate, LocalDate endDate, int numOfPersons, Long id) throws Exception;
    List<Boat> findAllMyAvailableSorted(Long id, LocalDate startDate, LocalDate endDate, int numOfPersons, Boolean asc, Boolean price, Boolean rating) throws Exception;

    List<Boat> orderByNameDesc();
    List<Boat> orderByNameAsc();
    List<Boat> orderByRatingAsc();
    List<Boat> orderByRatingDesc();
    List<Boat> orderByAddressDesc();
    List<Boat> orderByAddressAsc();

    Boolean boatAvailable(LocalDate startDate, LocalDate endDate, Boat boat, int numPersons);

    Set<Boat> findAllAvailable(LocalDate startDate, LocalDate endDate, int numOfPersons) throws Exception;
    List<Boat> findAllAvailableSorted(LocalDate startDate, LocalDate endDate, int numOfPersons, Boolean asc, Boolean price, Boolean rating) throws Exception;
}
