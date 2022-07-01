package com.isa.cottages.Model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyProgram implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double reservationPoints = 0.0;

    @Column
    private Double regularPoints = 0.0;

    @Column
    private Double silverPoints = 0.0;

    @Column
    private Double goldPoints = 0.0;

    @Column
    private Integer discountRegular = 0;

    @Column
    private Integer discountSilver = 0;

    @Column
    private Integer discountGold = 0;

    @Column
    private Integer penalties = 0;

    @OneToOne(mappedBy = "loyaltyProgram")
    private Client client;
}
