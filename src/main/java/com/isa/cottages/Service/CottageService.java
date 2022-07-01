package com.isa.cottages.Service;

import com.isa.cottages.Model.Boat;
import com.isa.cottages.Model.Cottage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CottageService {

    Cottage findById(Long id) throws Exception;
    Collection<Cottage> findAll();

    List<Cottage> findByCottageOwner(Long id) throws Exception;

    List<Cottage> findByKeyword(String keyword);
    List<Cottage> findMyByKeyword(String keyword, Long id) throws Exception;
    List<Cottage> findByKeywordAndCottageOwner(String keyword, Long id) throws Exception;
    Set<Cottage> findAllAvailable(LocalDate startDate, LocalDate endDate, int numOfPersons) throws Exception;
    List<Cottage> findAllAvailableSorted(LocalDate startDate, LocalDate endDate, int numOfPersons, Boolean asc, Boolean price, Boolean rating) throws Exception;

    Cottage defineAvailability(Cottage cottage) throws Exception;
    Boolean myCottageAvailable(LocalDate startDate, LocalDate endDate, Cottage cottage, Long id) throws Exception;
    Set<Cottage> findAllMyAvailable(LocalDate startDate, LocalDate endDate, int numOfPersons, Long id) throws Exception;
    List<Cottage> findAllMyAvailableSorted(Long id, LocalDate startDate, LocalDate endDate, int numOfPersons, Boolean asc, Boolean price, Boolean rating) throws Exception;

    Cottage saveCottage(Cottage cottage) throws Exception;
    Cottage updateAdditionalServices(Cottage cottage) throws Exception;
    Cottage updateCottage(Cottage cottage) throws Exception;
    void removeCottage(Cottage cottage, Long oid) throws Exception;

    Boolean isByCottageOwner(Long id, Cottage cottage) throws Exception;
    Boolean canUpdateOrDelete(Long id) throws Exception;
    Boolean cottageAvailable(LocalDate startDate, LocalDate endDate, Cottage cottage, int numPersons);

    List<Cottage> orderByNameDesc();
    List<Cottage> orderByNameAsc();
    List<Cottage> orderByRatingAsc();
    List<Cottage> orderByRatingDesc();
    List<Cottage> orderByAddressDesc();
    List<Cottage> orderByAddressAsc();
}
