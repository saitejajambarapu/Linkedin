package com.project.linkedin.userService.repository;

import com.project.linkedin.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
