package com.isa.cottages.Model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "users")
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "user_type")
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotEmpty(message = "This field can not be empty")
    private String firstName;

    @Column
    @NotEmpty(message = "This field can not be empty")
    private String lastName;

    @Column
    @NotEmpty(message = "This field can not be empty")
    private String email;

    @Column(nullable = false)
    @NotEmpty(message = "This field can not be empty")
    //@JsonIgnore
    private String password;

    @Column
    private String residence;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String phoneNumber;

    @Column
    private Boolean enabled = false;

    @Column
    private Boolean deleted = false;

    @Column
    private UserRole userRole;

    @Column
    private RegistrationType registrationType;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
