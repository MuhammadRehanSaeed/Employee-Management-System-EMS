package com.rehancode.ems.Repository;

import com.rehancode.ems.Model.UsersModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersModel,Long> {
    UsersModel findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
