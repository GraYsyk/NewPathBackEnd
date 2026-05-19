package com.graysenko.NewPathBackEnd.Entities.User;

import com.graysenko.NewPathBackEnd.Entities.CartItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    private String address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    public User(String name, String email, String password, Collection<Role> roles, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.address = address;
    }
}
