package com.isa.cottages.Model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("cottage_reservation")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CottageReservation extends Reservation {


    @ManyToOne(targetEntity = CottageOwner.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cottage_owner_id", nullable = true, referencedColumnName = "id")
    private CottageOwner cottageOwner;

    @ManyToOne(targetEntity = Cottage.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cottage_id", nullable = true, referencedColumnName = "id")
    private Cottage cottage;

}
