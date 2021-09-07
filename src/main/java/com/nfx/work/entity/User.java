package com.nfx.work.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
@Table(name = "USER")
public class User {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 70)
    private String name;

    @NotNull
    @Size(max = 100)
    private String surname;

    @NotNull
    @Size(max = 11)
    private String pesel;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

}
