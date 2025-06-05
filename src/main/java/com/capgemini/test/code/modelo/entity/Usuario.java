package com.capgemini.test.code.modelo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String dni;
    private String phone;
    private String role;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;


}
