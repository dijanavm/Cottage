package com.isa.cottages.Model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.lang.String;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Cottage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String residence;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String promotionalDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> imageUrl;

    @Column
    private Integer numberOfRooms = 0;

    @Column
    private Integer numberOfBeds = 0;

    @Column
    private Integer numPersons = numberOfBeds * numberOfRooms;

    @Column
    private String rules;

    @Column
    private Boolean reserved;

    @Column
    private Boolean deleted = false;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableFrom;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableUntil;
    
    @Column
    private Double averageRating = 0.0;

    @Column
    private Double price = 0.0;

    @ElementCollection
    private Set<Integer> ratings;

    @ManyToOne(targetEntity = CottageOwner.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="cottage_owner_id", nullable=true, referencedColumnName = "id")
    private CottageOwner cottageOwner;

    @OneToMany(mappedBy = "cottage", targetEntity = CottageReservation.class)
    private Set<CottageReservation> cottageReservations = new HashSet<>();

    @ManyToMany(mappedBy = "cottageSubscriptions")
    private Set<Client> subscribers;

    @OneToMany(mappedBy = "cottage", targetEntity = AdditionalService.class)
    private Set<AdditionalService> additionalServices = new HashSet<>();

    public void addAdditionalService(AdditionalService additionalService) {
        this.additionalServices.add(additionalService);
    }

}