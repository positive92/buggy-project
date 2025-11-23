package com.example.buggyapp.service;

import com.example.buggyapp.model.User;
import com.example.buggyapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    // DEFECT: Field injection instead of constructor injection
    @Autowired
    private UserRepository userRepository;

    // DEFECT: Static mutable field (thread-safety issue)
    private static int userCount = 0;

    // DEFECT: Public static mutable list (security and thread-safety issue)
    public static List<String> activeUsers = new ArrayList<>();

    // DEFECT: Random instance creation in method (should be reused)
    private Random random = new Random();

    // DEFECT: Method too long and does too many things
    public User createUser(String username, String password, String email) {
        // DEFECT: No input validation
        User user = new User();
        user.setUsername(username);

        // DEFECT: Storing password in plain text
        user.setPassword(password);
        user.setEmail(email);
        user.setCreatedAt(new Date());
        user.setActive(true);

        // DEFECT: Incrementing static variable without synchronization
        userCount++;

        // DEFECT: Adding to static list without synchronization
        activeUsers.add(username);

        // DEFECT: Potential NullPointerException - no null check
        User savedUser = userRepository.save(user);

        // DEFECT: Writing to file system without proper error handling
        try {
            FileWriter writer = new FileWriter("/tmp/users.log", true);
            writer.write(username + " created at " + new Date() + "\n");
            writer.close(); // DEFECT: Not using try-with-resources
        } catch (IOException e) {
            // DEFECT: Printing stack trace instead of proper logging
            e.printStackTrace();
        }

        return savedUser;
    }

    // DEFECT: Returning null instead of Optional
    public User getUserById(Long id) {
        // DEFECT: Using orElse(null) instead of proper Optional handling
        return userRepository.findById(id).orElse(null);
    }

    // DEFECT: Exposing sensitive password comparison
    public boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        // DEFECT: Comparing passwords with == instead of equals()
        // DEFECT: No null check before accessing user object
        // DEFECT: Timing attack vulnerability - string comparison
        if (user.getPassword() == password) {
            return true;
        }

        return false;
    }

    // DEFECT: Method has side effects despite name suggesting query
    public List<User> getAllUsers() {
        // DEFECT: Modifying static state in a getter method
        userCount = userRepository.findAll().size();
        return userRepository.findAll();
    }

    // DEFECT: Generic Exception thrown
    public void deleteUser(Long id) throws Exception {
        // DEFECT: No validation if user exists
        // DEFECT: Throwing generic Exception
        if (id == null) {
            throw new Exception("ID cannot be null");
        }

        userRepository.deleteById(id);
    }

    // DEFECT: Resource leak - InputStream not closed
    public String readUserData(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            // DEFECT: Not closing the stream
            byte[] data = new byte[1024];
            fis.read(data);
            return new String(data);
        } catch (Exception e) {
            // DEFECT: Catching generic Exception
            // DEFECT: Returning null on error
            return null;
        }
    }

    // DEFECT: Method modifying parameter object
    public void updateUserStatus(User user) {
        // DEFECT: Modifying object passed as parameter
        user.setActive(!user.getActive());
        userRepository.save(user);
    }

    // DEFECT: Using Thread.sleep() in service method
    public void slowOperation() {
        try {
            Thread.sleep(5000); // DEFECT: Blocking operation
        } catch (InterruptedException e) {
            // DEFECT: Empty catch block
        }
    }

    // DEFECT: Hard-coded values
    public boolean isAdminUser(User user) {
        // DEFECT: Magic string comparison
        if (user.role.equals("ADMIN")) {
            return true;
        }
        return false;
    }

    // DEFECT: Inefficient string concatenation in loop
    public String generateUserReport(List<User> users) {
        String report = "";
        for (User user : users) {
            // DEFECT: String concatenation in loop
            report += user.getUsername() + ", ";
        }
        return report;
    }

    // DEFECT: Dead code - method never called
    private void unusedPrivateMethod() {
        System.out.println("This is never called");
    }

    // DEFECT: Method with too many parameters
    public User updateUser(Long id, String username, String password, String email, Boolean isActive, String role, Date createdAt) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setActive(isActive);
            user.role = role;
            user.setCreatedAt(createdAt);
            return userRepository.save(user);
        }
        return null;
    }
}
