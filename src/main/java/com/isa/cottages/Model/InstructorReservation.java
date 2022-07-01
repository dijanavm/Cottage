package com.isa.cottages.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@DiscriminatorValue("instructor_reservation")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InstructorReservation extends Reservation{

    @ManyToOne(targetEntity = FishingInstructorAdventure.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "instructor_id", nullable = true, referencedColumnName = "id")
    private FishingInstructorAdventure fishingInstructorAdventure;
}
