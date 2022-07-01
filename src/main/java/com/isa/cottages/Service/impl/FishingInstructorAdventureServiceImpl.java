package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.FishingInstructorAdventure;
import com.isa.cottages.Model.InstructorReservation;
import com.isa.cottages.Repository.AdditionalServiceRepository;
import com.isa.cottages.Repository.FishingInstructorAdventureRepository;
import com.isa.cottages.Service.FishingInstructorAdventureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isa.cottages.Model.AdditionalService;
import com.isa.cottages.Model.Instructor;
// import com.isa.cottages.Repository.AdditionalServiceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FishingInstructorAdventureServiceImpl implements FishingInstructorAdventureService {

    private final FishingInstructorAdventureRepository adventureRepository;
    private final InstructorReservationsServiceImpl reservationService;
    private final AdditionalServiceRepository serviceRepository;
    private final UserServiceImpl userService;

    @Autowired
    public FishingInstructorAdventureServiceImpl(FishingInstructorAdventureRepository adventureRepository, AdditionalServiceRepository serviceRepository, UserServiceImpl userService, InstructorReservationsServiceImpl reservationService) {
        this.adventureRepository = adventureRepository;
        this.reservationService = reservationService;
        this.userService = userService;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public FishingInstructorAdventure findById(Long id) throws Exception {
        if (this.adventureRepository.findById(id).isEmpty()) {
            throw new Exception("No such value(adventure)");
        }
        return this.adventureRepository.findById(id).get();
    }

    @Override
    public List<FishingInstructorAdventure> findAll() {
        return this.adventureRepository.findAll();
    }

    @Override
    public Boolean canUpdateOrDelete(Long id) throws Exception {
        boolean updateOrDelete = true;
        FishingInstructorAdventure adventure = findById(id);

        if (adventure.getReserved() != null) {
            updateOrDelete = false;
        }
        return updateOrDelete;
    }

    @Override
    public List<FishingInstructorAdventure> findByKeyword(String keyword) {
        return this.adventureRepository.findByKeyword(keyword);
    }

/*
    @Override
    public List<AdditionalService> findServicesByAdventure(FishingInstructorAdventure adventure) {
        return this.serviceRepository.findAllByAdventure(adventure);
    }

    @Override
    public AdditionalService saveService(AdditionalService additionalService) {
        return this.serviceRepository.save(additionalService);
    }

    @Override
    public FishingInstructorAdventure updateAdventure(FishingInstructorAdventure adventure) throws Exception {
        FishingInstructorAdventure forUpdate = findById(adventure.getId());

        forUpdate.setAdventureName(adventure.getAdventureName());
        forUpdate.setAdventureResidence(adventure.getAdventureResidence());
        forUpdate.setAdventureCity(adventure.getAdventureCity());
        forUpdate.setAdventureState(adventure.getAdventureState());
        forUpdate.setAdventureDescription(adventure.getAdventureDescription());
        forUpdate.setMaxClients(adventure.getMaxClients());
        forUpdate.setQuickReservation(adventure.getQuickReservation());
        forUpdate.setImageUrl(adventure.getImageUrl());
        forUpdate.setAdditionalServices(adventure.getAdditionalServices());
        forUpdate.setConductRules(adventure.getConductRules());
        forUpdate.setAverageRating(adventure.getAverageRating());
        forUpdate.setRatings(adventure.getRatings());
        forUpdate.setReserved(adventure.getReserved());
        forUpdate.setGearIncluded(adventure.getGearIncluded());
        forUpdate.setPrice(adventure.getPrice());
        forUpdate.setCancellationFeePercent(adventure.getCancellationFeePercent());
        forUpdate.setInstructorInfo(adventure.getInstructorInfo());
        forUpdate.setAvailableFrom(adventure.getAvailableFrom());
        forUpdate.setAvailableUntil(adventure.getAvailableUntil());

        this.adventureRepository.save(forUpdate);
        return forUpdate;
    }
*/

    @Override
    public List<AdditionalService> findServicesByAdventure(FishingInstructorAdventure adventure) {
        return this.serviceRepository.findAllByAdventure(adventure);
    }

    @Override
    public AdditionalService saveService(AdditionalService additionalService) {
        return this.serviceRepository.save(additionalService);
    }

    @Override
    public Boolean InstructorAvailable(LocalDate startDate, LocalDate endDate, FishingInstructorAdventure instructor, int numPersons) {
        if (instructor.getNumPersons() >= numPersons) {
            if (instructor.getAvailableFrom() != null && instructor.getAvailableUntil() != null) {
                if (instructor.getAvailableFrom().toLocalDate().isBefore(startDate) && instructor.getAvailableUntil().toLocalDate().isAfter(endDate)) { return true; }
            } else { return true; }
        }

        return false;
    }

    @Override
    public Set<FishingInstructorAdventure> findAllAvailable(LocalDate startDate, LocalDate endDate, int numOfPersons) throws Exception {

        Set<FishingInstructorAdventure> available = new HashSet<>();
        Set<FishingInstructorAdventure> unAvailable = new HashSet<>();
        Set<FishingInstructorAdventure> withReservation = new HashSet<>();
        List<InstructorReservation> reservations = this.reservationService.getAllUpcoming();

        for (InstructorReservation res : reservations) {
            withReservation.add(res.getFishingInstructorAdventure());
            if(!unAvailable.contains(res.getFishingInstructorAdventure())) {
                if (this.InstructorAvailable(startDate, endDate, res.getFishingInstructorAdventure(), numOfPersons)) {
                    if ((res.getStartDate().isAfter(endDate) && res.getEndDate().isAfter(endDate)) ||
                            (res.getStartDate().isBefore(startDate) && res.getEndDate().isBefore(startDate))) {
                        available.add(res.getFishingInstructorAdventure());
                    }
                } else { unAvailable.add(res.getFishingInstructorAdventure()); }
            }
        }

        List<FishingInstructorAdventure> all = this.adventureRepository.findAll();
        HashSet<FishingInstructorAdventure> allSet = new HashSet<>(all);

        HashSet<FishingInstructorAdventure> woReservation = new HashSet<>(allSet) {{ removeAll(withReservation); }};

        for (FishingInstructorAdventure b : woReservation) {
            if (!unAvailable.contains(b) && this.InstructorAvailable(startDate, endDate, b, numOfPersons)) {  available.add(b); }
        }

        available.removeIf(unAvailable::contains);

        return available;
    }

    @Override
    public List<FishingInstructorAdventure> findAllAvailableSorted(LocalDate startDate, LocalDate endDate, int numOfPersons, Boolean asc, Boolean price, Boolean rating) throws Exception {
        Set<FishingInstructorAdventure> set = this.findAllAvailable(startDate, endDate, numOfPersons);
        List<FishingInstructorAdventure> available = new ArrayList<>(set);

        if (asc && price && !rating) {
            available.sort(Comparator.comparing(FishingInstructorAdventure::getPrice));
        }
        else if (asc && !price && rating) {
            available.sort(Comparator.comparing(FishingInstructorAdventure::getAverageRating));
        }
        else if (!asc && price && !rating) {
            available.sort(Comparator.comparing(FishingInstructorAdventure::getPrice).reversed());
        }
        else if (!asc && !price && rating) {
            available.sort(Comparator.comparing(FishingInstructorAdventure::getAverageRating).reversed());
        }

        return available;
    }

    @Override
    public FishingInstructorAdventure updateAdventure(FishingInstructorAdventure adventure) throws Exception {
        FishingInstructorAdventure forUpdate = findById(adventure.getId());

        forUpdate.setAdventureName(adventure.getAdventureName());
        forUpdate.setAdventureResidence(adventure.getAdventureResidence());
        forUpdate.setAdventureCity(adventure.getAdventureCity());
        forUpdate.setAdventureState(adventure.getAdventureState());
        forUpdate.setAdventureDescription(adventure.getAdventureDescription());
        forUpdate.setMaxClients(adventure.getMaxClients());
        forUpdate.setQuickReservation(adventure.getQuickReservation());
        forUpdate.setImageUrl(adventure.getImageUrl());
        forUpdate.setAdditionalServices(adventure.getAdditionalServices());
        forUpdate.setConductRules(adventure.getConductRules());
        forUpdate.setAverageRating(adventure.getAverageRating());
        forUpdate.setRatings(adventure.getRatings());
        forUpdate.setReserved(adventure.getReserved());
        forUpdate.setGearIncluded(adventure.getGearIncluded());
        forUpdate.setPrice(adventure.getPrice());
        forUpdate.setCancellationFeePercent(adventure.getCancellationFeePercent());
        forUpdate.setInstructorInfo(adventure.getInstructorInfo());
        forUpdate.setAvailableFrom(adventure.getAvailableFrom());
        forUpdate.setAvailableUntil(adventure.getAvailableUntil());

        this.adventureRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public FishingInstructorAdventure saveAdventure(FishingInstructorAdventure fishingInstructorAdventure) {
        FishingInstructorAdventure fia = new FishingInstructorAdventure();

        fia.setAdventureName(fishingInstructorAdventure.getAdventureName());
        fia.setAdventureCity(fishingInstructorAdventure.getAdventureCity());
        fia.setAdventureState(fishingInstructorAdventure.getAdventureState());
        fia.setAdventureResidence(fishingInstructorAdventure.getAdventureResidence());
        fia.setInstructor(fishingInstructorAdventure.getInstructor());
        fia.setGearIncluded(fishingInstructorAdventure.getGearIncluded());
        fia.setInstructorInfo(fishingInstructorAdventure.getInstructorInfo());
        fia.setPrice(fishingInstructorAdventure.getPrice());
        fia.setReserved(fishingInstructorAdventure.getReserved());
        fia.setImageUrl(fishingInstructorAdventure.getImageUrl());
        fia.setConductRules(fishingInstructorAdventure.getConductRules());
        fia.setMaxClients(fishingInstructorAdventure.getMaxClients());
        fia.setAdventureDescription(fishingInstructorAdventure.getAdventureDescription());
        fia.setAverageRating(fishingInstructorAdventure.getAverageRating());

        this.adventureRepository.save(fia);
        return fia;
    }

    @Override
    public List<FishingInstructorAdventure> findByOrderByAdventureNameAsc() { return this.adventureRepository.findByOrderByAdventureNameAsc(); }

    @Override
    public List<FishingInstructorAdventure> findByOrderByAdventureNameDesc() { return this.adventureRepository.findByOrderByAdventureNameDesc(); }

    @Override
    public List<FishingInstructorAdventure> findByOrderByRatingAsc() { return this.adventureRepository.findByOrderByAverageRatingAsc(); }

    @Override
    public List<FishingInstructorAdventure> findByOrderByRatingDesc() { return this.adventureRepository.findByOrderByAverageRatingDesc(); }

    @Override
    public List<FishingInstructorAdventure> findByOrderByAddressAsc() { return this.adventureRepository.findByOrderByAdventureResidenceAscAdventureCityAscAdventureStateAsc(); }

    @Override
    public List<FishingInstructorAdventure> findByOrderByAddressDesc() { return this.adventureRepository.findByOrderByAdventureResidenceDescAdventureCityDescAdventureStateDesc(); }

    @Override
    public List<FishingInstructorAdventure> sortByInstructorInfo(Boolean asc) {
        if (asc) {
            return this.adventureRepository.findByOrderByInstructorInfoAsc();
        } else {
            return this.adventureRepository.findByOrderByInstructorInfoDesc();
        }
    }

    @Override
    public List<FishingInstructorAdventure> findByInstructor(Long id) throws Exception {
        Instructor instructor = (Instructor) this.userService.getUserFromPrincipal();
        List<FishingInstructorAdventure> all = this.adventureRepository.findByInstructor(id);
        List<FishingInstructorAdventure> myAdventures = new ArrayList<FishingInstructorAdventure>();

        for (FishingInstructorAdventure fia : all) {
            if (Objects.equals(fia.getInstructor().getId(), instructor.getId())) {
                myAdventures.add(fia);
            }
        }
        return myAdventures;
    }
}
