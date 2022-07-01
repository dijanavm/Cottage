package com.isa.cottages.Model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("Client")
public class Client extends User {

    @Column
    private Double loyaltyPoints;

    @Column
    private Integer discount;

    @Column
    private Integer penalties = 0;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "loyalty_program_id", referencedColumnName = "id")
    private LoyaltyProgram  loyaltyProgram;

    @OneToMany(mappedBy = "client", targetEntity = Reservation.class)
    private Set<Reservation> Reservations = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "boat_subscribers", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "boat_id"))
    private Set<Boat> boatSubscriptions = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "cottage_subscribers", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "cottage_id"))
    private Set<Cottage> cottageSubscriptions = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "instructor_subscribers", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "instructor_id"))
    private Set<FishingInstructorAdventure> instructorSubscriptions = new HashSet<>();

    @OneToMany(mappedBy = "client", targetEntity = Report.class)
    private Set<Report> reports = new HashSet<>();
}
