package com.graysenko.NewPathBackEnd.Entities.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(unique = true, name = "name")
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    public Role(String name, Collection<User> users) {
        this.name = name;
        this.users = users;
    }
}
