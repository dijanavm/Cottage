package com.isa.cottages.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class FishingInstructorAdventure implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String adventureName;

    @Column
    @NonNull
    private String adventureResidence;

    @Column
    @NonNull
    private String adventureCity;

    @Column
    @NonNull
    private String adventureState;

    @Column
    private String adventureDescription;

    @Column
    @NonNull
    private String maxClients;

    @Column
    private String quickReservation = "No";

    @Column
    private String imageUrl;

    @Column
    private String conductRules;

    @Column
    private Double averageRating = 0.0;

    @Column
    private Double price = 0.0;

    @Column
    private Integer numPersons;

    @ElementCollection
    private Set<Integer> ratings;

    @Column
    private Boolean reserved;

    @Column
    private Boolean deleted = false;

    @Column
    @NonNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableFrom;

    @Column
    @NonNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableUntil;

    @Column
    private String gearIncluded;

    @Column
    private Double cancellationFeePercent;

    @Column
    private String instructorInfo;

    public FishingInstructorAdventure(String images) {
        this.imageUrl = imageUrl;
    }

    @ManyToOne(targetEntity = Instructor.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "instructor_id", nullable = true, referencedColumnName = "id")
    private Instructor instructor;

    @OneToMany(mappedBy = "adventure", targetEntity = AdventureReservation.class)
    private Set<AdventureReservation> adventureReservations = new HashSet<>();

    @ManyToMany(mappedBy = "instructorSubscriptions")
    private Set<Client> subscribers;

    @OneToMany(targetEntity = AdditionalService.class, mappedBy = "adventure")
    private Set<AdditionalService> additionalServices = new HashSet<>();
}
