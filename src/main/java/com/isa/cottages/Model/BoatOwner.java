package com.isa.cottages.Model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("Boat_owner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoatOwner extends User {

    @Column
    private String explanationOfRegistration;

    @Column
    private UserRole userRole = UserRole.BOAT_OWNER;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime unavailableFrom;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime unavailableUntil;

    @Column
    private RegistrationType registrationType = RegistrationType.BOAT_ADVERTISER;

    @OneToMany(mappedBy = "boatOwner", cascade = CascadeType.ALL)
    private Set<Boat> boats = new HashSet<>();

    @OneToMany(mappedBy = "boatOwner", targetEntity = Report.class)
    private Set<Report> reports = new HashSet<>();
}
