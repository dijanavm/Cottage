package com.isa.cottages.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "reservation")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "reservation_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    @Column
    private LocalDateTime time = LocalDateTime.now();

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @Column
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;

    @Column
    private String startDateString;

    @Column
    private String endDateString;

    @Column
    private String startTimeString;

    @Column
    private String endTimeString;

    @Column
    private Boolean discount = false;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime discountAvailableFrom;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime discountAvailableUntil;

    @Column
    private Integer numPersons;

    @Column
    private Double price;

    @Column
    private Double income;

    @Column
    private Integer attendance;

    @Column
    private Double discountPrice = 0.0;

    @Column
    private Integer duration = 1;

    @Column
    private Boolean reserved = false;

    @Column
    private Boolean deleted = false;

    @ManyToOne(targetEntity = Client.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", nullable = true, referencedColumnName = "id")
    private Client client;

    @ManyToOne(targetEntity = SystemAdministrator.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id", nullable = true, referencedColumnName = "id")
    private SystemAdministrator admin;

    @OneToMany(targetEntity = AdditionalService.class, mappedBy = "reservation")
    private Set<AdditionalService> additionalServices = new HashSet<>();


//    public void CalculatePrice() {
//        Double sum = price;
//        if (this.discount && this.discountPrice != 0.0) {
//            sum = discountPrice;
//        }
//
//        for (AdditionalService s : this.additionalServices) {
//            sum += s.getPrice();
//        }
//        // this.calculateDuration();
//        this.price = sum;
//    }

    public void calculateDuration(LocalDate startDate, LocalDate endDate) throws ParseException {
        Period difference = Period.between(startDate, endDate);
        this.duration = difference.getDays() + 1;
    }
}
