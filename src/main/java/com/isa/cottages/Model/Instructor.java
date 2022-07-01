package com.isa.cottages.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("Instructor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Instructor extends User {

    @Column
    private String explanationOfRegistration;

    @Column
    private UserRole userRole = UserRole.INSTRUCTOR;

    @OneToMany(mappedBy = "instructor", targetEntity = FishingInstructorAdventure.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<FishingInstructorAdventure> adventures = new HashSet<>();

    @OneToMany(mappedBy = "instructor", targetEntity = AdventureReservation.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AdventureReservation> adventureReservations = new HashSet<>();

    @Column
    private RegistrationType registrationType;
}
