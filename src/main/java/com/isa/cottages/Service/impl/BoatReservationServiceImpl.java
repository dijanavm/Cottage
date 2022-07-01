package com.isa.cottages.Service.impl;

import com.isa.cottages.Email.EmailService;
import com.isa.cottages.Model.*;
import com.isa.cottages.Repository.BoatRepository;
import com.isa.cottages.Repository.BoatReservationRepository;
import com.isa.cottages.Service.BoatReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BoatReservationServiceImpl implements BoatReservationService {

    private UserServiceImpl userService;
    private BoatReservationRepository reservationRepository;
    private BoatRepository boatRepository;
    private EmailService emailService;

    @Autowired
    public BoatReservationServiceImpl(UserServiceImpl userService,
                                      BoatReservationRepository reservationRepository, BoatRepository boatRepository,
                                      EmailService emailService) {
        this.userService = userService;
        this.reservationRepository = reservationRepository;
        this.boatRepository = boatRepository;
        this.emailService = emailService;
    }

    @Override
    public BoatReservation findById(Long id) throws Exception{
        if (this.reservationRepository.findById(id).isEmpty()) {
            throw new Exception("No such value(BoatReservation service)");
        }
        return this.reservationRepository.findById(id).get();
    }

    @Override
    public List<BoatReservation> findNowAndUpcomingByBoat(Long id) throws Exception {
        Boat boat = boatRepository.findById(id).get();

        List<BoatReservation> all = this.reservationRepository.getAllReservedByBoat(id);
        List<BoatReservation> upcoming = new ArrayList<>();

        for (BoatReservation res : all) {
            if( (res.getStartTime().isAfter(LocalDateTime.now()) || res.getStartTime().isBefore(LocalDateTime.now())
                    || res.getStartTime().isEqual(LocalDateTime.now()))
                    &&  res.getEndTime().isAfter(LocalDateTime.now())){
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<BoatReservation> getAllOwnersReservations(Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();

        return this.reservationRepository.getAllOwnersReservations(id);
    }

    @Override
    public List<BoatReservation> getAllOwnersReservedReservations(Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();

        return this.reservationRepository.getAllOwnersReservedReservations(id);
    }

    @Override
    public List<BoatReservation> findByOrderByStartTimeAsc() throws Exception {
        List<BoatReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(BoatReservation::getStartTime));

        return pastOnes;
    }

    @Override
    public List<BoatReservation> findByOrderByStartTimeDesc() throws Exception {
        List<BoatReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(BoatReservation::getStartTime).reversed());

        return pastOnes;
    }

    @Override
    public List<BoatReservation> findByOrderByDurationAsc() throws Exception {
        List<BoatReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(BoatReservation::getDuration));

        return pastOnes;
    }

    @Override
    public List<BoatReservation> findByOrderByDurationDesc() throws Exception {
        List<BoatReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(BoatReservation::getDuration).reversed());

        return pastOnes;
    }

    @Override
    public List<BoatReservation> findByOrderByPriceAsc() throws Exception {
        List<BoatReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(BoatReservation::getPrice));

        return pastOnes;
    }

    @Override
    public List<BoatReservation> findAllAvailable(LocalDate startTime, LocalDate endTime) {
        List<BoatReservation> all = this.reservationRepository.getAllReservations();
        List<BoatReservation> available = new ArrayList<>();

        for (BoatReservation res : all) {
            if ((res.getStartTime().isAfter(ChronoLocalDateTime.from(startTime)) && res.getEndTime().isAfter(ChronoLocalDateTime.from(endTime))) ||
                            (res.getStartTime() == null && res.getEndTime() == null) ||
                            (res.getStartTime().isBefore(ChronoLocalDateTime.from(startTime)) && res.getEndTime().isBefore(ChronoLocalDateTime.from(endTime)))) {
                available.add(res);
            }
        }
        return available;
    }

    @Override
    public List<BoatReservation> getAllWithDiscount(Long boatId) {
        List<BoatReservation> all = this.reservationRepository.findAllWithDiscount(boatId);
        List<BoatReservation> upcoming = new ArrayList<>();

        for (BoatReservation res : all) {
            if (res.getStartTime().isAfter(LocalDateTime.now()) && (res.getEndTime().isAfter(LocalDateTime.now()))) {
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public BoatReservation getOne(Long id) {
        return this.reservationRepository.getById(id);
    }

    @Override
    public BoatReservation update(BoatReservation reservation) {
        BoatReservation toUpdate = this.reservationRepository.getById(reservation.getId());

        toUpdate.setPrice(reservation.getPrice());
        toUpdate.setBoat(reservation.getBoat());
        toUpdate.setBoatOwner(reservation.getBoatOwner());
        toUpdate.setReserved(reservation.getReserved());
        toUpdate.setStartTime(reservation.getStartTime());
        toUpdate.setEndTime(reservation.getEndTime());
        toUpdate.setClient(reservation.getClient());
        toUpdate.setNumPersons(reservation.getNumPersons());
        toUpdate.setStartDate(reservation.getStartDate());
        toUpdate.setEndDate(reservation.getEndDate());
        toUpdate.setDuration(reservation.getDuration());
        toUpdate.setAdditionalServices(reservation.getAdditionalServices());
        toUpdate.setDiscountPrice(reservation.getDiscountPrice());
        toUpdate.setDiscountAvailableFrom(reservation.getDiscountAvailableFrom());
        toUpdate.setDiscountAvailableUntil(reservation.getDiscountAvailableUntil());

        this.reservationRepository.save(toUpdate);
        return toUpdate;
    }

    @Override
    public List<BoatReservation> getAllUpcoming() {
        List<BoatReservation> all = this.reservationRepository.getAllReservations();
        List<BoatReservation> upcoming = new ArrayList<>();

        for (BoatReservation res : all) {
            if ((res.getStartTime().isAfter(LocalDateTime.now())) && (res.getEndTime().isAfter(LocalDateTime.now()))) {
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<BoatReservation> findByOrderByPriceDesc() throws Exception {
        List<BoatReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(BoatReservation::getPrice).reversed());

        return pastOnes;
    }

    @Override
    public List<BoatReservation> findAllByClient(Client client) {
        return this.reservationRepository.findAllByClient(client.getId());
    }

    @Override
    public BoatReservation save(BoatReservation reservation) {
        return this.reservationRepository.save(reservation);
    }
/*
    public double getDiscountPrice(Double price) throws Exception {
        return (1 - this.clientService.getDiscount()) * price;
    }
*/
    @Override
    public BoatReservation makeReservation(BoatReservation reservation, Boat boat) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();

        reservation.setBoat(boat);
        reservation.setBoatOwner(boat.getBoatOwner());
        reservation.setClient(client);
        reservation.setPrice(boat.getPrice());
        reservation.setReserved(true);
        this.setDate(reservation);
        reservation.calculateDuration(reservation.getStartDate(),
                reservation.getEndDate());

        Double price = this.CalculatePrice(reservation);
        reservation.setPrice(price);
        this.save(reservation);

        this.sendReservationMail(reservation);

        return reservation;
    }

    @Override
    public BoatReservation makeReservationOnDiscount(Long reservationId) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        BoatReservation reservation = this.getOne(reservationId);

        reservation.setClient(client);
        reservation.setReserved(true);
        reservation.setBoatOwner(reservation.getBoatOwner());
        reservation.calculateDuration(reservation.getStartDate(),
                reservation.getEndDate());
        // Double price = this.CalculatePrice(reservation);
        // reservation.setPrice(price);
        this.update(reservation);

        this.sendReservationMail(reservation);

        return reservation;
    }

    @Override
    public Boolean canCancel(Long id) {
        if (this.getOne(id).getStartTime().isAfter(LocalDateTime.now().plusDays(3))) {
            return true;
        }
        return false;
    }

    @Override
    public void cancel(Long id) {
        BoatReservation reservation = this.getOne(id);

        if(reservation.getDiscount()) {
            reservation.setClient(null);
            reservation.setReserved(false);
            this.update(reservation);
        }
        else {
            reservation.setDeleted(true);
            this.update(reservation);
        }
    }

    @Override
    public void deleteById(Long id) {
        this.deleteById(id);
    }

    @Override
    public void sendReservationMail(BoatReservation reservation) {
        String to = reservation.getClient().getEmail();
        String topic = "Boat Reservation";
        String body = "You successfully made boat reservation. \n\n\n" +
                "\tBoat:\t" + reservation.getBoat().getBoatName() + "\n" +
                "\tBoat Owner:\t" + reservation.getBoatOwner().getFullName() + "\n\n" +
                "\tStart date\t" + reservation.getStartDate().atStartOfDay().toLocalDate().toString() + "\n" +
                "\tEnd date\t" + reservation.getEndDate().atStartOfDay().toLocalDate().toString() + "\n\n" +
                "\tAddress:\t" + reservation.getBoatOwner().getResidence() + ", " +
                reservation.getBoatOwner().getState() + "\n" +
                "\tPrice:\t" + reservation.getPrice().toString() + "0  RSD\n";

        this.emailService.sendEmail(to, body, topic);
    }

    @Override
    public List<BoatReservation> getOwnersUpcomingReservations(Long id) throws Exception {
       BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.getAllReservedByOwner(id);
        List<BoatReservation> upcoming = new ArrayList<>();

        for (BoatReservation res: all) {
            if((res.getStartTime().isAfter(LocalDateTime.now())) && (Objects.equals(res.getBoatOwner().getId(), boatOwner.getId()))) {
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<BoatReservation> getAllOwnersNowAndUpcomingReservations(Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.getAllOwnersReservations(id);
        List<BoatReservation> upcoming = new ArrayList<>();

        for (BoatReservation res : all) {
            if( (res.getStartTime().isAfter(LocalDateTime.now()) || res.getStartTime().isBefore(LocalDateTime.now())
            || res.getStartTime().isEqual(LocalDateTime.now()))
                    &&  res.getEndTime().isAfter(LocalDateTime.now())){
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<BoatReservation> findClientForCalendar(String keyword, Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.findClientForCalendar(keyword);
        List<BoatReservation> upcoming = new ArrayList<>();

        for (BoatReservation res : all) {
            if( (res.getStartTime().isAfter(LocalDateTime.now()) || res.getStartTime().isBefore(LocalDateTime.now())
                    || res.getStartTime().isEqual(LocalDateTime.now()))
                    &&  res.getEndTime().isAfter(LocalDateTime.now())
                    && (Objects.equals(res.getBoatOwner().getId(), boatOwner.getId()))){
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<BoatReservation> getOwnersPastReservations(Long id) throws Exception {

        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.getAllReservedByOwner(id);
        List<BoatReservation> pastOnes = new ArrayList<>();

        for (BoatReservation res:all) {
            if((res.getStartTime().isBefore(LocalDateTime.now())) && (res.getEndTime().isBefore(LocalDateTime.now()))
            && (Objects.equals(res.getBoatOwner().getId(), boatOwner.getId()))) {
                pastOnes.add(res);
            }
        }
        return pastOnes;
    }

    @Override
    public List<BoatReservation> findClientForHistory(String keyword, Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();

        List<BoatReservation> all = this.reservationRepository.findClientForHistory(keyword);
        List<BoatReservation> pastOnes = new ArrayList<>();

        for (BoatReservation res:all) {
            if((res.getStartTime().isBefore(LocalDateTime.now())) && (res.getEndTime().isBefore(LocalDateTime.now()))
                    && (Objects.equals(res.getBoatOwner().getId(), boatOwner.getId()))) {
                pastOnes.add(res);
            }
        }
        return pastOnes;
    }

    @Override
    public List<BoatReservation> getOwnersFreeReservations(Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.getAllFreeReservationsByOwner(id);

        return all;
    }

    @Override
    public List<BoatReservation> getPastReservations() throws Exception {
        Client cl = (Client) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.getAllReservations();
        List<BoatReservation> pastOnes = new ArrayList<>();

        for (BoatReservation res : all) {
            if ((res.getStartTime().isBefore(LocalDateTime.now())) && (res.getEndTime().isBefore(LocalDateTime.now()))
                    && (Objects.equals(res.getClient().getId(), cl.getId()))) {
                pastOnes.add(res);
            }
        }
        return pastOnes;
    }

    @Override
    public List<BoatReservation> getUpcomingReservations() throws Exception {
        Client cl = (Client) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.getAllReservations();
        List<BoatReservation> upcoming = new ArrayList<>();

        for (BoatReservation res : all) {
            if ((res.getStartTime().isAfter(LocalDateTime.now())) && (res.getEndTime().isAfter(LocalDateTime.now()))
                    && (Objects.equals(res.getClient().getId(), cl.getId()))) {
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public BoatReservation saveDiscount(BoatReservation boatReservation) throws Exception{
        BoatReservation br = new BoatReservation();

        br.setDiscountAvailableFrom(boatReservation.getDiscountAvailableFrom());
        br.setDiscountAvailableUntil(boatReservation.getDiscountAvailableUntil());
        br.setStartTime(boatReservation.getDiscountAvailableFrom());
        br.setEndTime(boatReservation.getDiscountAvailableUntil());
        br.setStartDate(boatReservation.getDiscountAvailableFrom().toLocalDate());
        br.setEndDate(boatReservation.getDiscountAvailableUntil().toLocalDate());
        br.setNumPersons(boatReservation.getNumPersons());
        br.setDiscountPrice(boatReservation.getDiscountPrice());
        br.setAdditionalServices(boatReservation.getAdditionalServices());
        br.setBoatOwner(boatReservation.getBoatOwner());
        br.setBoat(boatReservation.getBoat());
        br.setDiscount(true);
        br.setDeleted(false);
        br.setReserved(false);
        this.reservationRepository.save(br);

        return br;
    }

    @Override
    public BoatReservation saveReservation(BoatReservation boatReservation) {
        BoatReservation br = new BoatReservation();

        br.setNumPersons(boatReservation.getNumPersons());
        br.setPrice(boatReservation.getPrice());
        br.setAdditionalServices(boatReservation.getAdditionalServices());
        br.setBoatOwner(boatReservation.getBoatOwner());
        br.setBoat(boatReservation.getBoat());
        br.setDiscount(false);
        br.setDeleted(false);
        br.setReserved(false);
        br.setClient(boatReservation.getClient());
        this.reservationRepository.save(br);

        return br;
    }

    @Override
    public List<BoatReservation> findDiscountsByBoat(Long id) throws Exception{
        return this.reservationRepository.findDiscountsByBoat(id);
    }

    @Override
    public Set<BoatReservation> findByInterval(LocalDate startDate, LocalDate endDate, Long id) throws Exception{
        List<BoatReservation> reservations = this.getOwnersPastReservations(id);
        Set<BoatReservation> filtered = new HashSet<>();
        Double income = 0.0;

        for (BoatReservation res : reservations) {
            if (res.getStartDate().isAfter(startDate) && res.getEndDate().isBefore(endDate)) {
                filtered.add(res);
            }
        }
        return filtered;
    }

    @Override
    public Set<BoatReservation> findByInterval2(LocalDate startDate, LocalDate endDate, Long id) throws Exception{
        List<BoatReservation> reservations = this.getOwnersPastReservations(id);
        Set<BoatReservation> filtered = new HashSet<>();
        Double attendance = 0.0;

        for(BoatReservation res: reservations){
            if(res.getStartDate().isAfter(startDate) && res.getEndDate().isBefore(endDate)) {
                filtered.add(res);
            }
        }

        return filtered;
    }


    @Override
    public void setDate(BoatReservation reservation) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(reservation.getStartDateString(), formatter);
        LocalDate ed = LocalDate.parse(reservation.getEndDateString(), formatter);

        reservation.setStartDate(sd);
        reservation.setEndDate(ed);
        reservation.setStartTime(sd.atStartOfDay());
        reservation.setEndTime(ed.atStartOfDay());
    }
    
    @Override
    public List<BoatReservation> getAllMyAvailable(LocalDate desiredStart, LocalDate desiredEnd, int capacity, Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.findAllMyAvailable(desiredStart, desiredEnd, capacity, id);
        List<BoatReservation> filtered = new ArrayList<>();

        for (BoatReservation res:all) {
            if(Objects.equals(res.getBoatOwner().getId(), boatOwner.getId())) {
                filtered.add(res);
            }
        }
        return filtered;
    }

    @Override
    public List<BoatReservation> getAllMyUnavailable(LocalDate desiredStart, LocalDate desiredEnd, Long id) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        List<BoatReservation> all = this.reservationRepository.findAllMyUnavailable(desiredStart, desiredEnd, id);
        List<BoatReservation> filtered = new ArrayList<>();

        for (BoatReservation res:all) {
            if(Objects.equals(res.getBoatOwner().getId(), boatOwner.getId())) {
                filtered.add(res);
            }
        }
        return filtered;
    }

    @Override
    public BoatReservation makeReservationWithClient(BoatReservation reservation, Boat boat, Long clid) throws Exception {
        BoatOwner boatOwner = (BoatOwner) this.userService.getUserFromPrincipal();
        Client client = (Client) userService.findById(clid);

        reservation.setBoat(boat);
        reservation.setBoatOwner(boat.getBoatOwner());
        reservation.setClient(client);
        reservation.setPrice(boat.getPrice());
        reservation.setReserved(true);
        this.setDate(reservation);
        reservation.calculateDuration(reservation.getStartDate(),
                reservation.getEndDate());

        Double price = this.CalculatePrice(reservation);
        reservation.setPrice(price);
        this.save(reservation);

        return reservation;
    }

    @Override
    public Double CalculatePrice(BoatReservation reservation) throws ParseException {
        Double sum = reservation.getPrice();
        if (reservation.getDiscount() && reservation.getDiscountPrice() != 0.0) {
            sum = reservation.getDiscountPrice();
        }

        for (AdditionalService s : reservation.getAdditionalServices()) {
            sum += s.getPrice();
        }
        return sum * reservation.getDuration();
    }
}


