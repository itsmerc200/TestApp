package com.quizserver.repository;

import com.quizserver.entities.User;
import com.quizserver.enums.userRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByRole(userRole role);

    User findFirstByEmail(String email);

    Optional<User> findByEmail(String email);


}
