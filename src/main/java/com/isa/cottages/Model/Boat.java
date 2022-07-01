package com.isa.cottages.Model;

import groovyjarjarantlr4.v4.runtime.misc.Array2DHashSet;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Boat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String boatName;

    @Column
    private EngineType engineType;

    @Column
    private Long length;

    @Column
    private Long engineNumber;

    @Column
    private Long enginePower;

    @Column
    private Long maxSpeed;

    @Column
    private String residence;

    @Column
    private String city;

    @Column
    private String state;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> imageUrl;

    @Column
    private Integer capacity;

    @Column
    private String rules;

    @Column
    private Double price = 0.0;

    @Column
    private Integer numPersons;

    @Column
    private String description;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableFrom;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime availableUntil;

    @Column
    private CancellationCondition cancellationCondition;

    @Column
    private Boolean deleted = false;

    @Column
    private Boolean reserved = false;

    @Column
    private Boolean available = true;

    @ManyToOne(targetEntity = BoatOwner.class)
    private BoatOwner boatOwner;

    @Column
    private Double averageRating = 0.0;

    @ElementCollection
    private Set<Integer> ratings;

    @ManyToMany(mappedBy = "boatSubscriptions")
    private Set<Client> subscribers;

    @OneToMany(mappedBy = "boat", targetEntity = AdditionalService.class)
    private Set<AdditionalService> additionalServices = new HashSet<>();

    @OneToMany(mappedBy = "boat", targetEntity = NavigationEquipment.class)
    private Set<NavigationEquipment> navigationEquipments = new HashSet<>();

    @OneToMany(mappedBy = "boat", targetEntity = FishingEquipment.class)
    private Set<FishingEquipment> fishingEquipments = new HashSet<>();

}
