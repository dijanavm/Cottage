package com.isa.cottages.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("boat_reservation")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoatReservation extends Reservation{

    @ManyToOne(targetEntity = BoatOwner.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "boat_owner_id", nullable = true, referencedColumnName = "id")
    private BoatOwner boatOwner;

    @ManyToOne(targetEntity = Boat.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "boat_id", nullable = true, referencedColumnName = "id")
    private Boat boat;

    public BoatReservation(LocalDate startDate, LocalDate endDate, Integer numPersons, Boolean reserved, Boat boat, Client client) {
        this.setNumPersons(numPersons);
        // this.setStartTime(LocalDateTime.from(startDate));
        // this.setEndTime(LocalDateTime.from(endDate));
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setReserved(reserved);
        this.setBoat(boat);
        this.setClient(client);

        this.setPrice(boat.getPrice());
//        this.CalculatePrice();
    }

    public  BoatReservation(String startDate, String endDate, int numPersons, Boat boat, Client client) {
        this.setStartDateString(startDate);
        this.setEndDateString(endDate);
        this.setNumPersons(numPersons);
        this.setBoat(boat);
        this.setBoatOwner(boat.getBoatOwner());
        this.setClient(client);
    }
}
