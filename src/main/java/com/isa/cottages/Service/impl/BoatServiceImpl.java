package com.isa.cottages.Service.impl;

import com.isa.cottages.Model.*;
import com.isa.cottages.Repository.BoatRepository;
import com.isa.cottages.Service.BoatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class BoatServiceImpl implements BoatService {

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BoatOwnerServiceImpl boatOwnerService;

    @Autowired
    private BoatReservationServiceImpl reservationService;

    @Override
    public Boat findById(Long id) throws Exception {
        if (this.boatRepository.findById(id).isEmpty()) {
            throw new Exception("No such value(boat service)");
        }
        return this.boatRepository.findById(id).get();
    }

    @Override
    public Boat saveBoat(Boat boat) {
        Boat b = new Boat();

        b.setBoatName(boat.getBoatName());
        b.setEngineType(boat.getEngineType());
        b.setLength(boat.getLength());
        b.setEngineNumber(boat.getEngineNumber());
        b.setEnginePower(boat.getEnginePower());
        b.setMaxSpeed(boat.getMaxSpeed());
        b.setState(boat.getState());
        b.setCity(boat.getCity());
        b.setResidence(boat.getResidence());
        b.setCapacity(boat.getCapacity());
        b.setNumPersons(boat.getCapacity());
        b.setRules(boat.getRules());
        b.setDescription(boat.getDescription());
        b.setPrice(boat.getPrice());
        b.setBoatOwner(boat.getBoatOwner());
        b.setAvailableFrom(boat.getAvailableFrom());
        b.setAvailableUntil(boat.getAvailableUntil());
        b.setImageUrl(boat.getImageUrl());
        b.setReserved(false);
        b.setDeleted(false);
        b.setImageUrl(boat.getImageUrl());

        this.boatRepository.save(b);
        return b;
    }

    @Override
    public Boat updateAdditionalServices(Boat boat) throws Exception {
        Boat forUpdate = this.findById(boat.getId());
        if(forUpdate == null) { throw new Exception("Boat does not exist."); }

        forUpdate.setAdditionalServices(boat.getAdditionalServices());
        this.boatRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public Boat updateFishingEquipments(Boat boat) throws Exception {
        Boat forUpdate = this.findById(boat.getId());
        if(forUpdate == null) { throw new Exception("Boat does not exist."); }

        forUpdate.setFishingEquipments(boat.getFishingEquipments());
        this.boatRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public Boat updateNavigationEquipments(Boat boat) throws Exception {
        Boat forUpdate = this.findById(boat.getId());
        if(forUpdate == null) { throw new Exception("Boat does not exist."); }

        forUpdate.setNavigationEquipments(boat.getNavigationEquipments());
        this.boatRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public Boat updateBoat(Boat boat) throws Exception {
        Boat forUpdate = findById(boat.getId());

        forUpdate.setBoatName(boat.getBoatName());
        forUpdate.setEngineType(boat.getEngineType());
        forUpdate.setLength(boat.getLength());
        forUpdate.setEngineNumber(boat.getEngineNumber());
        forUpdate.setEnginePower(boat.getEnginePower());
        forUpdate.setMaxSpeed(boat.getMaxSpeed());
        forUpdate.setState(boat.getState());
        forUpdate.setCity(boat.getCity());
        forUpdate.setResidence(boat.getResidence());
        forUpdate.setCapacity(boat.getCapacity());
        forUpdate.setNumPersons(boat.getCapacity());
        forUpdate.setRules(boat.getRules());
        forUpdate.setDescription(boat.getDescription());
        forUpdate.setAvailableFrom(boat.getAvailableFrom());
        forUpdate.setAvailableUntil(boat.getAvailableUntil());
        forUpdate.setPrice(boat.getPrice());
        forUpdate.setBoatOwner(boat.getBoatOwner());
        forUpdate.setCancellationCondition(boat.getCancellationCondition());
        forUpdate.setImageUrl(boat.getImageUrl());

        this.boatRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public void removeBoat(Boat boat, Long oid) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.findById(oid);
        if (boatOwner == null) {
            throw new Exception("Boat owner does not exist.");
        }
        Boat b = findById(boat.getId());

        Set<Boat> boats = boatOwner.getBoats();
        boats.remove(b);
        boatOwner.setBoats(boats);
        boat.setDeleted(true);

        b.setBoatOwner(null);
        this.boatOwnerService.updateBoats(boatOwner);
    }

    @Override
    public Boolean canUpdateOrDelete(Long id) throws Exception {
        boolean updateOrDelete = true;
        Boat boat = findById(id);
        List<BoatReservation> reservations = this.reservationService.findNowAndUpcomingByBoat(id);
        if (reservations != null) {
            for (BoatReservation br : reservations) {
                if (br.getReserved() == true || boat.getReserved() == true) {
                    updateOrDelete = false;
                }
            }
        }
        return updateOrDelete;
    }

    @Override
    public Collection<Boat> getAll() {
        return this.boatRepository.findAll();
    }

    @Override
    public List<Boat> findByKeyword(String keyword) {
        return this.boatRepository.findByKeyword(keyword);
    }

    @Override
    public List<Boat> findMyByKeyword(String keyword, Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        return this.boatRepository.findMyByKeyword(keyword, id);
    }

    @Override
    public List<Boat> findByBoatOwner(Long id) throws Exception{
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        List<Boat> all = this.boatRepository.findByBoatOwner(id);
        List<Boat> myBoats = new ArrayList<Boat>();

        for (Boat bo:all) {
            if(Objects.equals(bo.getBoatOwner().getId(), boatOwner.getId())) {
                myBoats.add(bo);
            }
        }
        return myBoats;
    }

    @Override
    public Boolean isByBoatOwner(Long id, Boat boat) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();

        if(Objects.equals(boat.getBoatOwner().getId(), boatOwner.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public Boat defineAvailability(Boat boat) throws Exception {
        Boat forUpdate = findById(boat.getId());

        forUpdate.setAvailableFrom(boat.getAvailableFrom());
        forUpdate.setAvailableUntil(boat.getAvailableUntil());

        this.boatRepository.save(forUpdate);
        return forUpdate;
    }

    @Override
    public List<Boat> orderByNameDesc() { return this.boatRepository.findByOrderByBoatNameDesc(); }

    @Override
    public List<Boat> orderByNameAsc() { return this.boatRepository.findByOrderByBoatNameAsc(); }

    @Override
    public List<Boat> orderByRatingAsc() { return this.boatRepository.findByOrderByAverageRatingAsc();  }

    @Override
    public List<Boat> orderByRatingDesc() { return this.boatRepository.findByOrderByAverageRatingDesc();  }

    @Override
    public List<Boat> orderByAddressDesc() { return this.boatRepository.findByOrderByResidenceDescCityDescStateDesc(); }

    @Override
    public List<Boat> orderByAddressAsc() { return this.boatRepository.findByOrderByResidenceAscCityAscStateAsc(); }

    @Override
    public Boolean boatAvailable(LocalDate startDate, LocalDate endDate, Boat boat, int numPersons) {
        if (boat.getNumPersons() >= numPersons) {
            if (boat.getAvailableFrom() != null && boat.getAvailableUntil() != null) {
                if (boat.getAvailableFrom().toLocalDate().isBefore(startDate) && boat.getAvailableUntil().toLocalDate().isAfter(endDate)) { return true; }
            } else { return true; }
        }

        return false;
    }

    public Boolean myBoatAvailable(LocalDate startDate, LocalDate endDate, Boat boat, Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
          if (boat.getDeleted() == false) {
              if ((boat.getAvailableFrom() == null && boat.getAvailableUntil() == null) ||
                      boat.getAvailableFrom().toLocalDate().isBefore(startDate) && boat.getAvailableUntil().toLocalDate().isAfter(endDate)
                              && Objects.equals(boat.getBoatOwner().getId(), boatOwner.getId())) {
                  return true;
              }
          }
        return false;
    }

    @Override
    public Set<Boat> findAllAvailable(LocalDate startDate, LocalDate endDate, int numOfPersons) throws Exception {

        Set<Boat> available = new HashSet<>();
        Set<Boat> unAvailable = new HashSet<>();
        Set<Boat> withReservation = new HashSet<>();
        List<BoatReservation> reservations = this.reservationService.getAllUpcoming();

        for (BoatReservation res : reservations) {
            withReservation.add(res.getBoat());
            if (!unAvailable.contains(res.getBoat())) {
                if (this.boatAvailable(startDate, endDate, res.getBoat(), numOfPersons)) {
                    if ((res.getStartTime().toLocalDate().isAfter(startDate) && res.getEndTime().toLocalDate().isAfter(endDate)) ||
                            (res.getStartTime().toLocalDate().isBefore(startDate) && res.getEndTime().toLocalDate().isBefore(endDate))) {
                        available.add(res.getBoat());
                    }
                } else { unAvailable.add(res.getBoat()); }
            }
        }

        // ako ne postoji rezervacija i dobar je kapacitet, dodaj
        List<Boat> all = this.boatRepository.findAll();
        HashSet<Boat> allSet = new HashSet<>(all);

        HashSet<Boat> woReservation = new HashSet<>(allSet) {{ removeAll(withReservation); }};

        for (Boat b : woReservation) {
            if (!unAvailable.contains(b) && this.boatAvailable(startDate, endDate, b, numOfPersons)) { available.add(b); }
        }
        // TODO: proveri logiku
        available.removeIf(unAvailable::contains);

        return available;
    }

    @Override
    public List<Boat> findAllAvailableSorted(LocalDate startDate, LocalDate endDate, int numOfPersons, Boolean asc, Boolean price, Boolean rating) throws Exception {
        Set<Boat> set = this.findAllAvailable(startDate, endDate, numOfPersons);
        List<Boat> available = new ArrayList<>(set);

        if (asc && price && !rating) {
            available.sort(Comparator.comparing(Boat::getPrice));
        }
        else if (asc && !price && rating) {
            available.sort(Comparator.comparing(Boat::getAverageRating));
        }
        else if (!asc && price && !rating) {
            available.sort(Comparator.comparing(Boat::getPrice).reversed());
        }
        else if (!asc && !price && rating) {
            available.sort(Comparator.comparing(Boat::getAverageRating).reversed());
        }

        return available;
    }

    @Override
    public Set<Boat> findAllMyAvailable(LocalDate startDate, LocalDate endDate, int numOfPersons, Long id) throws Exception {
        Set<Boat> available = new HashSet<>();
        Set<Boat> unavailable = new HashSet<>();

        List<BoatReservation> reservations = this.reservationService.getAllMyAvailable(startDate, endDate, numOfPersons, id);
        for (BoatReservation res : reservations) {
            if (isByBoatOwner(id, res.getBoat()) && myBoatAvailable(startDate, endDate, res.getBoat(), id)) {
                available.add(res.getBoat());
            }
        }

        List<BoatReservation> un = this.reservationService.getAllMyUnavailable(startDate, endDate, id);
        for (BoatReservation r : un) {
            if (isByBoatOwner(id, r.getBoat())) {
                unavailable.add(r.getBoat());
            }
        }

        // ako ne postoji rezervacija i dobar je kapacitet, dodaj
        List<Boat> all = this.boatRepository.findByBoatOwner(id);
        HashSet<Boat> allSet = new HashSet<>(all);

        HashSet<Boat> woReservation = new HashSet<>(allSet) {{
            removeAll(available);
        }};

        for (Boat b : woReservation) {
//          boolean u = unavailable.contains(b);
            if (b.getNumPersons() >= numOfPersons && !unavailable.contains(b)
                    && this.myBoatAvailable(startDate, endDate, b, id)){
                available.add(b);
            }
        }
        available.removeIf(unavailable::contains);

        return available;
    }

    @Override
    public List<Boat> findAllMyAvailableSorted(Long oid, LocalDate startDate, LocalDate endDate, int numOfPersons,
                                               Boolean asc, Boolean price, Boolean rating) throws Exception {
        BoatOwner boatOwner = (BoatOwner) userService.getUserFromPrincipal();
        Set<Boat> set = this.findAllMyAvailable(startDate, endDate, numOfPersons, oid);
        List<Boat> available = new ArrayList<>(set);

        if (asc && price && !rating) {
            available.sort(Comparator.comparing(Boat::getPrice));
        }
        else if (asc && !price && rating) {
            available.sort(Comparator.comparing(Boat::getAverageRating));
        }
        else if (!asc && price && !rating) {
            available.sort(Comparator.comparing(Boat::getPrice).reversed());
        }
        else if (!asc && !price && rating) {
            available.sort(Comparator.comparing(Boat::getAverageRating).reversed());
        }

        return available;
    }
}
