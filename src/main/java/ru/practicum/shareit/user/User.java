package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users", schema="public")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Email
    @Column(name = "email", nullable = false, length = 512)
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
