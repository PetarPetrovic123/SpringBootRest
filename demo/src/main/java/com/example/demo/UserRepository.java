package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<Ime2, Integer>{
    Optional<Ime2> findByUsername(String username);

    List<Ime2> findByRole(String role);
}
