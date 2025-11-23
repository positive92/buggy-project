package com.example.buggyapp.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

// DEFECT: Entity class implements Serializable but doesn't have serialVersionUID
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DEFECT: Sensitive data without encryption
    private String password;

    // DEFECT: No input validation
    private String username;

    private String email;

    // DEFECT: Using java.util.Date instead of modern date/time API
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // DEFECT: Mutable public fields (should be private with getters/setters)
    public String role;

    // DEFECT: Should use boolean primitive instead of Boolean wrapper
    private Boolean isActive;

    // DEFECT: No-args constructor is public (security issue for JPA entities)
    public User() {
    }

    // DEFECT: Constructor doesn't validate parameters
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = new Date(); // DEFECT: Not thread-safe
    }

    // DEFECT: Getters and setters without any validation
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    // DEFECT: No null check or validation
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // DEFECT: Password setter doesn't hash the password
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        // DEFECT: Returning mutable Date object directly (defensive copying needed)
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    // DEFECT: equals() method doesn't check for null or type
    @Override
    public boolean equals(Object o) {
        User user = (User) o; // DEFECT: Potential ClassCastException
        return id.equals(user.id); // DEFECT: Potential NullPointerException
    }

    // DEFECT: hashCode() not consistent with equals()
    @Override
    public int hashCode() {
        return 0; // DEFECT: Always returns same hash code (performance issue)
    }

    // DEFECT: toString() exposes sensitive information (password)
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}
