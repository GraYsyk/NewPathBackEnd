package com.graysenko.NewPathBackEnd.Repositories.User;

import com.graysenko.NewPathBackEnd.Entities.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
