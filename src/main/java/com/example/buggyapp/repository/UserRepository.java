package com.example.buggyapp.repository;

import com.example.buggyapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // DEFECT: SQL Injection vulnerability - concatenating user input directly
    @Query(value = "SELECT * FROM users WHERE username = '" + ":username" + "'", nativeQuery = true)
    User findByUsernameBuggy(@Param("username") String username);

    // DEFECT: Another SQL injection vulnerability
    @Query(value = "SELECT * FROM users WHERE email LIKE '%" + ":email" + "%'", nativeQuery = true)
    List<User> searchByEmailBuggy(@Param("email") String email);

    // DEFECT: Exposing sensitive password data in query
    @Query("SELECT u FROM User u WHERE u.password = :password")
    List<User> findByPassword(@Param("password") String password);

    // This one is correct but mixed with buggy ones
    User findByUsername(String username);
}
