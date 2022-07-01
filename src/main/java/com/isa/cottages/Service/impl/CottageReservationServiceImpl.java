package com.isa.cottages.Service.impl;

import com.isa.cottages.Email.EmailService;
import com.isa.cottages.Model.*;
import com.isa.cottages.Repository.CottageRepository;
import com.isa.cottages.Repository.CottageReservationRepository;
import com.isa.cottages.Service.CottageReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CottageReservationServiceImpl implements CottageReservationService {

    @Autowired
    private CottageReservationRepository reservationRepository;

    @Autowired
    private CottageRepository cottageRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EmailService emailService;

    @Override
    public CottageReservation findOne(Long id) {
        return reservationRepository.getOne(id);
    }

    @Override
    public List<CottageReservation> findByCottage(Long id) throws Exception {
        Cottage cottage = cottageRepository.findById(id).get();

        return this.reservationRepository.findByCottage(id);
    }

    @Override
    public List<CottageReservation> findNowAndUpcomingByCottage(Long id) throws Exception {
        Cottage cottage = cottageRepository.findById(id).get();

        List<CottageReservation> all = this.reservationRepository.getAllReservedByCottage(id);
        List<CottageReservation> upcoming = new ArrayList<>();

        for (CottageReservation res : all) {
            if( (res.getStartTime().isAfter(LocalDateTime.now()) || res.getStartTime().isBefore(LocalDateTime.now())
                    || res.getStartTime().isEqual(LocalDateTime.now()))
                    &&  res.getEndTime().isAfter(LocalDateTime.now())){
                upcoming.add(res);
            }
        }
        return upcoming;
    }


    @Override
    public List<CottageReservation> getAllOwnersReservations(Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();

        return this.reservationRepository.getAllOwnersReservations(id);
    }

    @Override
    public List<CottageReservation> getAllOwnersReservedReservations(Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();

        return this.reservationRepository.getAllOwnersReservedReservations(id);
    }

    @Override
    public List<CottageReservation> findByOrderByStartTimeAsc() throws Exception {
        List<CottageReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(CottageReservation::getStartTime));

        return pastOnes;
    }

    @Override
    public List<CottageReservation> findByOrderByStartTimeDesc() throws Exception {
        List<CottageReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(CottageReservation::getStartTime).reversed());

        return pastOnes;
    }

    @Override
    public List<CottageReservation> findByOrderByDurationAsc() throws Exception {
        List<CottageReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(CottageReservation::getDuration));

        return pastOnes;
    }

    @Override
    public List<CottageReservation> findByOrderByDurationDesc() throws Exception {
        List<CottageReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(CottageReservation::getDuration).reversed());

        return pastOnes;
    }

    @Override
    public List<CottageReservation> findByOrderByPriceAsc() throws Exception {
        List<CottageReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(CottageReservation::getPrice));

        return pastOnes;
    }

    @Override
    public List<CottageReservation> findByOrderByPriceDesc() throws Exception {
        List<CottageReservation> pastOnes = getPastReservations();
        pastOnes.sort(Comparator.comparing(CottageReservation::getPrice).reversed());

        return pastOnes;
    }

    @Override
    public void setDate(Reservation reservation) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(reservation.getStartDateString(), formatter);
        LocalDate ed = LocalDate.parse(reservation.getEndDateString(), formatter);

        reservation.setStartDate(sd);
        reservation.setEndDate(ed);
        reservation.setStartTime(sd.atStartOfDay());
        reservation.setEndTime(ed.atStartOfDay());
    }

    @Override
    public CottageReservation makeReservation(CottageReservation reservation, Cottage cottage) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();

        reservation.setCottage(cottage);
        reservation.setCottageOwner(cottage.getCottageOwner());
        reservation.setClient(client);
        reservation.setPrice(cottage.getPrice());
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
    public CottageReservation save(CottageReservation cottageReservation) { return this.reservationRepository.save(cottageReservation); }

    @Override
    public void sendReservationMail(CottageReservation reservation) {
        String to = reservation.getClient().getEmail();
        String topic = "Cottage Reservation";
        String body = "You successfully made cottage reservation. \n\n\n" +
                "\tCottage:\t" + reservation.getCottage().getName() + "\n" +
                "\tCottage Owner:\t" + reservation.getCottageOwner().getFullName() + "\n\n" +
                "\tStart date\t" + reservation.getStartDate().atStartOfDay().toLocalDate().toString() + "\n" +
                "\tEnd date\t" + reservation.getEndDate().atStartOfDay().toLocalDate().toString() + "\n\n" +
                "\tAddress:\t" + reservation.getCottageOwner().getResidence() + ", " +
                reservation.getCottageOwner().getState() + "\n" +
                "\tPrice:\t" + reservation.getPrice().toString() + "0  RSD\n";

        this.emailService.sendEmail(to, body, topic);
    }

    @Override
    public List<CottageReservation> getAllWithDiscount(Long CottageId) {
        List<CottageReservation> all = this.reservationRepository.findAllWithDiscount(CottageId);
        List<CottageReservation> upcoming = new ArrayList<>();

        for (CottageReservation res : all) {
            if (res.getStartTime().isAfter(LocalDateTime.now()) && (res.getEndTime().isAfter(LocalDateTime.now()))) {
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public CottageReservation update(CottageReservation reservation) {
        CottageReservation toUpdate = this.reservationRepository.getById(reservation.getId());

        toUpdate.setPrice(reservation.getPrice());
        toUpdate.setCottage(reservation.getCottage());
        toUpdate.setCottageOwner(reservation.getCottageOwner());
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
    public CottageReservation getOne(Long id) {
        return this.reservationRepository.getById(id);
    }

    @Override
    public CottageReservation makeReservationOnDiscount(Long reservationId) throws Exception {
        Client client = (Client) this.userService.getUserFromPrincipal();
        CottageReservation reservation = this.getOne(reservationId);

        reservation.setClient(client);
        reservation.setReserved(true);
        reservation.setCottageOwner(reservation.getCottage().getCottageOwner());
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
        CottageReservation reservation = this.getOne(id);

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
    public List<CottageReservation> getOwnersUpcomingReservations(Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        List<CottageReservation> all = this.reservationRepository.getAllReservedByOwner(id);
        List<CottageReservation> upcoming = new ArrayList<>();

        for (CottageReservation res: all) {
            if((res.getStartTime().isAfter(LocalDateTime.now())) && (res.getEndTime().isAfter(LocalDateTime.now())) &&
                    (Objects.equals(res.getCottageOwner().getId(), cottageOwner.getId()))) {
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<CottageReservation> getOwnersPastReservations(Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        List<CottageReservation> all = this.reservationRepository.getAllReservedByOwner(id);
        List<CottageReservation> pastOnes = new ArrayList<>();

        for (CottageReservation res:all) {
            if((res.getStartTime().isBefore(LocalDateTime.now())) && (res.getEndTime().isBefore(LocalDateTime.now()))
                    && (Objects.equals(res.getCottageOwner().getId(), cottageOwner.getId()))) {
                pastOnes.add(res);
            }
        }
        return pastOnes;
    }

    @Override
    public List<CottageReservation> getAllOwnersNowAndUpcomingReservations(Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        List<CottageReservation> all = this.reservationRepository.getAllOwnersReservations(id);
        List<CottageReservation> upcoming = new ArrayList<>();

        for (CottageReservation res : all) {
            if( (res.getStartTime().isAfter(LocalDateTime.now()) || res.getStartTime().isBefore(LocalDateTime.now())
                    || res.getStartTime().isEqual(LocalDateTime.now()))
                    &&  res.getEndTime().isAfter(LocalDateTime.now())) {
                upcoming.add(res);
            }
        }

        return upcoming;
    }

    @Override
    public CottageReservation saveDiscount(CottageReservation cottageReservation) throws Exception {
        CottageReservation cr = new CottageReservation();

        cr.setDiscountAvailableFrom(cottageReservation.getDiscountAvailableFrom());
        cr.setDiscountAvailableUntil(cottageReservation.getDiscountAvailableUntil());
        cr.setStartTime(cottageReservation.getDiscountAvailableFrom());
        cr.setEndTime(cottageReservation.getDiscountAvailableUntil());
        cr.setStartDate(cottageReservation.getDiscountAvailableFrom().toLocalDate());
        cr.setEndDate(cottageReservation.getDiscountAvailableUntil().toLocalDate());
        cr.setNumPersons(cottageReservation.getNumPersons());
        cr.setDiscountPrice(cottageReservation.getDiscountPrice());
        cr.setAdditionalServices(cottageReservation.getAdditionalServices());
        cr.setCottageOwner(cottageReservation.getCottageOwner());
        cr.setCottage(cottageReservation.getCottage());
        cr.setDiscount(true);
        cr.setDeleted(false);
        cr.setReserved(false);
        this.reservationRepository.save(cr);

        return cr;
    }

    @Override
    public List<CottageReservation> findDiscountsByCottage(Long id) {
        return this.reservationRepository.findDiscountsByCottage(id);

    }

    @Override
    public List<CottageReservation> getUpcomingReservations() throws Exception {
        Client cl = (Client) this.userService.getUserFromPrincipal();
        List<CottageReservation> all = this.reservationRepository.getAllReserved();
        List<CottageReservation> upcoming = new ArrayList<>();

        for (CottageReservation res: all) {
            if((res.getStartTime().isAfter(LocalDateTime.now())) && (res.getEndTime().isAfter(LocalDateTime.now()))
                    && (Objects.equals(res.getClient().getId(), cl.getId()))) {
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<CottageReservation> getAllUpcoming() {

        List<CottageReservation> all = this.reservationRepository.getAllReserved();
        List<CottageReservation> upcoming = new ArrayList<>();

        for (CottageReservation res: all) {
            if(res.getStartTime().isAfter(LocalDateTime.now()) && res.getEndTime().isAfter(LocalDateTime.now())) {
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<CottageReservation> getPastReservations() throws Exception {
        Client cl = (Client) this.userService.getUserFromPrincipal();
        List<CottageReservation> all = this.reservationRepository.getAllReserved();
        List<CottageReservation> pastOnes = new ArrayList<>();

        for (CottageReservation res: all) {
            if((res.getStartTime().isBefore(LocalDateTime.now())) && (res.getEndTime().isBefore(LocalDateTime.now())) &&
                    (Objects.equals(res.getClient().getId(), cl.getId()))) {
                pastOnes.add(res);
            }
        }
        return pastOnes;
    }

    @Override
    public List<CottageReservation> findAllByClient(Client client){
        return this.reservationRepository.findAllByClient(client.getId());
    }

    @Override
    public List<CottageReservation> findClientForCalendar(String keyword, Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        List<CottageReservation> all = this.reservationRepository.findClientForCalendar(keyword);
        List<CottageReservation> upcoming = new ArrayList<>();

        for (CottageReservation res : all) {
            if( (res.getStartTime().isAfter(LocalDateTime.now()) || res.getStartTime().isBefore(LocalDateTime.now())
                    || res.getStartTime().isEqual(LocalDateTime.now()))
                    &&  res.getEndTime().isAfter(LocalDateTime.now())
                    && (Objects.equals(res.getCottageOwner().getId(), cottageOwner.getId()))
            ){
                upcoming.add(res);
            }
        }
        return upcoming;
    }

    @Override
    public List<CottageReservation> findClientForHistory(String keyword, Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();

        List<CottageReservation> all = this.reservationRepository.findClientForHistory(keyword);
        List<CottageReservation> pastOnes = new ArrayList<>();

        for (CottageReservation res:all) {
            if((res.getStartTime().isBefore(LocalDateTime.now())) && (res.getEndTime().isBefore(LocalDateTime.now()))
                    && (Objects.equals(res.getCottageOwner().getId(), cottageOwner.getId()))) {
                pastOnes.add(res);
            }
        }
        return pastOnes;
    }

    @Override
    public Set<CottageReservation> findByInterval(LocalDate startDate, LocalDate endDate, Long id) throws Exception{
        List<CottageReservation> reservations = this.getOwnersPastReservations(id);
        Set<CottageReservation> filtered = new HashSet<>();

        for(CottageReservation res: reservations){
            if(res.getStartDate().isAfter(startDate) && res.getEndDate().isBefore(endDate)) {
                filtered.add(res);
            }
        }
        return filtered;
    }

    @Override
    public Set<CottageReservation> findByInterval2(LocalDate startDate, LocalDate endDate, Long id) throws Exception{
        List<CottageReservation> reservations = this.getOwnersPastReservations(id);
        Set<CottageReservation> filtered = new HashSet<>();
        Double attendance = 0.0;

        for(CottageReservation res: reservations){
            if(res.getStartDate().isAfter(startDate) && res.getEndDate().isBefore(endDate)) {
                filtered.add(res);
            }
        }

        return filtered;
    }

    @Override
    public void setDate(CottageReservation reservation) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sd = LocalDate.parse(reservation.getStartDateString(), formatter);
        LocalDate ed = LocalDate.parse(reservation.getEndDateString(), formatter);

        reservation.setStartDate(sd);
        reservation.setEndDate(ed);
        reservation.setStartTime(sd.atStartOfDay());
        reservation.setEndTime(ed.atStartOfDay());
    }

    @Override
    public List<CottageReservation> getAllMyAvailable(LocalDate desiredStart, LocalDate desiredEnd, int capacity, Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        List<CottageReservation> all = this.reservationRepository.findAllMyAvailable(desiredStart, desiredEnd, capacity, id);
        List<CottageReservation> filtered = new ArrayList<>();

        for (CottageReservation res:all) {
            if(Objects.equals(res.getCottageOwner().getId(), cottageOwner.getId())) {
                filtered.add(res);
            }
        }
        return filtered;
    }

    @Override
    public List<CottageReservation> getAllMyUnavailable(LocalDate desiredStart, LocalDate desiredEnd, Long id) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        List<CottageReservation> all = this.reservationRepository.findAllMyUnavailable(desiredStart, desiredEnd, id);
        List<CottageReservation> filtered = new ArrayList<>();

        for (CottageReservation res:all) {
            if(Objects.equals(res.getCottageOwner().getId(), cottageOwner.getId())) {
                filtered.add(res);
            }
        }
        return filtered;
    }


    @Override
    public CottageReservation makeReservationWithClient(CottageReservation reservation, Cottage cottage, Long clid) throws Exception {
        CottageOwner cottageOwner = (CottageOwner) this.userService.getUserFromPrincipal();
        Client client = (Client) userService.findById(clid);

        reservation.setCottage(cottage);
        reservation.setCottageOwner(cottage.getCottageOwner());
        reservation.setClient(client);
        reservation.setPrice(cottage.getPrice());
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
    public Double CalculatePrice(CottageReservation reservation) throws ParseException {
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
