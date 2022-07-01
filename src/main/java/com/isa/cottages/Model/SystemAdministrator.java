package com.isa.cottages.Model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("System_administrator")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SystemAdministrator extends User{
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LoyaltyProgram> loyaltyPrograms = new HashSet<>();

    @Column
    private Boolean isFirstLogin = false;

    @OneToMany(mappedBy = "admin", targetEntity = Report.class)
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "admin", targetEntity = Reservation.class)
    private Set<Reservation> Reservations = new HashSet<>();

}
