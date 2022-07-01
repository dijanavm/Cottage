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
@DiscriminatorValue("fishing_equipment")
public class FishingEquipment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    private String name;

    @Column
    private Boolean deleted = false;

    @ManyToOne(targetEntity = Boat.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="boat_id", nullable=true, referencedColumnName = "id")
    private Boat boat;
}
