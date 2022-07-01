package com.isa.cottages.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// @DiscriminatorValue("additional_service")
public class AdditionalService implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Double price = 0.0;

    @Column
    private Boolean deleted = false;

    @ManyToOne(targetEntity = Boat.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "boat_id", nullable = true, referencedColumnName = "id")
    private Boat boat;

    @ManyToOne(targetEntity = Cottage.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cottage_id", nullable = true, referencedColumnName = "id")
    private Cottage cottage;

    @ManyToOne(targetEntity = FishingInstructorAdventure.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "adventure_id", nullable = true, referencedColumnName = "id")
    private FishingInstructorAdventure adventure;

    @ManyToOne(targetEntity = Reservation.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reservation_id", nullable = true, referencedColumnName = "id")
    private Reservation reservation;
}
